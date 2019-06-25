package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.ArchiveMOSA;
import org.uma.jmetal.util.comparator.DominanceComparator;

import tools.MathFunction;

/**
 * Archive Multi-objective Simulated Annealing (AMOSA)
 * 
 * Bandyopadhyay, S., Saha, S., Maulik, U., & Deb, K. (2008). A simulated
 * annealing-based multiobjective optimization algorithm: AMOSA. Evolutionary
 * Computation, IEEE Transactions on, 12(3), 269-283.
 *
 * @author José Carlos Soto Monterrubio <soto190@gmail.com>
 * @version 1.0
 *
 */

public class AMOSA<S extends Solution<?>> extends MOSA<S> {

	protected static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();
	protected ArchiveMOSA<S> archive;
	protected int hardLimit;
	protected int softLimit;
	protected double[] objectiveRange;

	protected ArrayList<S> new_ptDomBy;
	protected ArrayList<S> new_ptDomTo;

	private double amountOfDomination;

	protected S currentS;
	protected S newS;

	protected double[] historicalEnergy;
	protected double[] historicalTemperature;
	protected double[] historicalSlope;
	protected double[] historicalAverage;
	protected double[] historicalAccumulative;

	protected ArrayList<S> historicalSolution;

	protected S idealSolution, nonIdealSolution;

	protected int totalAccepted;
	protected S bestLocalSolution;
	protected S worstLocalSolution;

	protected S bestGlobalSolution;
	protected S worstGlobalSolution;

	protected double bestLocalEnergy;
	protected double worstLocalEnergy;

	protected double bestGlobalEnergy = Double.MAX_VALUE;
	protected double worstGlobalEnergy = Double.MIN_VALUE;

	protected double bestGlobalAvg = Double.MAX_VALUE;
	protected double worstGlobalAvg = Double.MIN_NORMAL;

	protected double bestAccumulativeEnergy;
	protected double worstAccumulativeEnergy;

	protected int totalAcceptedInMetropolis;
	protected double totalEnergyInMetropolis;
	protected double averageEnergyInMetropolis;

	protected int step = 0;

	protected double deltaAmountDominance;

	public AMOSA(Problem<S> problem, int populationSize, double initialTemperature, double finalTemperature,
			double alpha, int metropolisLength, MutationOperator<S> mutation, AcceptanceCriteria<S> criteria,
			int hardLimit, int softLimit) {
		super(problem, populationSize, initialTemperature, finalTemperature, metropolisLength, alpha, mutation,
				criteria);
		this.hardLimit = hardLimit;
		this.softLimit = softLimit;
		archive = new ArchiveMOSA<S>(hardLimit, softLimit);
		new_ptDomBy = new ArrayList<S>(softLimit);
		new_ptDomTo = new ArrayList<S>(softLimit);
		objectiveRange = new double[problem.getNumberOfObjectives()];

		idealSolution = problem.createSolution();
		problem.evaluate(idealSolution);

		nonIdealSolution = problem.createSolution();
		problem.evaluate(nonIdealSolution);

	}

	@Override
	protected void initProgress() {
		iterations = 1;
		this.temperature = this.getInitialTemperature();

		int totalSteps = (int) ((Math.log(this.getFinalTemperature()) - Math.log(this.getInitialTemperature()))
				/ Math.log(this.getAlpha())) + 1;

		historicalEnergy = new double[totalSteps];
		historicalTemperature = new double[totalSteps];
		historicalSlope = new double[totalSteps];
		historicalAverage = new double[totalSteps];
		historicalAccumulative = new double[totalSteps];
		historicalSolution = new ArrayList<S>(totalSteps);
		createInitialPopulation();

	}

	@Override
	protected List<S> createInitialPopulation() {

		for (int i = 0; i < populationSize; i++) {
			S newIndividual = problem.createSolution();
			archive.add(newIndividual);
			for (int nObj = 0; nObj < objectiveRange.length; nObj++)
				if (newIndividual.getObjective(nObj) > objectiveRange[nObj])
					objectiveRange[nObj] = newIndividual.getObjective(nObj);
		}
		archive.clustering();

		return archive.getSolutionList();
	}

	@Override
	protected S selectRandomSolution() {
		return archive.get(randomGenerator.nextInt(0, archive.size() - 1));
	}

	public S getSolution(int idxSolution) {
		return this.archive.get(idxSolution);
	}

	@Override
	protected void updatePopulation(S candidate) {

		int timesRepeated = 0;
		boolean isRepeated = false;

		for (int sol = 0; sol < archive.size() && !isRepeated; sol++) {
			
			int nObEq = 0;
			S s = archive.get(sol);

			for (int obj = 0; obj < problem.getNumberOfObjectives() && nObEq == obj; obj++)
				if (s.getObjective(obj) == candidate.getObjective(obj))
					nObEq++;

			if (nObEq == problem.getNumberOfObjectives())
				isRepeated = true;
		}

		if (!isRepeated) {
			this.archive.add(candidate);
			if (this.archive.size() > this.archive.getSoftLimit())
				this.archive.clustering();
		}
	}

	protected void updateRange(S solution) {
		for (int nObj = 0; nObj < objectiveRange.length; nObj++)
			if (solution.getObjective(nObj) > objectiveRange[nObj])
				objectiveRange[nObj] = solution.getObjective(nObj);
	}

	protected void resetLocalValues() {

		totalEnergyInMetropolis = 0;
		totalAcceptedInMetropolis = 0;
		averageEnergyInMetropolis = 0;

		bestLocalEnergy = Double.MAX_VALUE;
		worstLocalEnergy = Double.MIN_VALUE;

		bestLocalSolution = null;
		worstLocalSolution = null;
		deltaAmountDominance = 0.0;
		amountOfDomination = 0.0;
	}

	protected void updateProgress() {

		if (currentS == newS) {
			updateBestAndWorstGlobalSolutions(newS, deltaAmountDominance);
			updateBestAndWorstLocalSolutions(newS, deltaAmountDominance);
			totalEnergyInMetropolis += deltaAmountDominance;
			totalAcceptedInMetropolis++;
		}

		iterations++;
	}

	protected void updateHistoricalStatistics() {
		historicalEnergy[step] = bestLocalEnergy;
		historicalTemperature[step] = this.getTemperature();
		historicalAccumulative[step] = totalEnergyInMetropolis;
		historicalSolution.add(currentS);

		if (step > 0) {

			S last = historicalSolution.get(step - 1);
			S next = historicalSolution.get(step);
			double e1 = 1;
			double e2 = 1;
			for (int nOnj = 0; nOnj < problem.getNumberOfObjectives(); nOnj++) {
				e1 *= last.getObjective(nOnj) / objectiveRange[nOnj];
				e2 *= next.getObjective(nOnj) / objectiveRange[nOnj];
			}
			historicalEnergy[step - 1] = e1;
			historicalEnergy[step] = e2;
			historicalSlope[step] = MathFunction.slope( /****/
					historicalEnergy[step], historicalTemperature[step] / this.getInitialTemperature(), /****/
					historicalEnergy[step - 1], historicalTemperature[step - 1] / this.getInitialTemperature()); /****/

		}
		/** Used in fuzzy. **/
		averageEnergyInMetropolis = totalEnergyInMetropolis / totalAcceptedInMetropolis;

		if (averageEnergyInMetropolis < bestGlobalAvg)
			bestGlobalAvg = averageEnergyInMetropolis;
		if (averageEnergyInMetropolis > worstGlobalAvg)
			worstGlobalAvg = averageEnergyInMetropolis;

		if (totalEnergyInMetropolis < bestAccumulativeEnergy)
			bestAccumulativeEnergy = totalEnergyInMetropolis;
		if (totalEnergyInMetropolis > bestAccumulativeEnergy)
			bestAccumulativeEnergy = totalEnergyInMetropolis;

		historicalAverage[step] = averageEnergyInMetropolis;
		totalAccepted += totalAcceptedInMetropolis;

		step++;
	}

	protected void updateBestAndWorstGlobalSolutions(S solution, double deltaAmount) {
		if (deltaAmount < bestGlobalEnergy) {
			bestGlobalEnergy = deltaAmount;
			bestGlobalSolution = solution;
		}

		if (deltaAmount > worstGlobalEnergy) {
			worstGlobalEnergy = deltaAmount;
			worstGlobalSolution = solution;
		}

		for (int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {
			if (solution.getObjective(obj) < idealSolution.getObjective(obj))
				idealSolution.setObjective(obj, solution.getObjective(obj));
			if (solution.getObjective(obj) > nonIdealSolution.getObjective(obj))
				nonIdealSolution.setObjective(obj, solution.getObjective(obj));

		}

	}

	protected void updateBestAndWorstLocalSolutions(S solution, double deltaAmount) {
		if (deltaAmount < bestLocalEnergy) {
			bestLocalEnergy = deltaAmount;
			bestLocalSolution = solution;
		}

		if (deltaAmount > worstLocalEnergy) {
			worstLocalEnergy = deltaAmount;
			worstLocalSolution = solution;
		}

	}

	/**
	 * Computes the domination status from the given solution
	 * 
	 * @param newPt
	 * @return an array with three values. [0] indicates the total of solutions
	 *         which dominate to new_pt in the Archive. [1] indicates the total
	 *         of solutions which are non-dominated to new_pt in the Archive.[2]
	 *         indicates the total of solutions which the new_pt dominates in
	 *         the Archive. Also updates two arrayLists.
	 */
	private int[] dominationStatus(S newPt) {
		new_ptDomBy.clear();
		new_ptDomTo.clear();
		amountOfDomination = 0;
		int[] dominationStatus = new int[3];
		int domination = 0;

		for (S sol : archive.getSolutionList()) {
			domination = DOMINANCE_COMPARATOR.compare(newPt, sol);
			if (domination == 1) {
				dominationStatus[0]++;
				amountOfDomination += amountOfDomination(newPt, sol);
				new_ptDomBy.add(sol);
			} else if (domination == 0)
				dominationStatus[1]++;
			else if (domination == -1) {
				dominationStatus[2]++;
				new_ptDomTo.add(sol);
			}
		}
		return dominationStatus;
	}

	protected double amountOfDomination(S solutionA, S solutionB) {
		double amount = 1;
		boolean thereIsDifference = false;

		for (int nObj = 0; nObj < solutionA.getNumberOfObjectives(); nObj++) {
			double difference = Math.abs(solutionA.getObjective(nObj) - solutionB.getObjective(nObj));
			if (difference > 0) {
				amount *= difference / objectiveRange[nObj];
				thereIsDifference = true;
			}
		}
		if (!thereIsDifference)
			amount = 0.0;
		return amount;
	}

	protected boolean acceptanceCriteria(double energyDiference) {
		return 1 / (1 + Math.exp(energyDiference)) < randomGenerator.nextDouble();
	}

	protected S caseI(S new_pt, S current_pt) {
		/* current_pt dominates new_pt */
		int kDominances = 0;
		double avgDom = 0.0;
		for (S sol : archive.getSolutionList())
			if (DOMINANCE_COMPARATOR.compare(new_pt, sol) == 1) {
				avgDom += amountOfDomination(new_pt, sol);
				kDominances++;
			}

		avgDom = (avgDom + amountOfDomination(new_pt, current_pt)) / (kDominances + 1);

		/* Set new_pt as current_pt wiht probability = prob. */
		if (acceptanceCriteria(avgDom / temperature)) {
			// updatePopulation(new_pt);
			deltaAmountDominance = avgDom;
			return new_pt;
		}

		return current_pt;
	}

	protected S caseII(S new_pt, S current_pt) {
		int[] dominationStatus = dominationStatus(new_pt);
		double avgDom = 0;

		if (new_ptDomBy.size() > 0)
			avgDom = amountOfDomination / new_ptDomBy.size();
		/*
		 * Case 2 (a): new_pt is dominated by k(k>= 1) points in the Archive.
		 */
		if (dominationStatus[0] > 0) {

			/* Set new_pt as current_pt with probability = prob. */
			if (acceptanceCriteria(avgDom / temperature)) {
				deltaAmountDominance = avgDom;
				current_pt = new_pt;
			}
		}

		/*
		 * Case 2 (b): new_pt is non-dominating with respect to all the points
		 * in the Archive.
		 */
		else if (dominationStatus[0] == 0 && dominationStatus[2] == 0) {

			/* Set new_pt as current_pt and add new_pt to the Archive. */
			updatePopulation(new_pt);
			deltaAmountDominance = avgDom;
			current_pt = new_pt;
		}

		/* Case 2 (c): new_pt dominates k, (k >= 1) points of the Archive. */
		else if (dominationStatus[2] > 0) {

			/*
			 * Set new_pt as current_pt and add it to the Archive. Removes all
			 * the k dominated points in the Archive.
			 */
			for (S sol : new_ptDomTo)
				archive.remove(sol);

			updatePopulation(new_pt);
			deltaAmountDominance = avgDom;
			current_pt = new_pt;
		}

		return current_pt;
	}

	protected S caseIII(S new_pt, S current_pt) {
		int[] dominationStatus = dominationStatus(new_pt);
		double amount = amountOfDomination(new_pt, current_pt);

		/*
		 * Case 3 (a): new_pt is dominated by k (k >= 1) points in the Archive.
		 */
		if (dominationStatus[0] > 0) {

			double minDom = Double.MAX_VALUE;
			S minS = null;
			for (S solInArch : new_ptDomBy) {
				double amountOfDomination = amountOfDomination(new_pt, solInArch);
				if (amountOfDomination < minDom) {
					minDom = amountOfDomination;
					minS = solInArch;
				}
			}

			/*
			 * Set point of the archive which corresponds to minDom as
			 * current_pt with probability = prob.
			 */
			if (acceptanceCriteria(-minDom)) {
				current_pt = minS;
				deltaAmountDominance = minDom;
			} else {
				// updatePopulation(new_pt);
				deltaAmountDominance = amount;
				current_pt = new_pt;
			}
		}

		/*
		 * Case 3 (b): new_pt is non-dominating with respect to all the points
		 * in the Archive.
		 */
		else if (dominationStatus[0] == 0 && dominationStatus[2] == 0) {

			/* Set new_pt as current_pt, remove it from the Archive. */
			archive.remove(current_pt);
			updatePopulation(new_pt);

			deltaAmountDominance = amount;
			current_pt = new_pt;
		}

		/*
		 * Case 3 (c): new_pt dominates k other points in the Archive.
		 */
		else if (dominationStatus[2] > 0) {

			/*
			 * Set new_pt as current_pt and add it to the Archive. Remove all
			 * the k dominated points from the Archive.
			 */
			for (S sol : new_ptDomTo)
				archive.remove(sol);

			updatePopulation(new_pt);
			deltaAmountDominance = amount;
			current_pt = new_pt;
		}
		return current_pt;
	}

	@Override
	public List<S> getResult() {
		return getNonDominatedSolutions(archive.getSolutionList());
	}

	public void printHistoricalData() {

		for (int s = 0; s < historicalEnergy.length && historicalTemperature[s] > 0; s++)
			System.out.printf(Locale.ENGLISH, "T:%6.6f E:%6.6f S:%6.6f M:%6.6f obj1: %6.6f obj2:%6.6f\n",
					historicalTemperature[s], historicalEnergy[s], historicalSlope[s], historicalAverage[s],
					historicalSolution.get(s).getObjective(0), historicalSolution.get(s).getObjective(1));

		System.out.println("Total steps: " + step);

	}

	public void run() {
		initProgress();

		currentS = selectRandomSolution();

		while (!isStopConditionReached()) {
			resetLocalValues();
			currentS = selectRandomSolution();

			while (metropolisCycle()) {
				newS = perturbation(currentS);
				updateRange(newS);

				deltaAmountDominance = amountOfDomination(newS, currentS);
				updateBestAndWorstGlobalSolutions(newS, deltaAmountDominance);

				int flagDominance = DOMINANCE_COMPARATOR.compare(newS, currentS);

				if (flagDominance == 1) /* 1: current_pt dominates new_pt */
					currentS = caseI(newS, currentS);
				else if (flagDominance == 0) /* 0: No dominance */
					currentS = caseII(newS, currentS);
				else if (flagDominance == -1) /*-1: new_pt dominates current_pt */
					currentS = caseIII(newS, currentS);

				// System.out.print(" " + deltaAmountDominance + "\n");

				updateProgress();
			}
			//updateHistoricalStatistics();
			coolingScheme();
		}
		if (archive.size() > archive.getSoftLimit())
			archive.clustering();
		// printHistoricalData();

		System.out.println("Total Accepted: " + totalAccepted);

	}

}

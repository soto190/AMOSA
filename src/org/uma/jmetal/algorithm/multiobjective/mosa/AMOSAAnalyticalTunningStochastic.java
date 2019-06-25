package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;

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

public class AMOSAAnalyticalTunningStochastic<S extends IntegerSolution> extends AMOSA<S> {

	private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();
	protected int maxLength;
	protected double beta;
	protected double maxDeterioration;
	protected double minDeterioration;
	protected double explorationLevel;
	protected CrossoverOperator<S> crossover;
	boolean stochasticEquilibriumReached = false;

	protected S s2;

	public AMOSAAnalyticalTunningStochastic(Problem<S> problem, int populationSize, double alpha,
			double maxDeterioration, double minDeterioration, double neighborhoodExploration,
			MutationOperator<S> mutation, CrossoverOperator<S> crossover, int hardLimit, int softLimit) {
		super(problem, populationSize, 1000, 0.01, alpha, 100, mutation, null, hardLimit, softLimit);
		this.crossover = crossover;
		this.maxDeterioration = maxDeterioration;
		this.minDeterioration = minDeterioration;
		this.explorationLevel = neighborhoodExploration;
		this.historicalEnergy = new double[1024];

	}

	@Override
	protected void initProgress() {
		iterations = 1;
		createInitialPopulation();
		evaluatePopulation(this.archive.getSolutionList());

		currentS = selectRandomSolution();

		System.out.println("Initial temperature: " + this.getInitialTemperature());
		System.out.println("Final Temperature: " + this.getFinalTemperature());

		S newIndividual = problem.createSolution();
		for (int nV = 0; nV < problem.getNumberOfVariables(); nV++) {
			newIndividual.setVariableValue(nV, 0);
		}

		problem.evaluate(newIndividual);
		/**
		 * Mini SA
		 */
		/*
		 * while (!isStopConditionReached()) { resetLocalValues(); currentS =
		 * selectRandomSolution();
		 * 
		 * while (metropolisCycle()) { newS = perturbation(currentS);
		 * updateRange(newS);
		 * 
		 * deltaAmountDominance = amountOfDomination(newS, currentS);
		 * 
		 * int flagDominance = DOMINANCE_COMPARATOR.compare(newS, currentS);
		 * 
		 * if (flagDominance == 1)
		 **/ /* 1: current_pt dominates new_pt */
		/**
		 * currentS = caseI(newS, currentS); else if (flagDominance == 0)
		 **/ /* 0: No dominance */
		/**
		 * currentS = caseII(newS, currentS); else if (flagDominance == -1)
		 **//*-1: new_pt dominates current_pt */
		/**
		 * currentS = caseIII(newS, currentS);
		 * 
		 * } coolingScheme(); }
		 **/
		this.setMetropolisLength(1);

		double[] energyCost = new double[this.populationSize];

		for (int i = 0; i < this.populationSize - 1; i++) {
			S solutionA = this.getSolution(i);
			// S solutionB;
			// for (int j = 1; j < this.populationSize; j++) {
			// solutionB = this.getSolution(j);
			// energyCost[i] = amountOfDomination(solutionA, solutionB);
			// }

			for (int nObj = 0; nObj < this.problem.getNumberOfObjectives(); nObj++)
				energyCost[i] += (this.randomGenerator.nextDouble() * solutionA.getObjective(nObj));
		}

		Arrays.sort(energyCost);

		/**
		 * The number of variables is the neighborhood size because we are only
		 * to explore a change in each variable
		 */
		double neightborhoodSize = problem.getNumberOfVariables();

		double maxDeterioration = energyCost[energyCost.length - 1] - energyCost[0];
		double minDeterioration = energyCost[1] - energyCost[0];

		this.setIntialTemperature(-maxDeterioration / Math.log(this.maxDeterioration));
		this.setFinalTemperature(-minDeterioration / Math.log(this.minDeterioration));

		double totalSteps = (Math.log(this.getFinalTemperature()) - Math.log(this.getInitialTemperature()))
				/ Math.log(this.getAlpha());

		this.maxLength = (int) (neightborhoodSize * explorationLevel);
		this.beta = Math.exp(((Math.log(this.maxLength) - Math.log(this.getMetropolisLength())) / totalSteps));
		this.temperature = this.getInitialTemperature();

		int tempSize = ((int) totalSteps + 1) * 3;
		historicalEnergy = new double[tempSize];
		historicalTemperature = new double[tempSize];
		historicalSlope = new double[tempSize];
		historicalAverage = new double[tempSize];
		historicalAccumulative = new double[tempSize];
		historicalSolution = new ArrayList<S>((int) totalSteps);

		System.out.println("Initial temperature: " + this.getInitialTemperature());
		System.out.println("Final Temperature: " + this.getFinalTemperature());
		System.out.println("Exploration level: " + this.getExplorationLevel());
		System.out.println("Neighborhood size: " + neightborhoodSize);
		System.out.println("Beta: " + this.getBeta());
		System.out.println("Alpha: " + this.getAlpha());
		System.out.println("Max Length: " + this.getMaxLength());
		System.out.println("Total steps;" + totalSteps);

	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getBeta() {
		return this.beta;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public void setNeighborhoodExploration(double neighborhoodExploration) {
		this.explorationLevel = neighborhoodExploration;
	}

	public double getExplorationLevel() {
		return this.explorationLevel;
	}

	/**
	 * TODO delete this method after the tests.
	 */
	public void printMOSAAnalyticalProgress() {
		System.out.println("Temperature: " + String.format("%16.6f", this.getTemperature()) + " Metropolis length: "
				+ String.format("%16.6f", this.getMetropolisLength()));
	}

	protected boolean isEqualInObjectives(S current, S newS) {

		boolean isEqual = true;
		for (int nObj = 0; nObj < problem.getNumberOfObjectives() && isEqual == true; nObj++)
			if (current.getObjective(nObj) != newS.getObjective(nObj))
				isEqual = false;

		return isEqual;
	}

	protected boolean isStochasticEquilibriumReached() {
		return historicalSlope[step] <= 0.01 && historicalSlope[step] >= -0.01;
	}

	@Override
	protected boolean metropolisCycle() {
		if (metropolis++ <= this.getMetropolisLength())
			return true;

		this.setMetropolisLength(this.beta * this.getMetropolisLength());
		metropolis = 0;
		return false;
	}

	public S perturbation(S solution) {
		S candidate = ((S) solution.copy());
		S parent = this.selectRandomSolution();
		ArrayList<S> parents = new ArrayList<S>();
		parents.add(candidate);
		parents.add(parent);

		ArrayList<S> offspring = new ArrayList<S>();
		offspring = (ArrayList<S>) this.crossover.execute(parents);
		S offspring1 = this.mutationOperator.execute(offspring.get(0));
		S offspring2 = this.mutationOperator.execute(offspring.get(1));
		this.problem.evaluate(offspring1);
		this.problem.evaluate(offspring2);

		return offspring2;
	}

	public void run() {
		initProgress();

		currentS = selectRandomSolution();
		int reannealings = 0;
		while (isStochasticEquilibriumReached() && reannealings++ < 2) {
			System.out.println("Annealing: " + reannealings);
			if (reannealings > 1)
				this.setTemperature(this.getInitialTemperature() * 0.75);

			while (!isStopConditionReached()) {
				resetLocalValues();
				if (step % 20 == 0)
					System.out.println(this.getTemperature());

				currentS = selectRandomSolution();

				while (metropolisCycle()) {

					newS = perturbation(currentS);

					// while (isEqualInObjectives(currentS, newS))
					newS = perturbation(currentS);

					updateRange(newS);
					deltaAmountDominance = amountOfDomination(newS, currentS);
					updateBestAndWorstGlobalSolutions(newS, deltaAmountDominance);

					int flagDominance = DOMINANCE_COMPARATOR.compare(newS, currentS);

					if (flagDominance == 1) /* 1: current dominates new */
						currentS = caseI(newS, currentS);
					else if (flagDominance == 0) /* 0: No dominance */
						currentS = caseII(newS, currentS);
					else if (flagDominance == -1) /*-1: new dominates current */
						currentS = caseIII(newS, currentS);

					updateProgress();
				}

				updateHistoricalStatistics();
				coolingScheme();
			}

			if (archive.size() > archive.getSoftLimit())
				archive.clustering();
		}

		// printHistoricalData();
		System.out.println(
				"Non ideal solution: " + nonIdealSolution.getObjective(0) + " " + nonIdealSolution.getObjective(1));
		System.out.println("Ideal solution: " + idealSolution.getObjective(0) + " " + idealSolution.getObjective(1));

		System.out.println("Total Accepted: " + totalAccepted);
	}
}

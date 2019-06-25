package org.uma.jmetal.algorithm.multiobjective.mosa;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author J. Carlos Soto Monterrubio <soto190@gmail.com>
 */
public class GeneticMOSA<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
	protected final int maxIterations;
	protected final int populationSize;

	protected final Problem<S> problem;

	protected final SolutionListEvaluator<S> evaluator;

	protected int iterations;

	protected double initialTemperature;
	protected double finalTemperature;
	protected double alpha;
	protected double temperature;
	protected int metropolisLength;
	protected int metropolis;
	


	protected AcceptanceCriteria<S> criteria;

	protected JMetalRandom rndGenerator;

	protected double[] objectiveRange;
	private ArrayList<S> new_ptDomBy;
	private ArrayList<S> new_ptDomTo;
	private double amountOfDomination;
	protected static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();


	/**
	 * Constructor
	 */
	public GeneticMOSA(Problem<S> problem, int maxIterations, int populationSize,
			CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
			SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
		super(problem);
		this.problem = problem;
		this.maxIterations = maxIterations;
		this.populationSize = populationSize;

		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
		this.selectionOperator = selectionOperator;

		this.evaluator = evaluator;
		this.rndGenerator = JMetalRandom.getInstance();
	}

	public GeneticMOSA(Problem<S> problem, int maxIterations, int populationSize, double initialTemperature,
			double finalTemperaturem, double alpha, int metropolisLength, CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator,
			SolutionListEvaluator<S> evaluator) {
		super(problem);
		this.problem = problem;
		this.maxIterations = maxIterations;
		this.populationSize = populationSize;

		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
		this.selectionOperator = selectionOperator;
		this.evaluator = evaluator;

		this.finalTemperature = finalTemperaturem;
		this.initialTemperature = initialTemperature;
		this.metropolisLength = metropolisLength;
		this.alpha = alpha;

		criteria = new AcceptanceCriteria<S>();

		this.objectiveRange = new double[problem.getNumberOfObjectives()];
		this.rndGenerator = JMetalRandom.getInstance();

	}

	public double getInitialTemperature() {
		return initialTemperature;
	}

	public void setInitialTemperature(double initialTemperature) {
		this.initialTemperature = initialTemperature;
	}

	public double getFinalTemperature() {
		return finalTemperature;
	}

	public void setFinalTemperature(double finalTemperature) {
		this.finalTemperature = finalTemperature;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setMetropolisLength(int metropolisLength) {
		this.metropolisLength = metropolisLength;
	}

	private int getMetropolisLength() {
		return this.metropolisLength;
	}

	private double getTemperature() {
		return this.temperature;
	}

	@Override
	protected void initProgress() {
		this.temperature = initialTemperature;
		iterations = 1;
	}

	@Override
	protected void updateProgress() {
		iterations++;
	}

	@Override
	protected boolean isStoppingConditionReached() {
		// return iterations >= maxIterations;
		return this.temperature <= this.finalTemperature;
	}

	@Override
	protected List<S> createInitialPopulation() {
		List<S> population = new ArrayList<S>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			S newIndividual = problem.createSolution();
			population.add(newIndividual);
		}
		return population;
	}

	@Override
	protected List<S> evaluatePopulation(List<S> population) {
		population = evaluator.evaluate(population, problem);

		for (S sol : population)
			updateRange(sol);

		return population;
	}

	@Override
	protected List<S> selection(List<S> population) {
		List<S> matingPopulation = new ArrayList<S>(population.size());
		for (int i = 0; i < populationSize; i++) {
			S solution = selectionOperator.execute(population);
			matingPopulation.add(solution);
		}

		return matingPopulation;
	}

	@Override
	protected List<S> reproduction(List<S> population) {
		List<S> offspringPopulation = new ArrayList<S>(populationSize);
		for (int i = 0; i < populationSize; i += 2) {
			List<S> parents = new ArrayList<S>(2);
			parents.add(population.get(i));
			parents.add(population.get(i + 1));

			List<S> offspring = crossoverOperator.execute(parents);

			mutationOperator.execute(offspring.get(0));
			mutationOperator.execute(offspring.get(1));

			offspringPopulation.add(offspring.get(0));
			offspringPopulation.add(offspring.get(1));
		}
		return offspringPopulation;
	}

	@Override
	protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
		List<S> jointPopulation = new ArrayList<S>();

		
		
		for (int i = 0; i < populationSize; i++) {
			dominationStatus(offspringPopulation.get(i));
			double avgDom = amountOfDomination / new_ptDomBy.size();

			if(new_ptDomTo.size() > 0 )
				jointPopulation.add(offspringPopulation.get(i));
			else if (1 / (1 + Math.exp(avgDom * temperature)) < rndGenerator.nextDouble()) {
				jointPopulation.add(offspringPopulation.get(i));
			}else
				jointPopulation.add(population.get(i));
							
		}
		
		
		/**
		for (int i = 0; i < populationSize; i++) {
			int[] domNew = dominationStatus(offspringPopulation.get(i));
			int[] domPop = dominationStatus(population.get(i));

			if (criteria.boltzman(amountOfDomination(offspringPopulation.get(i), population.get(i)),
					temperature) < rndGenerator.nextDouble())
				jointPopulation.add(offspringPopulation.get(i));
			else
				jointPopulation.add(population.get(i));

		}
		**/

		/**
		 * for (int i = 0; i < populationSize; i += 2) {
		 * 
		 * if (acceptanceCriteria(offspringPopulation.get(i), population.get(i))
		 * || acceptanceCriteria(offspringPopulation.get(i), population.get(i +
		 * 1))) jointPopulation.add(offspringPopulation.get(i));
		 * 
		 * else jointPopulation.add(population.get(i));
		 * 
		 * if (acceptanceCriteria(offspringPopulation.get(i + 1),
		 * population.get(i)) || acceptanceCriteria(offspringPopulation.get(i +
		 * 1), population.get(i + 1)))
		 * jointPopulation.add(offspringPopulation.get(i + 1)); else
		 * jointPopulation.add(population.get(i + 1)); }
		 **/

		// jointPopulation.addAll(population);
		// jointPopulation.addAll(offspringPopulation);

		// Ranking<S> ranking = computeRanking(jointPopulation);
		// return crowdingDistanceSelection(ranking);

		return jointPopulation;
	}

	protected boolean acceptanceCriteria(S candidate, S current) {
		double[] weight = new double[] { 0.1, 0.1 };

		criteria.setWeight(weight);
		return criteria.weak(candidate, current, temperature) < rndGenerator.nextDouble();
	}

	@Override
	public List<S> getResult() {
		return getNonDominatedSolutions(getPopulation());
	}

	protected Ranking<S> computeRanking(List<S> solutionList) {
		Ranking<S> ranking = new DominanceRanking<S>();
		ranking.computeRanking(solutionList);

		return ranking;
	}

	protected List<S> crowdingDistanceSelection(Ranking<S> ranking) {
		CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
		List<S> population = new ArrayList<S>(populationSize);
		int rankingIndex = 0;
		while (populationIsNotFull(population)) {
			if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
				addRankedSolutionsToPopulation(ranking, rankingIndex, population);
				rankingIndex++;
			} else {
				crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
				addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
			}
		}

		return population;
	}

	protected boolean populationIsNotFull(List<S> population) {
		return population.size() < populationSize;
	}

	protected boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, List<S> population) {
		return ranking.getSubfront(rank).size() < (populationSize - population.size());
	}

	protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
		List<S> front;

		front = ranking.getSubfront(rank);

		for (S solution : front) {
			population.add(solution);
		}
	}

	protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
		List<S> currentRankedFront = ranking.getSubfront(rank);

		Collections.sort(currentRankedFront, new CrowdingDistanceComparator<S>());

		int i = 0;
		while (population.size() < populationSize) {
			population.add(currentRankedFront.get(i));
			i++;
		}
	}

	protected List<S> getNonDominatedSolutions(List<S> solutionList) {
		return SolutionListUtils.getNondominatedSolutions(solutionList);
	}

	protected void coolingScheme() {
		this.temperature *= this.alpha;
	}

	protected boolean metropolisCycle() {
		if (this.metropolis++ < this.metropolisLength)
			return true;

		this.metropolis = 0;
		return false;
	}

	private int[] dominationStatus(S new_pt) {
		new_ptDomBy = new ArrayList<S>(100);
		new_ptDomTo = new ArrayList<S>(100);
		amountOfDomination = 0;
		int[] dominationStatus = new int[3];
		int domination = 0;

		for (S sol : getPopulation()) {
			domination = DOMINANCE_COMPARATOR.compare(new_pt, sol);
			if (domination == 1) {
				dominationStatus[0]++;
				amountOfDomination += amountOfDomination(new_pt, sol);
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

		for (int nObj = 0; nObj < solutionA.getNumberOfObjectives(); nObj++)
			amount *= Math.abs((solutionA.getObjective(nObj) - solutionB.getObjective(nObj) / objectiveRange[nObj]));

		return amount;
	}

	protected void updateRange(S solution) {
		for (int nObj = 0; nObj < objectiveRange.length; nObj++)
			if (solution.getObjective(nObj) > objectiveRange[nObj])
				objectiveRange[nObj] = solution.getObjective(nObj);
	}

	/**
	 * TODO delete this method after the tests.
	 */
	public void printProgress() {
		System.out.println("Temperature: " + String.format("%16.6f", this.getTemperature()) + " Metropolis length: "
				+ String.format("%3d", this.getMetropolisLength()));
	}

	@Override
	public void run() {
		List<S> offspringPopulation;
		List<S> matingPopulation;

		setPopulation(createInitialPopulation());
		setPopulation(evaluatePopulation(getPopulation()));
		initProgress();
		while (!isStoppingConditionReached()) {
			while (metropolisCycle()) {
				matingPopulation = selection(getPopulation());
				offspringPopulation = reproduction(matingPopulation);
				offspringPopulation = evaluatePopulation(offspringPopulation);
				setPopulation(replacement(getPopulation(), offspringPopulation));
				updateProgress();
			}
			coolingScheme();
			printProgress();
		}
		System.out.println("The end...");
	}

	public int getIterations() {
		return this.iterations;
	}

	public String getName() {
		return "GeneticMOSA";
	}

	public String getDescription() {
		return "Hibrid with MOSA and genetic";
	}
}

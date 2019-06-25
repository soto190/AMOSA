package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.util.Arrays;

import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * Multi-objective Simulated Annealing with analytical tunning. This algorithm is
 * based on Frausto-Solís, Juan, Héctor Sanvicente-Sánchez, and Froilín Imperial-Valenzuela. 
 * "ANDYMARK: an analytical method to establish dynamically the length of the markov 
 * chain in simulated annealing for the satisfiability problem." Simulated Evolution 
 * and Learning. Springer Berlin Heidelberg, 2006. 269-276.
 * 
 * @author José Carlos Soto Monterrubio
 * @version 0.1
 * 
 * 
 */

public class MOSAwithFuzzy<S extends Solution<?>> extends MOSA<S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int maxLength;
	protected double beta;
	protected double maxDeterioration;
	protected double minDeterioration;
	protected double explorationLevel;
	
	protected double deltaEaccepted;
	

	/**
	 * 
	 * @param problem Problem to solve
	 * @param populationSize Size of the population
	 * @param alpha Cooling scheme factor (between 0 and 1)
	 * @param maxDeterioration Probability to accept the maximum deterioration
	 * @param minDeterioration Probability to accept the minimum deterioration
	 * @param neighborhoodExploration Level of exploration to be done (between 1 and 4.6). 4.6 for 99% of exploration. 
	 * @param mutation the mutation used in the perturbation.
	 * @param criteria criteria used in the transition rule.
	 */

	public MOSAwithFuzzy(Problem<S> problem, int populationSize, double alpha, double maxDeterioration,
			double minDeterioration, double neighborhoodExploration, MutationOperator<S> mutation,
			AcceptanceCriteria<S> criteria) {
		super(problem, populationSize, 1000, 0.01, 100, alpha, mutation, criteria);
		this.maxDeterioration = maxDeterioration;
		this.minDeterioration = minDeterioration;
		this.explorationLevel = neighborhoodExploration;
	}



	@Override
	protected void initProgress() {
		iterations = 1;
		this.setMetropolisLength(1);

		double[] energyCost = new double[this.populationSize];

		for (int i = 0; i < this.populationSize; i++) {
			S solution = this.getSolution(i);
			for (int nObj = 0; nObj < this.problem.getNumberOfObjectives(); nObj++)
				energyCost[i] += (this.randomGenerator.nextDouble() * solution.getObjective(nObj));
		}
		Arrays.sort(energyCost);

		double neightborhoodSize = problem.getNumberOfVariables();

		double maxDeterioration = energyCost[energyCost.length - 1] - energyCost[0];
		double minDeterioration = energyCost[1] - energyCost[0];

		this.setIntialTemperature(-maxDeterioration / Math.log(this.maxDeterioration));
		this.setFinalTemperature(-minDeterioration / Math.log(this.minDeterioration));

		double n = (Math.log(this.getFinalTemperature()) - Math.log(this.getInitialTemperature()))
				/ Math.log(this.getAlpha());

		this.maxLength = (int) (neightborhoodSize * explorationLevel);
		this.beta = Math.exp(((Math.log(this.maxLength) - Math.log(this.getMetropolisLength())) / n));
		this.temperature = this.getInitialTemperature();

		System.out.println("Initial temperature: " + this.getInitialTemperature());
		System.out.println("Final Temperature: " + this.getFinalTemperature());
		System.out.println("Exploration level: " + this.getExplorationLevel());
		System.out.println("Neighborhood size: " + neightborhoodSize);
		System.out.println("Beta: " + this.getBeta());
		System.out.println("Alpha: " + this.getAlpha());
		System.out.println("Max Length: " + this.getMaxLength());

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

	@Override
	protected boolean metropolisCycle() {
		if (metropolis++ <= this.getMetropolisLength())
			return true;

		this.setMetropolisLength(this.beta * this.getMetropolisLength());
		this.printMOSAAnalyticalProgress();
		metropolis = 0;
		return false;
	}
	
	public void run() {

		this.population = createInitialPopulation();
		this.population = evaluatePopulation(this.population);
		initProgress();

		while (!isStopConditionReached()) {
			S current = selectRandomSolution();

			while (metropolisCycle()) {
				S candidate = perturbation(current);
				if (acceptanceCriteria(candidate, current))
					updatePopulation(candidate);
				updateProgress();
			}
			coolingScheme();
		}
	}

}

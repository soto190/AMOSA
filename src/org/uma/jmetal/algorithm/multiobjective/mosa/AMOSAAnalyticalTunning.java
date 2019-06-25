package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.ArchiveMOSA;
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

public class AMOSAAnalyticalTunning<S extends Solution<?>> extends AMOSA<S> {

	private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();
	protected int maxLength;
	protected double beta;
	protected double maxDeterioration;
	protected double minDeterioration;
	protected double explorationLevel;
	
	

	public AMOSAAnalyticalTunning(Problem<S> problem, int populationSize, double alpha, double maxDeterioration, double minDeterioration, double neighborhoodExploration,
			 MutationOperator<S> mutation,	int hardLimit, int softLimit) {
		
		super(problem, populationSize, 1000, 0.1, alpha, 100, mutation, null, hardLimit, softLimit);
		this.maxDeterioration = maxDeterioration;
		this.minDeterioration = minDeterioration;
		this.explorationLevel = neighborhoodExploration;

	}

	

	// protected S perturbation(S solution){
	//
	// S candidate = (S) solution.copy();
	// this.problem.evaluate(this.mutationOperator.execute(candidate));
	//
	// for (int i = 0; i < 5; i++) {
	// int dominance = DOMINANCE_COMPARATOR.compare(candidate, solution);
	// if(dominance == -1)
	// return candidate;
	// else
	// this.problem.evaluate(this.mutationOperator.execute(candidate));
	//
	// }
	//
	// return candidate;
	// }

	@Override
	protected void initProgress() {
		
		iterations = 1;

		createInitialPopulation();
		evaluatePopulation(this.archive.getSolutionList());
		this.setMetropolisLength(10);

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
		
		int steps = (int) (n + 1);
		historicalEnergy = new double[steps];
		historicalTemperature = new double[steps];
		historicalSlope = new double[steps];
		
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
		metropolis = 0;
		return false;
	}

}

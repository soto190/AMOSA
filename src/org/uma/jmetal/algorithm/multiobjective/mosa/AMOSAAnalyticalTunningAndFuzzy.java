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

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import fuzzyForSA.FuzzyForSA;

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

public class AMOSAAnalyticalTunningAndFuzzy<S extends Solution<?>> extends AMOSA<S> {

	private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();
	protected int maxLength;
	protected double beta;
	protected double maxDeterioration;
	protected double minDeterioration;
	protected double explorationLevel;

	protected double average;
	protected double maxDif;
	protected double minDif;
	protected double deltaAccepted;
	protected double mean;
	protected double variance;

	protected FuzzyForSA fuzzy;


	public AMOSAAnalyticalTunningAndFuzzy(Problem<S> problem, int populationSize, double alpha, double maxDeterioration,
			double minDeterioration, double neighborhoodExploration, MutationOperator<S> mutation, int hardLimit,
			int softLimit) {
		super(problem, populationSize, 0, 0, alpha, 0, mutation, null, hardLimit, softLimit);
		this.maxDeterioration = maxDeterioration;
		this.minDeterioration = minDeterioration;
		this.explorationLevel = neighborhoodExploration;

	}


	@Override
	protected void initProgress() {
		try {
			fuzzy = new FuzzyForSA();
		} catch (MWException e) {
			e.printStackTrace();
		}
		iterations = 1;
		averageEnergyInMetropolis = 0.0;

		createInitialPopulation();
		evaluatePopulation(this.archive.getSolutionList());
		this.setMetropolisLength(10);


		double neightborhoodSize = problem.getNumberOfVariables();
		this.maxLength = (int) (neightborhoodSize * explorationLevel);

		double[] energyCost = new double[this.populationSize];

		for (int i = 0; i < this.populationSize; i++) {
			S solution = this.getSolution(i);
			for (int nObj = 0; nObj < this.problem.getNumberOfObjectives(); nObj++)
				energyCost[i] += (this.randomGenerator.nextDouble() * solution.getObjective(nObj));
		}
		Arrays.sort(energyCost);

		double maxDeterioration = energyCost[energyCost.length - 1] - energyCost[0];
		double minDeterioration = energyCost[1] - energyCost[0];

		this.setIntialTemperature(-maxDeterioration / Math.log(this.maxDeterioration));
		this.setFinalTemperature(-minDeterioration / Math.log(this.minDeterioration));

		this.temperature = this.getInitialTemperature();

		double n = (Math.log(this.getFinalTemperature()) - Math.log(this.getInitialTemperature()))
				/ Math.log(this.getAlpha());

		this.beta = Math.exp(((Math.log(this.maxLength) - Math.log(this.getMetropolisLength())) / n));

		int steps = (int) (n + 1);
		historicalEnergy = new double[steps];
		historicalTemperature = new double[steps];
		historicalSlope = new double[steps];
		historicalAverage = new double[steps];
		historicalAccumulative = new double[steps];

		System.out.println("Initial temperature: " + this.getInitialTemperature());
		System.out.println("Final Temperature: " + this.getFinalTemperature());
		System.out.println("Exploration level: " + this.getExplorationLevel());
		System.out.println("Neighborhood size: " + neightborhoodSize);
		System.out.println("Beta: " + this.getBeta());
		System.out.println("Alpha: " + this.getAlpha());
		System.out.println("Max Length: " + this.getMaxLength());
		System.out.println("Total steps:" + n);

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

	@Override
	protected boolean metropolisCycle() {
		if (metropolis++ < this.getMetropolisLength())
			return true;
	
		double tmpMetropolis = this.getMetropolisLength() * (this.beta + getMetropolisFromFuzzy(averageEnergyInMetropolis));
		
		if(tmpMetropolis > this.maxLength)
			tmpMetropolis = this.maxLength;
			
		this.setMetropolisLength(tmpMetropolis);

		metropolis = 0;
		return false;
	}

	protected double getNormalizedTemperature() {

		return (this.temperature - this.getFinalTemperature())
				/ (this.getInitialTemperature() - this.getFinalTemperature());
	}

	protected double getNormalizedAmountDominance(double average) {
//		 return (average - this.bestGlobalAvg ) / (this.worstGlobalAvg - this.bestGlobalAvg);
		double avgTmp = average / (this.worstGlobalAvg);

		return avgTmp;

	}

	protected double getMetropolisFromFuzzy(double averageDominance) {

		double normTemp = getNormalizedTemperature();
		double normAvg = averageDominance;//getNormalizedAmountDominance(averageDominance);
		Object[] results;
		try {
			results = fuzzy.fuzzyForSA(1, new double[] { normTemp, normAvg });

			MWArray result = (MWNumericArray) results[0];
			double[][] total = (double[][]) result.toArray();

//			System.out.println(
//					"in: " + totalAcceptedInMetropolis + " " + temperature + "(" + normTemp + ") " + averageDominance
//							+ "(" + normAvg + ") " + total[0][0] + ": " + (this.getMetropolisLength() + total[0][0]));

			return total[0][0];

		} catch (MWException e) {
			e.printStackTrace();
		}

		return 0;
	}

}

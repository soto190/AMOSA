package org.uma.jmetal.algorithm.multiobjective.mosa;

import org.uma.jmetal.algorithm.impl.AbstractSimulatedAnnealing.TransitionRule;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;

public class MOSABuilder<S extends Solution<?>> implements AlgorithmBuilder<MOSA<S>> {
	public enum MOSAVariant {
		MOSA, MOSAAnalyticalTunning, AMOSA, AMOSAAnalyticalTunning, MOSAAnalyticalTunningAndFuzzy, AMOSAAnalyticalTunningStochastic, TEST
	}

	private final Problem<S> problem;
	private int populationSize;
	private double initialTemperature;
	private double finalTemperature;
	private double alpha;

	/**
	 * Analytical MOSA parameters.
	 */
	private double minDeterioration;
	private double maxDeterioration;
	private double explorationLevel;
	private int metropolisLength;
	private double[] weight;
	private double[] rRule;
	private double alphaRule;
	/**
	 * AMOSA archive parameters.
	 */
	private int hardLimit;
	private int softLimit;

	private CrossoverOperator<S> crossoverOperator;
	private MutationOperator<S> mutationOperator;
	private AcceptanceCriteria<S> criteria;

	private MOSAVariant variant;

	public MOSABuilder(Problem<S> problem, MutationOperator<S> mutationOperator) {
		this.problem = problem;
		this.variant = MOSAVariant.MOSA;
		this.populationSize = 100;
		this.initialTemperature = 10000;
		this.finalTemperature = 0.001;
		this.alpha = 0.90;
		this.metropolisLength = 100;

		this.maxDeterioration = 0.8;
		this.minDeterioration = 0.4;
		this.explorationLevel = 0.6;

		this.criteria = new AcceptanceCriteria<S>(TransitionRule.RandomCost);
		this.mutationOperator = mutationOperator;
	}

	public MOSABuilder<S> setCrossover(CrossoverOperator<S> crossover) {

		this.crossoverOperator = crossover;
		return this;
	}

	public MOSABuilder<S> setPopulationSize(int populationSize) {
		if (populationSize < 0)
			throw new JMetalException("Population size is negative: " + populationSize);

		this.populationSize = populationSize;

		return this;
	}

	public MOSABuilder<S> setInitialTemperature(double initialTemperature) {
		if (initialTemperature < 0)
			throw new JMetalException("Initial temperature is negative: " + initialTemperature);

		this.initialTemperature = initialTemperature;

		return this;
	}

	public MOSABuilder<S> setFinalTemperature(double finalTemperature) {
		if (finalTemperature < 0)
			throw new JMetalException("Final temperature is negative: " + finalTemperature);

		this.finalTemperature = finalTemperature;

		return this;
	}

	public MOSABuilder<S> setMetropolisLength(int metropolisLength) {
		if (metropolisLength < 0)
			throw new JMetalException("Metropolis length is negative: " + metropolisLength);

		this.metropolisLength = metropolisLength;

		return this;
	}

	public MOSABuilder<S> setAlpha(double alpha) {
		if (alpha < 0 || alpha > 1)
			throw new JMetalException("Alpha is not between 0 and 1: " + alpha);

		this.alpha = alpha;

		return this;
	}

	public MOSABuilder<S> setVariant(MOSAVariant variant) {
		this.variant = variant;

		return this;
	}

	public MOSABuilder<S> setAcceptanceCriteria(AcceptanceCriteria<S> criteria) {
		this.criteria = criteria;

		return this;
	}

	public MOSABuilder<S> setMinDeterioration(double minDeterioration) {
		this.minDeterioration = minDeterioration;

		return this;
	}

	public MOSABuilder<S> setMaxDeterioration(double maxDeterioration) {
		this.maxDeterioration = maxDeterioration;

		return this;
	}

	/**
	 * 
	 * @param explorationLevel
	 *            a value between 1 and 4.6 (4.6 for 99% of exploration).
	 * @return
	 */
	public MOSABuilder<S> setExplorationLevel(double explorationLevel) {
		this.explorationLevel = explorationLevel;

		return this;
	}

	public MOSABuilder<S> setHardLimit(int hardLimit) {
		this.hardLimit = hardLimit;
		return this;
	}

	public MOSABuilder<S> setSoftLimit(int SoftLimit) {
		this.softLimit = SoftLimit;
		return this;
	}

	public MOSA<S> build() {

		MOSA<S> algorithm = null;
		if (variant.equals(MOSAVariant.MOSA))
			algorithm = new MOSA<S>(problem, populationSize, initialTemperature, finalTemperature, metropolisLength,
					alpha, mutationOperator, criteria);

		else if (variant.equals(MOSAVariant.MOSAAnalyticalTunning))
			algorithm = new MOSAAnalyticalTunning<S>(problem, populationSize, alpha, maxDeterioration, minDeterioration,
					explorationLevel, mutationOperator, criteria);

		else if (variant.equals(MOSAVariant.AMOSA))
			algorithm = new AMOSA<S>(problem, populationSize, initialTemperature, finalTemperature, alpha,
					metropolisLength, mutationOperator, criteria, hardLimit, softLimit);

		else if (variant.equals(MOSAVariant.AMOSAAnalyticalTunning))
			algorithm = new AMOSAAnalyticalTunning<S>(problem, populationSize, alpha, maxDeterioration,
					minDeterioration, explorationLevel, mutationOperator, hardLimit, softLimit);

		else if (variant.equals(MOSAVariant.MOSAAnalyticalTunningAndFuzzy))
			algorithm = new AMOSAAnalyticalTunningAndFuzzy<S>(problem, populationSize, alpha, maxDeterioration,
					minDeterioration, explorationLevel, mutationOperator, hardLimit, softLimit);

		else if (variant.equals(MOSAVariant.AMOSAAnalyticalTunningStochastic))
			algorithm = new AMOSAAnalyticalTunningStochastic(problem, populationSize, alpha, maxDeterioration,
					minDeterioration, explorationLevel, mutationOperator, crossoverOperator, hardLimit, softLimit);

		else if (variant.equals(MOSAVariant.TEST))
			algorithm = new MOSATest<S>(problem, populationSize, alpha, maxDeterioration, minDeterioration,
					explorationLevel, mutationOperator,  criteria);

		return algorithm;
	}

}

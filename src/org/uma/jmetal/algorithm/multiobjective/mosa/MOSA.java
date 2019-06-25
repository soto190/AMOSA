package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.impl.AbstractSimulatedAnnealing;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.CoolingScheme;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;;

public class MOSA<S extends Solution<?>> extends AbstractSimulatedAnnealing<S, List<S>> {

	protected final int populationSize;

	protected final Problem<S> problem;
	protected int iterations;
	protected int metropolis;
	protected JMetalRandom randomGenerator;

	/**
	 * 
	 * @param problem
	 *            the problem to solve.
	 * @param populationSize
	 *            size of the population.
	 * @param initialTemperature
	 *            maximum temperature.
	 * @param finalTemperature
	 *            minimum temperature.
	 * @param metropolisLength
	 *            metropolis length or Markov's chain length.
	 * @param alpha
	 *            the cooling rate.
	 * @param mutation
	 *            mutation used in the perturbation.
	 * @param criteria
	 *            criteria used in the acceptance criteria or transition rule.
	 */
	public MOSA(Problem<S> problem, int populationSize, double initialTemperature, double finalTemperature,
			int metropolisLength, double alpha, MutationOperator<S> mutation, AcceptanceCriteria<S> criteria) {
		super();
		this.problem = problem;
		this.populationSize = populationSize;
		this.setMetropolisLength(metropolisLength);
		this.setIntialTemperature(initialTemperature);
		this.setFinalTemperature(finalTemperature);
		this.setAlpha(alpha);

		this.mutationOperator = mutation;
		this.criteria = criteria;
		randomGenerator = JMetalRandom.getInstance();

	}

	@Override
	protected void initProgress() {
		iterations = 1;
		this.temperature = this.getInitialTemperature();

	}

	@Override
	protected void updateProgress() {
		iterations++;

	}

	@Override
	protected void updatePopulation(S candidate) {
		this.population.add(candidate);
		if (this.population.size() >= this.populationSize) {
			System.out.println("updating population");
			this.population = getNonDominatedSolutions(getPopulation());
			while (this.population.size() >= this.populationSize * 0.20)
				this.population.remove(randomGenerator.nextInt(0, this.population.size() - 1));
		}
	}

	@Override
	protected boolean isStopConditionReached() {
		return this.temperature <= this.getFinalTemperature();
	}

	@Override
	protected boolean isStochasticEquilibriumReached() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected double coolingScheme() {
		// return CoolingScheme.geometric(temperature, this.getAlpha());
		return this.temperature *= this.getAlpha();
	}

	@Override
	protected boolean acceptanceCriteria(S candidate, S current) {

		double[] weight = new double[candidate.getNumberOfObjectives()];

		for (int nObj = 0; nObj < this.problem.getNumberOfObjectives(); nObj++)
			weight[nObj] = randomGenerator.nextDouble();

		criteria.setWeight(weight);
		return criteria.execute(candidate, current, temperature) < randomGenerator.nextDouble();

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
	protected S perturbation(S solution) {
		S candidate = ((S) solution.copy());
		candidate = this.mutationOperator.execute(candidate);
		this.problem.evaluate(candidate);
		return candidate;
	}

	@Override
	protected List<S> evaluatePopulation(List<S> population) {
		for (S solution : population)
			this.problem.evaluate(solution);
		return population;
	}

	@Override
	protected boolean metropolisCycle() {
		if (this.metropolis++ <= this.getMetropolisLength())
			return true;

		this.metropolis = 0;
		return false;
	}

	@Override
	protected S selectRandomSolution() {
		return getPopulation().get(randomGenerator.nextInt(0, getPopulation().size() - 1));
	}

	public double getTemperature() {
		return this.temperature;
	}

	public int getIterations() {
		return this.iterations;
	}

	@Override
	public List<S> getResult() {
		return getNonDominatedSolutions(getPopulation());
	}

	public S getSolution(int idxSolution) {
		return this.population.get(idxSolution);
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

	public String getName() {
		// TODO Auto-generated method stub
		return "MOSA";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Multi-Objective Simulated Annealing";
	}

}

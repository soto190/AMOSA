package org.uma.jmetal.runner.multiobjective;

import java.util.List;

import org.uma.jmetal.algorithm.impl.AbstractSimulatedAnnealing.TransitionRule;
import org.uma.jmetal.algorithm.multiobjective.mosa.GeneticMOSA;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSA;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSABuilder;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.operator.scheduling.mutation.LoadBalancingMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.SchedulingProblem;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

public class GeneticMOSARunner extends AbstractAlgorithmRunner {

	public static void main(String[] args) {

		Problem<IntegerSolution> problem = new SchedulingProblem(12);
		CrossoverOperator<IntegerSolution> crossover;
		MutationOperator<IntegerSolution> mutation;
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection;
		SolutionListEvaluator<IntegerSolution> evaluator = new SequentialSolutionListEvaluator<IntegerSolution>();

		double crossoverProbability = 0.9;
		double crossoverDistributionIndex = 20.0;
		crossover = new IntegerSBXCrossover(crossoverProbability, crossoverDistributionIndex);

		double mutationProbability = 1.0 / problem.getNumberOfVariables();
		double mutationDistributionIndex = 20.0;
		mutation = new IntegerPolynomialMutation(mutationProbability, mutationDistributionIndex);

		selection = new BinaryTournamentSelection<IntegerSolution>();

		GeneticMOSA<IntegerSolution> algorithm;
		// MutationOperator mutation = new
		// LoadBalancingMutation(mutationProbability);
		AcceptanceCriteria<IntegerSolution> criteria = new AcceptanceCriteria<IntegerSolution>(
				TransitionRule.ScalarLinear);
		// MutationOperator mutation = new IntegerPolynomialMutation(0.9, 0.2);
		String problemName = problem.getName();
		System.out.println("Solving the instance: " + problemName);

		// algorithm = new GeneticMOSA<IntegerSolution>(problem, mutation);

		int populationSize = 100;
		double initialTemperature = 100;
		double finalTemperature = 0.01;
		int metropolisLength = 1;
		double alpha = 0.96;
		algorithm = new GeneticMOSA<IntegerSolution>(problem, 0, populationSize, initialTemperature, finalTemperature,
				alpha, metropolisLength, crossover, mutation, selection, evaluator);

		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

		List<IntegerSolution> population = algorithm.getResult();
		long computingTime = algorithmRunner.getComputingTime();
		System.out.println("Total iterations: " + algorithm.getIterations());

		JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

		printFinalSolutionSet(population);

	}
}

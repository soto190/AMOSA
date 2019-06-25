package org.uma.jmetal.runner.multiobjective;

import java.util.List;

import org.uma.jmetal.algorithm.impl.AbstractSimulatedAnnealing.TransitionRule;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSA;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSABuilder;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.scheduling.mutation.LoadBalancingMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.SchedulingProblem;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;

public class MOSAnalyticalRunnner extends AbstractAlgorithmRunner {

	public static void main(String[] args) {

		Problem<IntegerSolution> problem = new SchedulingProblem(12);
		double mutationProbability = 0.90;
		MOSA<IntegerSolution> algorithm;
		// MutationOperator mutation = new IntegerPolynomialMutation(1.0 /
		// problem.getNumberOfVariables() , 20.0);
		MutationOperator mutation = new LoadBalancingMutation(mutationProbability);
		AcceptanceCriteria<IntegerSolution> criteria = new AcceptanceCriteria<IntegerSolution>(
				TransitionRule.ScalarLinear);
		// MutationOperator mutation = new IntegerPolynomialMutation(0.9, 0.2);
		String problemName = problem.getName();
		System.out.println("Solving the instance: " + problemName);

		algorithm = new MOSABuilder<IntegerSolution>(problem, mutation) /**/
				.setPopulationSize(100) /**/
				.setMaxDeterioration(0.90) /**/
				.setMinDeterioration(0.10) /**/
				.setExplorationLevel(2) /**/
				.setAlpha(0.95) /**/
				.setAcceptanceCriteria(criteria) /**/
				.setVariant(MOSABuilder.MOSAVariant.MOSAAnalyticalTunning).build();

		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

		List<IntegerSolution> population = algorithm.getResult();
		long computingTime = algorithmRunner.getComputingTime();
		System.out.println("Total iterations: " + algorithm.getIterations());

		JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

		printFinalSolutionSet(population);

	}
}

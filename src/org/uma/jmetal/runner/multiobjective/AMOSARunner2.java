package org.uma.jmetal.runner.multiobjective;

import java.util.List;

import org.uma.jmetal.algorithm.impl.AbstractSimulatedAnnealing.TransitionRule;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSA;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSABuilder;
import org.uma.jmetal.algorithm.multiobjective.mosa.criteria.AcceptanceCriteria;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.scheduling.mutation.LoadBalancingMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.SchedulingProblem;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.ArchiveMOSA;

/**
 * Archive Multi-objective Simulated Annealing (AMOSA) Runner.
 * 
 * Bandyopadhyay, S., Saha, S., Maulik, U., & Deb, K. (2008). A simulated
 * annealing-based multiobjective optimization algorithm: AMOSA. Evolutionary
 * Computation, IEEE Transactions on, 12(3), 269-283.
 *
 * In AMOSA paper the next values are used Tmax = 200, Tmin = 10^-7, and iter =
 * 500. Population = 100.
 * 
 * @author Jos� Carlos Soto Monterrubio <soto190@gmail.com>
 * @version 1.0
 *
 */
public class AMOSARunner2 extends AbstractAlgorithmRunner {

	public static void main(String[] args) {

		String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
		String referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT1.pf";

		Problem<DoubleSolution> problem = (DoubleProblem) ProblemUtils.<DoubleSolution> loadProblem(problemName);
		MutationOperator<DoubleSolution> mutation;
		double mutationProbability = 1.0 / problem.getNumberOfVariables();
		double mutationDistributionIndex = 20.0;
		mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
		MOSA<DoubleSolution> algorithm;
		// MutationOperator mutation = new
		// LoadBalancingMutation(mutationProbability);
		System.out.println("Solving the instance: " + problemName);

		algorithm = new MOSABuilder<DoubleSolution>(problem, mutation) /**/
				.setPopulationSize(100) /**/
				.setInitialTemperature(100) /**/
				.setFinalTemperature(0.01) /**/
				.setMetropolisLength(500) /**/
				.setAlpha(0.95) /**/
				.setHardLimit(100) /**/
				.setSoftLimit(150) /**/
				.setVariant(MOSABuilder.MOSAVariant.AMOSA).build();

		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

		List<DoubleSolution> population = algorithm.getResult();
		long computingTime = algorithmRunner.getComputingTime();
		System.out.println("Total iterations: " + algorithm.getIterations());

		JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

		printFinalSolutionSet(population);

	}
}


//int[][] test = new int[][] { { 4, 10 },
		// { 14, 21 },
		// { 19, 1 },
		// { 20, 37 },
		// { 29, 31 },
		// { 32, 22 },
		// { 40, 7 },
		// { 44, 47 },
		// { 47, 44 },
		// { 50, 41 },
		// { 70, 39 },
		// { 78, 7 },
		// { 86, 38 },
		// { 90, 28 },
		// { 94, 17 },
		// { 95, 37 },
		// {69, 31},
		// {84, 47},
		// {88, 28},
		// {63, 41}};
		//
		// ArchiveMOSA<Solution> archive = new ArchiveMOSA<Solution>(10, 20);
		// for (int i = 0; i < test.length; i++) {
		// IntegerSolution sol = problem.createSolution();
		// sol.setObjective(0, test[i][0]);
		// sol.setObjective(1, test[i][1]);
		// archive.add(sol);
		// }
		// archive.clustering();
package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.SchedulingProblem;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIRunnerScheduling extends AbstractAlgorithmRunnerV2 {
	/**
	 * @param args
	 *            Command line arguments.
	 * @throws JMetalException
	 * @throws FileNotFoundException
	 *             Invoking command: java
	 *             org.uma.jmetal.runner.multiobjective.NSGAIIRunner problemName
	 *             [referenceFront]
	 */
	public static void main(String[] args) throws JMetalException, FileNotFoundException {

		String referencePath = System.getProperty("user.dir") + "/ReferenceParetoFronts/MachineScheduling/";
		String folder = "NSGAII";
		String outputFile = System.getProperty("user.dir") + "/" + folder + "/NSGAII.qi";

		for (int inst = 12; inst < 55; inst++)
			for (int r = 1; r <= 30; r++) {

				Problem<IntegerSolution> problem = new SchedulingProblem(inst);
				
				String problemName = problem.getName().split("\\.")[0];
				String referenceFront = referencePath + problemName + ".pf";
				
				Algorithm<List<IntegerSolution>> algorithm;
				CrossoverOperator<IntegerSolution> crossover;
				MutationOperator<IntegerSolution> mutation;
				SelectionOperator<List<IntegerSolution>, IntegerSolution> selection;
				String referenceParetoFront = "";

				double crossoverProbability = 0.9;
				double crossoverDistributionIndex = 20.0;
				crossover = new IntegerSBXCrossover(crossoverProbability, crossoverDistributionIndex);

				double mutationProbability = 1.0 / problem.getNumberOfVariables();
				double mutationDistributionIndex = 20.0;
				mutation = new IntegerPolynomialMutation(mutationProbability, mutationDistributionIndex);

				selection = new BinaryTournamentSelection<IntegerSolution>(
						new RankingAndCrowdingDistanceComparator<IntegerSolution>());

				algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
						.setSelectionOperator(selection).setMaxEvaluations(4000).setPopulationSize(100).build();

				AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

				List<IntegerSolution> population = algorithm.getResult();
				System.out.println(population.size());
				long computingTime = algorithmRunner.getComputingTime();

				JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

				String fileText = problemName + "-r" + r;

				printFinalSolutionSet(population, folder, fileText);
				if (!referenceParetoFront.equals("")) {
					printQualityIndicators(population, referenceParetoFront);
				}
			}
	}
}

package org.uma.jmetal.runner.multiobjective;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.mosa.MOSA;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSABuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.scheduling.mutation.LoadBalancingMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.SchedulingProblem;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;

public class AMOSAnalyticalStochasticRunnner extends AbstractAlgorithmRunnerV2 {

	public static void main(String[] args) {

		String referencePath = System.getProperty("user.dir") + "/ReferenceParetoFronts/MachineScheduling/";
		String folder = "AMOSA-AT-S";
		String outputFile = System.getProperty("user.dir") + "/" + folder + "/AMOSA-AT-S.qi";

		BufferedWriter bf = null;
		try {
			bf = new BufferedWriter(new FileWriter(new File(outputFile)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (int inst = 12; inst <= 12; inst++)
			for (int r = 1; r <= 1; r++) {

				Problem<IntegerSolution> problem = new SchedulingProblem(inst);
				String problemName = problem.getName().split("\\.")[0];
				String referenceFront = referencePath + problemName + ".pf";

				double crossoverProbability = 0.999;
				double crossoverDistributionIndex = 20.0;

				CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(crossoverProbability,
						crossoverDistributionIndex);
				double mutationProbability = 0.20; // 1.0 /
													// problem.getNumberOfVariables()
				MOSA<IntegerSolution> algorithm;
				MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(mutationProbability, 20.0);
				System.out.println("Solving the instance: " + problemName);

				double maxDeterioration = 0.95;
				double minDeterioration = 1 - maxDeterioration;
				algorithm = new MOSABuilder<IntegerSolution>(problem, mutation) /**/
						.setPopulationSize(100) /**/
						.setMaxDeterioration(maxDeterioration) /**/
						.setMinDeterioration(minDeterioration) /**/
						.setExplorationLevel(3) /**/
						.setAlpha(0.95) /**/
						.setCrossover(crossover) /**/
						.setHardLimit(100) /**/
						.setSoftLimit(120) /**/
						.setVariant(MOSABuilder.MOSAVariant.AMOSAAnalyticalTunningStochastic).build();

				AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

				List<IntegerSolution> population = algorithm.getResult();
				long computingTime = algorithmRunner.getComputingTime();
				System.out.println("Total iterations: " + algorithm.getIterations());

				String fileText = problemName + "-r" + r;
				JMetalLogger.logger.info("Total execution time " + fileText + ": " + computingTime + "ms");
				// JMetalLogger.logger.info(problemName);

				printFinalSolutionSet(population, folder, fileText);
			}
		try {
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

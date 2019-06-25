package org.uma.jmetal.runner.multiobjective;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.mosa.MOSA;
import org.uma.jmetal.algorithm.multiobjective.mosa.MOSABuilder;
import org.uma.jmetal.operator.MutationOperator;
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

public class AMOSAnalyticalWithFuzzyRunnner extends AbstractAlgorithmRunnerV2 {


	public static void main(String[] args) {

		String referencePath = System.getProperty("user.dir") + "/ReferenceParetoFronts/MachineScheduling/";
		String folder = "AMOSA-ATFC";
		String outputFile = System.getProperty("user.dir") + "/" + folder + "/AMOSA-ATFC.qi";

		BufferedWriter bf = null;
		try {
			bf = new BufferedWriter(new FileWriter(new File(outputFile)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int inst = 12; inst < 13; inst++)
			for (int r = 0; r < 1; r++) {

				Problem<IntegerSolution> problem = new SchedulingProblem(inst);
				String referenceFront = referencePath + problem.getName().split("\\.")[0] + ".pf";
				double mutationProbability = 0.90;
				MOSA<IntegerSolution> algorithm;
				MutationOperator mutation = new IntegerPolynomialMutation(1.0 / problem.getNumberOfVariables() , 20.0);
				// MutationOperator mutation = new
				// LoadBalancingMutation(mutationProbability);
				String problemName = problem.getName();
				System.out.println("Solving the instance: " + problemName);

				algorithm = new MOSABuilder<IntegerSolution>(problem, mutation) /**/
						.setPopulationSize(100) /**/
						.setMaxDeterioration(0.90) /**/
						.setMinDeterioration(0.10) /**/
						.setExplorationLevel(2) /**/
						.setAlpha(0.95) /**/
						.setHardLimit(100) /**/
						.setSoftLimit(150) /**/
						.setVariant(MOSABuilder.MOSAVariant.MOSAAnalyticalTunningAndFuzzy).build();

				AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

				List<IntegerSolution> population = algorithm.getResult();
				long computingTime = algorithmRunner.getComputingTime();
				System.out.println("Total iterations: " + algorithm.getIterations());

				JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

				JMetalLogger.logger.info(problemName);

				String fileText = inst + "r" + r;

				printFinalSolutionSet(population, folder, fileText);
//				try {
//					printQI(bf, problemName, population, referenceFront);
//
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
			}
		try {
			bf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

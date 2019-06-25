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
import org.uma.jmetal.qualityindicator.impl.*;
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

/**
 * Archive Multi-objective Simulated Annealing (AMOSA) Runner.
 * 
 * Bandyopadhyay, S., Saha, S., Maulik, U., & Deb, K. (2008). A simulated
 * annealing-based multiobjective optimization algorithm: AMOSA. Evolutionary
 * Computation, IEEE Transactions on, 12(3), 269-283.
 *
 * In AMOSA paper the next values are used Tmax = 200, Tmin = 10^E-7, and iter =
 * 500. Population = 100.
 * 
 * @author José Carlos Soto Monterrubio <soto190@gmail.com>
 * @version 1.0
 *
 */
public class AMOSARunner extends AbstractAlgorithmRunnerV2 {
	
	
	public static void main(String[] args) {

		String referencePath = System.getProperty("user.dir") + "/ReferenceParetoFronts/MachineScheduling/";
		String folderName = "AMOSAtest";
		String outputQIFile = System.getProperty("user.dir") + "/" + folderName + "/AMOSA.qi";
		BufferedWriter bf = null;
		try {
			bf = new BufferedWriter(new FileWriter(new File(outputQIFile)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (int inst = 12; inst < 13; inst++) {

			for (int r = 0; r < 1; r++) {

				Problem<IntegerSolution> problem = new SchedulingProblem(inst);
				String problemName =  problem.getName().split("\\.")[0];
				String referenceFront = referencePath + problemName+ ".tsv";
				double mutationProbability = 1;
				MOSA<IntegerSolution> algorithm;
//				 MutationOperator mutation = new LoadBalancingMutation(mutationProbability);
				MutationOperator mutation = new IntegerPolynomialMutation(mutationProbability, 20.0);
				System.out.println("Solving the instance: " + problemName);

				algorithm = new MOSABuilder<IntegerSolution>(problem, mutation) /**/
						.setPopulationSize(100) /**/
						.setInitialTemperature(8000) /**/
						.setFinalTemperature(0.01) /**/
						.setMetropolisLength(50) /**/
						.setAlpha(0.98) /**/
						.setHardLimit(100) /**/
						.setSoftLimit(150) /**/
						.setVariant(MOSABuilder.MOSAVariant.AMOSA).build();

				AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

				List<IntegerSolution> population = algorithm.getResult();
				long computingTime = algorithmRunner.getComputingTime();
				System.out.println("Total iterations: " + algorithm.getIterations());

				JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
				
				String outputPathFile =  System.getProperty("user.dir") + "/" + folderName;
				String fileName = problemName + "-r" + r;
				printFinalSolutionSet(population, outputPathFile, fileName);
//				try {
//					printQI(bf, problemName, population, referenceFront);
//
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
			}

		}
		try {
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

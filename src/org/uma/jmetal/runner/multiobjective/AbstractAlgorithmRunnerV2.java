package org.uma.jmetal.runner.multiobjective;

import java.io.File;
import java.util.List;

import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class AbstractAlgorithmRunnerV2 extends AbstractAlgorithmRunner {
	/**
	 * This methods write to a file the variables and the objective values of
	 * the given solution set. This method adds to the file name the prefix VAR-
	 * to the file which contains the variable values and the prefix FUN- to the
	 * file with the objective values. The saved files uses the  file extension
	 * .tsv.
	 * 
	 * @param population
	 *            Solution set to be written in the file.
	 * @param outputPathFile
	 *            The directory path where the file will be written.
	 * @param fileName
	 *            The name used to give to the file in which the data will be
	 *            written.
	 */

	public static void printFinalSolutionSet(List<? extends Solution<?>> population, String outputPathFile,
			String fileName) {
		new SolutionListOutput(population).setSeparator("\t")
				.setVarFileOutputContext(new DefaultFileOutputContext(outputPathFile + "/VAR-" + fileName + ".tsv"))
				.setFunFileOutputContext(new DefaultFileOutputContext(outputPathFile + "/FUN-" + fileName + ".tsv"))
				.print();

		JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
		JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
		JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

	}

}

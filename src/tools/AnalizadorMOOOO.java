package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.ErrorRatio;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;

import analizadormo.AnalizadorMO;
import analizadormo.Comparador;
import analizadormo.MaxMin;

public class AnalizadorMOOOO {

	public static void printQualityIndicators(String name, String output, String approxFrontFile,
			String paretoFrontFile) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(output), true));

		Front referenceFront = new ArrayFront(paretoFrontFile);
		Front approxFront = new ArrayFront(approxFrontFile);
		FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);

		Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
		Front normalizedFront = frontNormalizer.normalize(approxFront);
		List<DoubleSolution> normalizedPopulation = null;//FrontUtils.convertFrontToSolutionList(normalizedFront);

		String outputString = name + " ";
		outputString += /* "Hypervolume (N) : " */
		+new PISAHypervolume<DoubleSolution>(normalizedReferenceFront).evaluate(normalizedPopulation) + " ";
		outputString += /* "Epsilon (N)     : " */
		+new Epsilon<DoubleSolution>(normalizedReferenceFront).evaluate(normalizedPopulation) + " ";
		outputString += /* "GD (N)          : " */
		+new GenerationalDistance<DoubleSolution>(normalizedReferenceFront).evaluate(normalizedPopulation) + " ";
		outputString += /* "IGD (N)         : " */
		+new InvertedGenerationalDistance<DoubleSolution>(normalizedReferenceFront).evaluate(normalizedPopulation)
				+ " ";
		outputString += /* "IGD+             : " */
		+new InvertedGenerationalDistancePlus<DoubleSolution>(normalizedReferenceFront).evaluate(normalizedPopulation)
				+ " ";
		outputString += /* "Spread            : " */
		+new Spread<DoubleSolution>(normalizedReferenceFront).evaluate(normalizedPopulation) + "\n";

		bw.write(outputString);
		bw.close();
		JMetalLogger.logger.info(outputString);
	}

	public static void main(String[] args) throws NumberFormatException, IOException {

		String problemName[] = new String[] { /***/
				"Ahmad_3_9_28", "Bittencourt_3_9_184", "Cao_3_10_536", /***/
				"Ching_3_10_84", "Demiroz_3_7_47", "Eswari_2_11_61", /***/
				"Gulzar_3_10_124", "Hamid_3_10", "Hernandez_3_10_70", /***/
				"heteroparjorgebarbosa1-3", "heteroparjorgebarbosa2", /***/
				"Ijaz_3_10_133", "Ilavarasan_3_10_77", "Ilavarasan_3_11_27", /***/
				"Ilavarasan_3_15_114", "IlavarasanIJCSIT_3_10", "Kang_3_10_76", /***/
				"Kang_3_10_84", "Kuan_3_10_28", "Liang_3_10_80", /***/
				"Linshan_4_9_38", "Liu_2_8_364", "Mohammad_2_11_64", /***/
				"Munir_3_10_76", "Rahmani_3_7", "SahA_3_11_131", /***/
				"SahB_3_6_76", "Samantha_5_11_31", "sample_3_10", /***/
				"sample_3_13", "sample_3_8_100", "sample_4_11_25", /***/
				"sample_8_3_100", "Tao_3_10", "TOPCUOGLU_3_10_80", /***/
				"Topcuoglu_3_8_51", "Xu_3_8_66", "YCLee_3_8_80", /***/
				"Yu_4_10", "Zhao_3_10_143" };

		String folderParetoFront = System.getProperty("user.dir") + File.separator + "ReferenceParetoFronts"
				+ File.separator + "MachineScheduling" + File.separator;

		String algorithmName = "AMOSA";
		String folderParetoApprox = System.getProperty("user.dir") + File.separator + algorithmName + File.separator;

		String output = System.getProperty("user.dir") + File.separator + "ex2.1-metrics" + algorithmName + ".txt";
		for (int p = 0; p < problemName.length; p++)
			for (int run = 1; run <= 10; run++) {
				// printQualityIndicators(problemName[p], output,
				// folderParetoApprox + "FUN-" + problemName[p] + "-r" + run +
				// ".tsv",
				// folderParetoFront + problemName[p] + ".tsv");
				printQualityIndicators(problemName[p], output,
						folderParetoApprox + "FUN-" + (p + 12) + "r" + (run - 1) + ".tsv",
						folderParetoFront + problemName[p] + ".tsv");
			}
	}

}

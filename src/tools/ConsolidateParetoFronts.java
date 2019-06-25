package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import analizadormo.Comparador;

public class ConsolidateParetoFronts {
	String pathParetoFront = "";
	private BufferedReader brParetoFront;
	private BufferedReader brParetoEstimated;
	ArrayList<double[]> paretoFront = new ArrayList<double[]>();

	public ConsolidateParetoFronts(String pathParetoFront) throws NumberFormatException, IOException {
		brParetoFront = new BufferedReader(new FileReader(new File(pathParetoFront)));
		this.pathParetoFront = pathParetoFront;
		String ls;
		while ((ls = brParetoFront.readLine()) != null) {
			String[] temp = ls.split("\\s+");
			double[] data = new double[temp.length];
			for (int i = 0; i < temp.length; i++)
				data[i] = Double.parseDouble(temp[i]);

			Comparador.agrega(paretoFront, data);
		}
		brParetoFront.close();
	}

	public void updateFile(String filePath) throws IOException {

		brParetoEstimated = new BufferedReader(new FileReader(new File(filePath)));

		String ls;
		while ((ls = brParetoEstimated.readLine()) != null) {
			String[] temp = ls.split("\\s+");
			double[] data = new double[temp.length];
			for (int i = 0; i < temp.length; i++)
				data[i] = Double.parseDouble(temp[i]);

			Comparador.agrega(paretoFront, data);
		}
		brParetoEstimated.close();
	}

	public void saveParetoFront() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathParetoFront), false));

		for (int i = 0; i < paretoFront.size() - 1; i++) {
			double[] tmp = paretoFront.get(i);
			String stmp = "";
			for (int j = 0; j < tmp.length - 1; j++)
				stmp += tmp[j] + " ";

			stmp += tmp[tmp.length - 1] + "\n";
			bw.write(stmp);
		}

		double[] tmp = paretoFront.get(paretoFront.size() - 1);
		String stmp = "";
		for (int j = 0; j < tmp.length - 1; j++)
			stmp += tmp[j] + " ";

		stmp += tmp[tmp.length - 1];
		bw.write(stmp);
		bw.close();
	}

	public static void main(String[] args) {

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

		String algorithmName = "AMOSA-AT-Sv4";
		String folderParetoNew = System.getProperty("user.dir") + File.separator + algorithmName + File.separator;

		int numberOfRuns = 10;
		for (int p = 0; p < problemName.length; p++)
			try {
				String pathFile = folderParetoFront + problemName[p] + ".tsv";

				
				ConsolidateParetoFronts cpf = new ConsolidateParetoFronts(pathFile);
				for (int run = 1; run <= numberOfRuns; run++) {

					String pathnewPareto = folderParetoNew + "FUN-" + problemName[p]+ "-r" + run + ".tsv";
//					String pathnewPareto = folderParetoNew + "FUN-" + (p+12) + "r" +(run -1) + ".tsv";
					
					cpf.updateFile(pathnewPareto);
				}
				cpf.saveParetoFront();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}

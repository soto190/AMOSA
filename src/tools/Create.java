package tools;

import java.io.File;

public class Create {

	public static void createFoldersExperiment8() {
		String workPath = System.getProperty("user.dir");
		File folder1;
		File folder3;

		for (int i = 1; i <= 7; i++) {

			folder1 = new File(workPath + "/Experimento8/CEC2009/UF" + i
					+ "/fun");
			folder3 = new File(workPath + "/Experimento8/CEC2009/UF" + i
					+ "/log");

			folder1.mkdirs();
			folder3.mkdirs();

			folder1 = new File(workPath + "/Experimento8/DTLZ/DTLZ" + i
					+ "/fun");
			folder3 = new File(workPath + "/Experimento8/DTLZ/DTLZ" + i
					+ "/log");

			folder1.mkdirs();
			folder3.mkdirs();
		}

		for (int i = 1; i <= 9; i++) {

			folder1 = new File(workPath + "/Experimento8/LZ09/LZ09_F" + i
					+ "/fun");
			folder3 = new File(workPath + "/Experimento8/LZ09/LZ09_F" + i
					+ "/log");

			folder1.mkdirs();
			folder3.mkdirs();
		}

		for (int i = 1; i <= 6; i++) {
			folder1 = new File(workPath + "/Experimento8/ZDT/ZDT" + i + "/fun");
			folder3 = new File(workPath + "/Experimento8/ZDT/ZDT" + i + "/log");

			folder1.mkdirs();
			folder3.mkdirs();
		}

		for (int i = 1; i <= 51; i++) {

			folder1 = new File(workPath + "/Experimento8/MachineScheduling/MS"
					+ i + "/fun");
			folder3 = new File(workPath + "/Experimento8/MachineScheduling/MS"
					+ i + "/log");

			folder1.mkdirs();
			folder3.mkdirs();
		}

	}

	public static void createFoldersEx11() {
		Utilities utils = new Utilities();

		String workPath = System.getProperty("user.dir");
		File folder1;
		for (int i = 0; i < 52; i++) {
			folder1 = new File(workPath + "/Experiments/Ex14/moead/MS"+ i);

			folder1.mkdirs();
//			System.out.print("\"" +utils.readInstance(i).getName().split("\\.")[0] + "\", ");
		}
	}

	public static void main(String[] args) {

		createFoldersEx11();

	}
}

package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import hcs.HCS;
import hcs.Machine;

/**
 * 
 * @author soto190
 * 
 */
public class Utilities {

	private static BufferedReader inputReader;
	private static File instances[];
	private static File machinesConfig;
	private static BufferedReader br;

	public Utilities() {
		loadIndex();
		loadFileMachinesConfig("Machines.conf");
	}

	private static void loadFileMachinesConfig(String config) {

		String path = System.getProperty("user.dir") + File.separator
				+ "Instances" + File.separator + "Machines" + File.separator
				+ config;
		machinesConfig = new File(path);
	}

	private static void loadIndex() {

		String workPath = System.getProperty("user.dir") + File.separator
				+ "Instances" + File.separator;

		try {
			BufferedReader br = new BufferedReader(new FileReader(workPath
					+ "Index"));

			int totalInstances = toInt(br.readLine());
			instances = new File[totalInstances];

			for (int i = 0; i < totalInstances; i++)
				instances[i] = new File(workPath + br.readLine());

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HCS readInstance(int indexInstance) {

		if (indexInstance >= 0 && indexInstance <= 11)
			return readInstanceBraunt(indexInstance);

		if (indexInstance >= 12 && indexInstance <= 52)
			return readInstanceReferencia(indexInstance);

		System.err.println("No se encontro la instancia " + indexInstance);
		return null;

	}

	private HCS readInstanceBraunt(int indexInstance) {

		HCS hcs = null;

		try {
			hcs = new HCS(512, 16);
			hcs.setName(instances[indexInstance].getName());
			loadFileMachinesConfig("Machines.conf");
			readConfigurations(hcs);

			inputReader = new BufferedReader(new FileReader(
					instances[indexInstance]));

			for (int task = 0; task < 512; task++)
				for (int machine = 0; machine < 16; machine++)
					hcs.addExecTime(task, machine,
							toDouble(inputReader.readLine()));

			inputReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hcs;
	}

	private HCS readInstanceReferencia(int indexInstance) {
		HCS hcs = null;
		try {

			inputReader = new BufferedReader(new FileReader(
					instances[indexInstance]));

			inputReader.readLine();
			String[] dataIn = inputReader.readLine().split("\\s+");
			hcs = new HCS(toInt(dataIn[0]), toInt(dataIn[1]));
			hcs.setName(instances[indexInstance].getName());
			inputReader.readLine();
			for (int task = 0; task < hcs.getTotalTasks(); task++) {
				dataIn = inputReader.readLine().split("\\s+");
				for (int machine = 0; machine < hcs.getTotalMachines(); machine++)
					hcs.addExecTime(task, machine, toDouble(dataIn[machine]));
			}

			inputReader.close();
			readConfigurations(hcs);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hcs;
	}

	private static void readConfigurations(HCS hcs) {

		try {
			inputReader = new BufferedReader(new FileReader(machinesConfig));

			/** read comments line "Configuraci�n (voltaje/velocidad)". **/
			inputReader.readLine();

			/** Read metadata: Max k Configurations and total machines. **/
			String dataV[];
			String[] dataIn = inputReader.readLine().split("\\s+");
			
			int maxKconfig = toInt(dataIn[0]);
			int totalMachines = toInt(dataIn[1]);
			hcs.setMaxKConfig(maxKconfig);
			/***
			 *  Repeats for the number of voltage/speeds configurations
			 * for the next machines.
			 */
			int baseMachine = 0;

			for (int k = 0; k < maxKconfig; k++) {
				dataIn = inputReader.readLine().split("\\s+");

				for (int machine = 0; machine < hcs.getTotalMachines(); machine++) {
					if (machine < totalMachines) {
						dataV = dataIn[machine].split("/");
						double voltaje = toDouble(dataV[0]);
						double speed = toDouble(dataV[1]);

						if (k == 0)
							hcs.addMachine(new Machine(machine, hcs
									.getTotalTasks(), maxKconfig));

						if (voltaje == Double.MAX_VALUE)
							hcs.getMachine(machine).setTotalConfig(k);
						else
							hcs.getMachine(machine).setKConfigVoltajeAndSpeed(
									k, voltaje, speed);
						baseMachine = 0;

					} else {

						if (k == 0)
							hcs.addMachine(new Machine(machine, hcs
									.getTotalTasks(), maxKconfig));

						if (!hcs.getMachine(baseMachine).lockedConfig())
							hcs.getMachine(machine).copyKConfig(k,
									hcs.getMachine(baseMachine));
						else
							hcs.getMachine(machine).setTotalConfig(
									hcs.getMachine(baseMachine).getTotalConfig());

						if (++baseMachine == totalMachines)
							baseMachine = 0;
					}
				}
			}
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int toInt(String sn) {
		return Integer.parseInt(sn);
	}

	public static double toDouble(String sn) {
		if (sn.equals("inf"))
			return Double.MAX_VALUE;
		return Double.parseDouble(sn);
	}

	public static void printArray(int[] array) {
		System.out.print("[");
		for (int i = 0; i < array.length; i++)
			System.out.printf("%2d" + (i < array.length - 1 ? ", " : "]\n"),
					array[i]);
	}

	public static void printArray(double[] array) {
		System.out.print(" [");
		for (int i = 0; i < array.length; i++)
			System.out.printf("%6.6f" + (i < array.length - 1 ? ", " : "]\n"),
					array[i]);
	}

	public static double[] getMaxValues(String file) throws IOException {

		FileReader fr = new FileReader(file);
		br = new BufferedReader(fr);

		String[] data = br.readLine().split("\\s+");

		int dimension = data.length;
		double[] max = new double[dimension];
		double[] val = new double[dimension];

		max[0] = toDouble(data[0]);
		max[1] = toDouble(data[1]);

		String lin = "";
		while ((lin = br.readLine()) != null) {
			data = lin.split("\\s+");

			for (int i = 0; i < dimension; i++) {
				val[i] = toDouble(data[i]);

				if (val[i] > max[i])
					max[i] = val[i];
			}
		}
		return max;
	}

	public static void saveResultsExperiment(String file, String arr[]) {
		try {

			BufferedWriter pw = new BufferedWriter(new FileWriter(file, true));
			String s = "";

			for (int i = 0; i < arr.length; i++)
				s += arr[i];

			pw.write(s);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double truncate(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = (long) value;
		return (double) tmp / factor;
	}

}

package hcs;

import tools.Utilities;

import org.uma.jmetal.solution.impl.SchedulingIntegerSolution;

//import jmetal.core.Variable;
import org.uma.jmetal.util.JMetalException;

/**
 * This class represents an instance of the HCSP. Includes an Task array and
 * Machine array.
 * 
 * Se encarga de calcular el makespan y energ�a, almacena la informaci�n de la
 * instancia Tareas, m�quinas, niveles de velocidad y voltaje.
 * 
 * 
 * 
 * @author soto190
 * 
 */
public class HCS {

	protected String instanceName;
	protected int totalTasks;
	protected int totalMachines;
	protected int totalEdges;
	protected int maxKConfig;

	protected Machine[] machine;
	protected Task[] task;

	public int indexEdges[];
	public double edges[][];
	public double antecedents[], precedents[];

	protected double[][] execTime;

	public double makespan, energy;

	/**
	 * Creates an HCS with the total of tasks and machines specified.
	 * 
	 * @param totalTasks
	 *            Total of the task in the HCS.
	 * @param totalMachines
	 *            Total of the machines in the HCS.
	 */

	public HCS(int totalTasks, int totalMachines) {

		this.totalTasks = totalTasks;
		this.totalMachines = totalMachines;
		task = new Task[totalTasks];
		machine = new Machine[totalMachines];
		execTime = new double[totalTasks][totalMachines];
	}

	public HCS(int totalTasks, int totalMachines, int maxKConfig, int totalEdges) {

		this.totalTasks = totalTasks;
		this.totalMachines = totalMachines;
		this.maxKConfig = maxKConfig;
		this.totalEdges = totalEdges;
		task = new Task[totalTasks];
		machine = new Machine[totalMachines];
		execTime = new double[totalTasks][totalMachines];

		this.edges = new double[totalEdges][3];
	}

	/**
	 * Add a new task to the HCS.
	 * 
	 * @param task
	 *            The task to be added.
	 */
	public void addTask(Task task) {
		this.task[task.getId()] = task;
	}

	/**
	 * Add a new machine to the HCS
	 * 
	 * @param machine
	 *            The machine to be added.
	 */
	public void addMachine(Machine machine) {
		if (this.machine[machine.getId()] == null)
			this.machine[machine.getId()] = machine;
		else
			this.machine[machine.getId()].setMaxKConfig(machine.getTotalConfig());

	}

	/**
	 * get the total of tasks in the HCS.
	 * 
	 * @return Integer with total of tasks in the HCS.
	 */
	public int getTotalTasks() {
		return this.task.length;
	}

	/**
	 * Get the total of machines in the HCS.
	 * 
	 * @return integer with the total of machines in the HCS.
	 */
	public int getTotalMachines() {
		return this.machine.length;
	}

	/**
	 * 
	 * @param id
	 *            Id of the machine.
	 * @return Machine with the specified id.
	 */
	public Machine getMachine(int id) {
		return machine[id];
	}

	/**
	 * 
	 * @param id
	 *            Id of the task.
	 * @return Task with the specified id.
	 */
	public Task getTask(int id) {
		return task[id];
	}

	public int getMaxKConfig() {
		return this.maxKConfig;
	}
	
	public void setMaxKConfig(int maxKconfig){
		this.maxKConfig = maxKconfig;
	}

	/**
	 * Add the execution time of the task in the machine.
	 * 
	 * @param idTask
	 *            Id of the task.
	 * @param idMachine
	 *            Id of the Machine.
	 * @param execTime
	 *            execution time to process the specified task in the machine.
	 */

	public void addExecTime(int idTask, int idMachine, double execTime) {

		if (this.task[idTask] == null)
			this.task[idTask] = new Task(idTask);
		if (this.machine[idMachine] == null)
			this.machine[idMachine] = new Machine(idMachine, totalTasks);

		this.execTime[idTask][idMachine] = execTime;
		this.machine[idMachine].addTask(idTask, execTime);
	}

	/**
	 * Set the name of the current instance.
	 * 
	 * @param name
	 *            Name of the instance.
	 */

	public void setName(String name) {
		this.instanceName = name;
	}

	/**
	 * 
	 * @return String with the name of the current instance.
	 */
	public String getName() {
		return this.instanceName;
	}

	public boolean isAntecedent(int a, int i) {

		for (int j = 0; j < edges.length; j++) {
			if (edges[j][0] == (a + 1) && edges[j][1] == (i + 1))
				return true;
		}

		return false;
	}

	public boolean existsEdge(int u, int v) {
		for (int j = this.indexEdges[u]; j < this.indexEdges[u + 1]; j++)
			if (edges[j][1] == v)
				return true;

		return false;
	}

	public double getP(int task, int machine) {
		return this.machine[machine].getExecTime(task);
	}

	public double getPprime(int task, int machine, int config) {
		Machine mach = this.getMachine(machine);
		return this.getP(task, machine) / mach.getKspeed(config);
	}

	public double getEnergy(int task, int machine, int config) {
		Machine mach = this.getMachine(machine);
		return this.getP(task, machine) / mach.getKspeed(config)
				* mach.getKvoltaje(config) * mach.getKvoltaje(config);
	}

	public double getCost(int u, int v) {

		for (int j = 0; j < edges.length; j++) {
			if (edges[j][0] == (u + 1) && edges[j][1] == (v + 1))
				return edges[j][2];
		}
		return 0;
	}

	public int getTotalNoPrecedence() {

		int total = 0;
		for (int i = 0; i < precedents.length; i++)
			if (antecedents[i] == 0)
				total++;

		return total;
	}

	public double evaluatePartialOF(int tm[], int level) {

		double makespan = 0;
		double timeM[] = new double[this.getTotalMachines()];
		for (int i = 0; i < level; i++) {
			timeM[tm[i] - 1] += this.getP(i, tm[i] - 1);
			if (timeM[tm[i] - 1] > makespan)
				makespan = timeM[tm[i] - 1];
		}
		return makespan;
	}

	public double evaluatePartialEnergy(int[] tm, int[] tc, int level) {

		double totalEnergy = 0;
		double makespan = 0;
		double timeM[] = new double[this.totalMachines];
		double taskEnergy[] = new double[this.totalTasks];
		double taskP[] = new double[this.totalTasks];
		int maquina = 0;
		int config = 0;
		try {
			for (int i = 0; i < level; i++) {

				maquina = tm[i] - 1;
				config = tc[i] - 1;
				Machine mach = this.getMachine(maquina);

				taskEnergy[i] = this.getP(i, maquina)
						* mach.getKvoltaje(config) * mach.getKvoltaje(config)
						/ mach.getKspeed(config);

				totalEnergy += taskEnergy[i];

				taskP[i] = this.getP(i, maquina) / mach.getKspeed(config);
				timeM[maquina] += taskP[i];

				if (timeM[maquina] > makespan)
					makespan = timeM[maquina];
			}

			double idleEnergy = 0;
			// for (int mach = 0; mach < timeM.length; mach++) {
			// Machine m = this.getMachine(mach);
			// int k = m.getkConfig() - 1;
			// idleEnergy += (makespan - timeM[mach]) * m.getKvoltaje(k)
			// * m.getKvoltaje(k);
			// }

			return totalEnergy + idleEnergy;

		} catch (Exception ex) {
			ex.printStackTrace();
			Utilities.printArray(tc);
			Utilities.printArray(tm);
			System.out.println(level + " " + maquina + " " + config);
			System.exit(0);
		}
		return -1;
	}

	public double[] evaluateObjectives(int[] tm, int[] tc) {

		double energy = 0;
		double makespan = 0;
		double timeM[] = new double[this.getTotalMachines()];
		int maquina = 0;
		int config = 0;
		double[] taskEnergy = new double[tm.length];
		double[] taskTime = new double[tm.length];

		for (int i = 0; i < tm.length; i++) {

			maquina = tm[i] - 1;
			config = tc[i] - 1;

			Machine mach = this.getMachine(maquina);

			taskEnergy[i] = this.getP(i, maquina) * mach.getKvoltaje(config)
					* mach.getKvoltaje(config) / mach.getKspeed(config);

			energy += taskEnergy[i];

			taskTime[i] = this.getP(i, maquina) / mach.getKspeed(config);

			timeM[maquina] += taskTime[i];

			if (timeM[maquina] > makespan)
				makespan = timeM[maquina];
		}

		double idleEnergy = 0;
		// for (int mach = 0; mach < timeM.length; mach++) {
		// Machine m = this.getMachine(mach);
		// int k = m.getkConfig() - 1;
		// idleEnergy += (makespan - timeM[mach]) * m.getKvoltaje(k)
		// * m.getKvoltaje(k);
		// }

		return new double[] { makespan, energy + idleEnergy };
	}

	public double[] computeObjectives(int[][] assignments) {

		double energy = 0;
		double makespan = 0;
		double timeM[] = new double[this.getTotalMachines()];
		int maquina = 0;
		int config = 0;
		double[] taskEnergy = new double[assignments.length];
		double[] taskTime = new double[assignments.length];

		for (int i = 0; i < assignments.length; i++) {

			maquina = assignments[i][0];
			config = assignments[i][1];

			Machine mach = this.getMachine(maquina);

			taskEnergy[i] = this.getP(i, maquina) * mach.getKvoltaje(config)
					* mach.getKvoltaje(config) / mach.getKspeed(config);

			energy += taskEnergy[i];

			taskTime[i] = this.getP(i, maquina) / mach.getKspeed(config);

			timeM[maquina] += taskTime[i];

			if (timeM[maquina] > makespan)
				makespan = timeM[maquina];
		}

		double idleEnergy = 0;
		// for (int mach = 0; mach < timeM.length; mach++) {
		// Machine m = this.getMachine(mach);
		// int k = m.getkConfig() - 1;
		// idleEnergy += (makespan - timeM[mach]) * m.getKvoltaje(k)
		// * m.getKvoltaje(k);
		// }

		return new double[] { makespan, energy + idleEnergy };
	}
/*
	public double[] computeObjectives(Variable[] assig) throws JMetalException {

		double makespan = 0.0;
		double energy = 0.0;

		double timeInMachine[] = new double[totalMachines];
		int k = 0;
		for (int t = 0; t < totalTasks; t++) {

			k = (int) assig[t + totalTasks].getValue();
			Machine m = this.getMachine((int) assig[t].getValue());

			double P = m.getExecTime(t) / m.getKspeed(k);

			timeInMachine[m.getId()] += P;
			energy += P * m.getKvoltaje(k) * m.getKvoltaje(k);

			if (timeInMachine[m.getId()] > makespan)
				makespan = timeInMachine[m.getId()];

		}
		double idle = 0;
		// for (int i = 0; i < timeInMachine.length; i++) {
		// Machine m = this.getMachine(i);
		// int kConf = m.getkConfig() - 1;
		// idle += (timeInMachine[i] - makespan) * m.getKvoltaje(kConf)
		// * m.getKvoltaje(kConf);
		// }

		return new double[] { makespan, energy + idle };
	}
*/	
	public double[] computeObjectives(SchedulingIntegerSolution solution) throws JMetalException {

		double makespan = 0.0;
		double energy = 0.0;

		double timeInMachine[] = new double[totalMachines];
		int k = 0;
		for (int t = 0; t < totalTasks; t++) {

			k = (int) solution.getVariableValue(t + totalTasks);
			
			
			Machine m = this.getMachine((int) solution.getVariableValue(t));
			
			if(k > m.getTotalConfig()){
				k = m.getTotalConfig();
				solution.setVariableValue(t, k);
			}

			double P = m.getExecTime(t) / m.getKspeed(k);

			timeInMachine[m.getId()] += P;
			energy += P * m.getKvoltaje(k) * m.getKvoltaje(k);

			if (timeInMachine[m.getId()] > makespan)
				makespan = timeInMachine[m.getId()];

		}
		double idle = 0;
		// for (int i = 0; i < timeInMachine.length; i++) {
		// Machine m = this.getMachine(i);
		// int kConf = m.getkConfig() - 1;
		// idle += (timeInMachine[i] - makespan) * m.getKvoltaje(kConf)
		// * m.getKvoltaje(kConf);
		// }

		return new double[] { makespan, energy + idle };
	}
	/*
	public double[] computeObjectivesMachineScheduling(SchedulingIntegerSolution solution) throws JMetalException {

		Variable [] assig = solution.getDecisionVariables();
		double makespan = 0.0;
		double energy = 0.0;

		solution.timeInMachine = new double[(int)(solution.numberOfVariables()/2)];
		int k = 0;
		for (int t = 0; t < totalTasks; t++) {

			k = (int) assig[t + totalTasks].getValue();
			Machine m = this.getMachine((int) assig[t].getValue());

			double P = m.getExecTime(t) / m.getKspeed(k);

			solution.timeInMachine[m.getId()] += P;
			energy += P * m.getKvoltaje(k) * m.getKvoltaje(k);

			if (solution.timeInMachine[m.getId()] > makespan)
				makespan = solution.timeInMachine[m.getId()];

		}
		double idle = 0;
		// for (int i = 0; i < timeInMachine.length; i++) {
		// Machine m = this.getMachine(i);
		// int kConf = m.getkConfig() - 1;
		// idle += (timeInMachine[i] - makespan) * m.getKvoltaje(kConf)
		// * m.getKvoltaje(kConf);
		// }

		return new double[] { makespan, energy + idle };
	}
	
	*/

	/**
	 * 
	 * @param solution
	 *            a Solution with the machines and configurations.
	 * @return a double array containing in the first position the makesapan and
	 *         in the second position the energy.
	 */
	/*public double[] computeMakespanAndEnergy(Solution solution) {
		solution.makespan = 0;
		solution.energy = 0;
		solution.minTime = Integer.MAX_VALUE;
		solution.totalMachinesWithMakespan = 0;
		solution.machineWithMinTime = 0;

		for (int i = 0; i < totalMachines; i++) {

			if (solution.execTimeInMachine[i] > solution.getMakespan()
					&& solution.execTimeInMachine[i] > 0) {
				solution.makespan = solution.execTimeInMachine[i];
				solution.machinesWithMakespan[solution.totalMachinesWithMakespan++] = i;
			}

			if (solution.execTimeInMachine[i] < solution.minTime
					&& solution.execTimeInMachine[i] > 0) {
				solution.minTime = solution.execTimeInMachine[i];
				solution.machineWithMinTime = i;
			}

			solution.energy += solution.energyInMachine[i];
		}
		double idleEnergy = 0;
		// for (int j = 0; j < sol.totalMachines; j++) {
		// Machine m = this.getMachine(j);
		// int k = m.getkConfig() - 1;
		// idleEnergy += (sol.makespan - sol.execTimeInMachine[j]) *
		// m.getKvoltaje(k)
		// * m.getKvoltaje(k);
		// }
		solution.energy += idleEnergy;
		return new double[] { solution.makespan, solution.energy };
	}
*/
	/**
	 * 
	 * @param solution
	 *            a Solution with the machines and configurations.
	 * @return a double array containing in the first position the makesapan and
	 *         in the second position the energy.
	 */
	/*public double[] computeObjectives(Solution solution) {
		
		int k;
		double[] timeInMachine = new double[solution.getTotalMachines()];
		
		Machine m;
		solution.resetObjectives();
		
		for (int i = 0; i < solution.getTotalTasks(); i++) {

			Task task = solution.getGen(i);
			if (task != null) {

				k = task.getkConfig();
				m = this.getMachine(task.getAssignedMachine());

				double P = m.getExecTime(task) / m.getKspeed(k);

				timeInMachine[m.getId()] += P;
				solution.increaseEnergy(P * m.getKvoltaje(k) * m.getKvoltaje(k));

				if (timeInMachine[m.getId()] > solution.getMakespan())
					solution.setMakespan(timeInMachine[m.getId()]);
			}

		}
		double idleEnergy = 0;
		// for (int j = 0; j < sol.totalMachines; j++) {
		// Machine m = this.getMachine(j);
		// int k = m.getkConfig() - 1;
		// idleEnergy += (sol.makespan - sol.execTimeInMachine[j]) *
		// m.getKvoltaje(k)
		// * m.getKvoltaje(k);
		// }
		solution.increaseEnergy(idleEnergy);
		return new double[] { solution.getMakespan(), solution.getEnergy()};
	}
*/
}

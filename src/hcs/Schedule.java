package hcs;

//import nsgaII.Solution;

/**
 * 
 * @author soto190
 * 
 */

public class Schedule {

	private Task[] schedule;

	private double[] execTimeInMachine;
	private double[] energyInMachine;

	private int[] machinesWithMakespan;
	protected int machineWithMinTime;
	private double makespan;
	protected double minTime;
	private double energy;
	private int totalMachinesWithMakespan;

	private int totalTasks;
	private int totalMachines;

	public Schedule(int totaTasks, int totalMachines) {

		makespan = 0;
		energy = 0;

		this.totalTasks = totaTasks;
		this.totalMachines = totalMachines;

		execTimeInMachine = new double[totalMachines];
		energyInMachine = new double[totalMachines];

		machinesWithMakespan = new int[totaTasks];

		schedule = new Task[totaTasks];

	}

	public void setTaskInMachine(Task task, Machine machine, int kConfig) {
		int t = task.getId(), m = machine.getId();

		if (this.schedule[t] == null) {
			
			this.schedule[t] = new Task(t);
			this.schedule[t].setInMachine(machine, kConfig);

			this.execTimeInMachine[m] += this.schedule[t].getCurrentExecTime();
			this.energyInMachine[m] += this.schedule[t].getCurrentEnergy();
			this.energy += this.schedule[t].getCurrentEnergy();
			
		} else
			changeTaskToMachine(this.schedule[t], machine, kConfig);

	}
	 
	/**
	 * 
	 * @param task Tarea a modificar.
	 * @param machine M�quina a usar.
	 * @param kConfig configuraci�n a usar.
	 */
	private void changeTaskToMachine(Task task, Machine machine, int kConfig) {
		
		this.execTimeInMachine[task.getAssignedMachine()] -= task
				.getCurrentExecTime();
		this.energyInMachine[task.getAssignedMachine()] -= task
				.getCurrentEnergy();
		this.energy -= task.getCurrentEnergy();

		this.schedule[task.getId()].setInMachine(machine, kConfig);

		this.execTimeInMachine[task.getAssignedMachine()] += task
				.getCurrentExecTime();
		this.energyInMachine[task.getAssignedMachine()] += task
				.getCurrentEnergy();
		this.energy += task.getCurrentEnergy();
	}

	public int getTotalTasks() {
		return this.totalTasks;
	}

	public int getTotalMachines() {
		return this.totalMachines;
	}

	public double getMakespan() {
		return this.makespan;
	}

	public double getEnergy() {
		return this.energy;
	}

	public int getTotalMachinesWithMakespan() {
		return this.totalMachinesWithMakespan;
	}

	public int getMachineWithMakespan(int index) {
		return this.machinesWithMakespan[index];
	}

	/**
	 * 
	 * @return the makespan and energy.
	 */
	public double[] getObjectives() {

		return new double[] { this.makespan, this.energy };

	}

	public void setMakespan(double makespan) {
		this.makespan = makespan;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public void resetObjectives() {
		this.makespan = 0;
		this.energy = 0;
	}

	public double increaseEnergy(double increment) {
		this.energy += increment;
		return this.energy;
	}

	/*public int compare(Solution sol1, Solution sol2) {
		return 0;
	}*/

}

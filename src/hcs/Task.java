package hcs;

/**
 * 
 * This class represents a Task from the HCSP ( Heterogenous Computing System
 * Problem).
 * 
 * @author soto190
 * 
 * 
 * 
 */
public class Task {

	private int id = 0;
	private int assignedMachine = -1;
	private double currentExecTime = -1;
	private double energyConsumed = -1;
	private int kConfig = -1;

	/**
	 * Creates a task with the id.
	 * 
	 * @param id
	 *            Integer with the id of the task.
	 */
	public Task(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return Integer with the task Id.
	 */
	public int getId() {
		return this.id;
	}

	public void setInMachine(Machine machine, int kConfig) {
		this.assignedMachine = machine.getId();
		this.currentExecTime = machine.getExecTime(this)
				/ machine.getKspeed(kConfig);
		this.energyConsumed = currentExecTime * machine.getKvoltaje(kConfig)
				* machine.getKvoltaje(kConfig);
		this.kConfig = kConfig;
	}

	/**
	 * Depends of the machine assigned.
	 * 
	 * @return Double with the current execution time of the task.
	 */
	public double getCurrentExecTime() {
		return this.currentExecTime;
	}

	/**
	 * 
	 * @param machine
	 *            The machine in which will be execute the current task.
	 * @param kConfig
	 *            The configuration to be used to execute the current task in
	 *            the assigend machine.
	 */
	public void setCurrentExecTime(Machine machine, int kConfig) {
		currentExecTime = machine.getExecTime(this)
				/ machine.getKspeed(kConfig);
	}

	/**
	 * 
	 * @return Id of the assigned machine.
	 */
	public int getAssignedMachine() {
		return this.assignedMachine;
	}

	/**
	 * Assigns the machin in which this task will be execute.
	 * 
	 * @param machine
	 */
	public void setAssingedMachine(Machine machine) {
		this.assignedMachine = machine.getId();
	}

	public void setAssingedMachine(int machine) {
		this.assignedMachine = machine;
	}

	/**
	 * 
	 * @return Integer with the k configurations.
	 */

	public int getkConfig() {
		return this.kConfig;
	}

	public void setkConfig(int kConfig) {
		this.kConfig = kConfig;
	}

	public double getCurrentEnergy() {
		return this.energyConsumed;
	}

	public void copyThis(Task task) {
		this.assignedMachine = task.getAssignedMachine();
		this.currentExecTime = task.getCurrentExecTime();
		this.energyConsumed = task.getCurrentEnergy();
		this.kConfig = task.getkConfig();
	}

	public String toString() {

		return String
				.format("Task [id=%4d, assignedMachine=%2d, currentExecTime=%6.6f, kConfig=%2d]",
						id, assignedMachine, currentExecTime, kConfig);
	}

}

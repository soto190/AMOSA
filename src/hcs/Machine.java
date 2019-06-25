package hcs;

/**
 * 
 * @author soto190
 * 
 */
public class Machine {

	private int id;
	private int totalConfig;
	/**
	 * protected double totalExecTime = 0; protected double totalEnergy = 0;
	 **/
	private double[] voltaje;
	private double[] speeds;

	private double[] taskExecTime;
	
	private boolean lockKConfig = false;

	/**
	 * protected int totalTask = 0;
	 * 
	 * protected Task[] assignedTask = new Task[512];
	 **/

	public Machine(int id) {
		this.id = id;
	}

	public Machine(int id, int totalTasks) {
		this.id = id;
		this.taskExecTime = new double[totalTasks];
	}
	
	/**
	 * Create a new machine with the Voltaje and Speed configurations of the given machine,
	 * @param id
	 * @param machine
	 */

	private Machine(int id, Machine machine) {
		this.id = id;
		this.totalConfig = machine.getTotalConfig();

		for (int k = 0; k < machine.getTotalConfig(); k++)
			this.setKConfigVoltajeAndSpeed(k, machine.getKvoltaje(k),
					machine.getKspeed(k));

	}

	public Machine(int id, int totalTasks, int kConfig) {
		this.id = id;
		this.taskExecTime = new double[totalTasks];
		this.totalConfig = kConfig;
		this.voltaje = new double[kConfig];
		this.speeds = new double[kConfig];
	}
	
	public int getId() {
		return id;
	}

	public int getTotalConfig() {
		return totalConfig;
	}
	
	public void setTotalConfig(int kConfig) {
		if (!lockKConfig) {
			this.totalConfig = kConfig;
			lockKConfig = true;
		}
	}
	
	public void setMaxKConfig(int kConfig){
		this.totalConfig = kConfig;
		this.voltaje = new double[kConfig];
		this.speeds = new double[kConfig];
	}

	public void addTask(int taskId, double execTime) {
		this.taskExecTime[taskId] = execTime;
	}

	public double getExecTime(Task task) {
		return taskExecTime[task.getId()];
	}

	public double getExecTime(int task) {
		return taskExecTime[task];
	}

	public double[] getVoltaje() {
		return voltaje;
	}

	public void setVoltaje(double[] voltaje) {
		for (int i = 0; i < voltaje.length; i++)
			this.voltaje[i] = voltaje[i];
	}

	public double[] getSpeeds() {
		return speeds;
	}

	public double getKspeed(int k) {
		return speeds[k];
	}
	
	public int getTotalKConfig() {
		return totalConfig;
	}

	public void setKspeed(int k, double speed) {
		this.speeds[k] = speed;
	}

	public double getKvoltaje(int k) {
		return voltaje[k];
	}

	public void setKvoltaje(int k, double voltaje) {
		this.voltaje[k] = voltaje;
	}

	public void setSpeeds(double[] speeds) {
		for (int i = 0; i < speeds.length; i++)
			this.speeds[i] = speeds[i];
	}

	public void setKConfigVoltajeAndSpeed(int k, double voltaje, double speed) {
		this.speeds[k] = speed;
		this.voltaje[k] = voltaje;

	}

	public void copyKConfig(int k, Machine machine) {
		this.setKConfigVoltajeAndSpeed(k, machine.getKvoltaje(k),
				machine.getKspeed(k));
	}
	
	public boolean lockedConfig(){
		return this.lockKConfig;
	}

	/**
	 * public Task[] getAssignedTask() { return assignedTask; }
	 * 
	 * public void setAssignedTask(Task[] assignedTask) { for (int i = 0; i <
	 * assignedTask.length; i++) this.assignedTask[i] = assignedTask[i]; }
	 **/

	public String toString() {
		return "Machine [id=" + id + ", kConfig=" + totalConfig + "]";
	}

}

package tools;

/**
 * This class represents an Instance for HCSP.
 * 
 * @author soto190
 * @version 1.0
 * @since September 2013.
 * 
 * 
 * 
 */
public class InstanceTS {

	/**
	 * t total tasks m total machines c total configurations voltages and speeds
	 * p Pij edges indexEdges voltage speed
	 */


	private int t, m, c, nEdges;
	private int A[], edges[][], indexEdges[];
	private double voltage[][], speed[][], p[][];
	private String name;

	/**
	 * This class represents an instance for the HCSP.
	 */

	public InstanceTS(){
		
	}
	public InstanceTS(String name) {
		setName(name);
	}

	/**
	 * This class represents an instance for the HCSP.
	 * 
	 * @param t
	 *            number of tasks.
	 * @param m
	 *            number of machines.
	 *            Processing costs of task i in machine j.
	 *            
	 *            
	 */
	InstanceTS(int t, int m) {
		this.t = t;
		this.m = m;
		this.p = new double[t][m];
		// this.voltage = new int[c][m];
		// this.speed = new int[c][m];
	}

	InstanceTS(int t, int m, double[][] p) {
		this.t = t;
		this.m = m;
		this.p = p.clone();
	}

	InstanceTS(int t, int m, int c, double[][] p, double[][] volt, double[][] speed) {
		this.t = t;
		this.m = m;
		this.c = c;
		this.p = p.clone();
		this.voltage = volt.clone();
		this.speed = speed.clone();
	}

	InstanceTS(int t, int m, int c, int nEdges, double[][] p, int[][] edges,
			int[] indexEdges, int[] A, double[][] volt, double[][] speed) {
		this.t = t;
		this.m = m;
		this.c = c;
		this.p = p.clone();
		this.nEdges = nEdges;
		this.edges = edges;
		this.indexEdges = indexEdges;
		this.A = A;
		this.voltage = volt;
		this.speed = speed;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the total of task.
	 */
	public int getT() {
		return t;
	}

	/**
	 * @param t
	 *            the t to set
	 */
	public void setT(int t) {
		this.t = t;
	}

	/**
	 * @return the total of machines
	 */
	public int getM() {
		return m;
	}

	/**
	 * @param m
	 *            the m to set
	 */
	public void setM(int m) {
		this.m = m;
	}

	/**
	 * @return the c. Total configurations.
	 */
	public int getC() {
		return c;
	}

	/**
	 * @param c
	 *            the c to set
	 */
	public void setC(int c) {
		this.c = c;
	}

	/**
	 * @return the nEdges
	 */
	public int getnEdges() {
		return nEdges;
	}

	/**
	 * @param nEdges
	 *            the nEdges to set
	 */
	public void setnEdges(int nEdges) {
		this.nEdges = nEdges;
	}

	/**
	 * @return the matrix of cost Pij.
	 */
	public double[][] getP() {
		return p;
	}

	/**
	 * @return the cost Pij.
	 */
	public double[] getP(int task) {
		return p[task];
	}

	/**
	 * @return the cost of Pij.
	 */
	public double getP(int task, int machine) {
		return p[task][machine];
	}

	/**
	 * @param p
	 *            the p to set
	 */
	public void setP(double[][] p) {
		this.p = p.clone();
	}

	public void setP(int task, int machine, double Pij) {
		this.p[task][machine] = Pij;
	}

	/**
	 * @return the edges
	 */
	public int[][] getEdges() {
		return edges;
	}

	/**
	 * @return A
	 */
	public int[] getA() {
		return A;
	}

	/**
	 * @param edges
	 *            the edges to set
	 */
	public void setEdges(int[][] edges) {
		this.edges = edges;
	}

	/**
	 * @return the indexEdges
	 */
	public int[] getIndexEdges() {
		return indexEdges;
	}

	/**
	 * @param indexEdges
	 *            the indexEdges to set
	 */
	public void setIndexEdges(int[] indexEdges) {
		this.indexEdges = indexEdges;
	}

	/**
	 * @return the voltaje
	 */
	public double[][] getVoltaje() {
		return voltage;
	}

	/**
	 * @param voltaje
	 *            the voltaje to set
	 */
	public void setVoltaje(double[][] voltaje) {
		this.voltage = voltaje.clone();
	}

	/**
	 * @return the speed
	 */
	public double[][] getSpeed() {
		return speed;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setSpeed(double[][] speed) {
		this.speed = speed.clone();
	}

	/**
	 * @param A
	 *            the A to set
	 */
	public void setA(int[] A) {
		this.A = A;
	}

	/**
	 * 
	 * @param u
	 *            node u
	 * @param v
	 *            node v
	 * @return true if exist an edge.
	 */
	public boolean existEdge(int u, int v) {

		int startIndex = indexEdges[u - 1];

		/**
		 * int endIndex = u < indexEdges.length ? indexEdges[u] : edges.length;
		 * for (int i = startIndex; i < endIndex; i++) if (edges[i][0] == u &&
		 * edges[i][1] == v) return true;
		 **/

		while (startIndex < edges.length && edges[startIndex][0] == u)
			if (edges[startIndex++][1] == v)
				return true;

		return false;
	}

}

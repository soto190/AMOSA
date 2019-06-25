package org.uma.jmetal.util.archive.impl;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;

/**
 * Archive for the
 * {@linkplain org.uma.jmetal.algorithm.multiobjective.mosa.AMOSA AMOSA}
 * algorithm.
 * 
 * 
 *
 * @author José Carlos Soto Monterrubio <soto190@gmail.com>
 *
 * @param <S>
 *            type of solution.
 * 
 * @see org.uma.jmetal.algorithm.multiobjective.mosa
 *
 */
public class ArchiveMOSA<S extends Solution<?>> implements Archive<S> {

	private static final long serialVersionUID = 1L;
	private ArrayList<S> solutionList;
	protected int hardLimit;
	protected int softLimit;

	/**
	 * 
	 * @param hardLimit
	 *            the maximum size of the Archive on termination. This set is
	 *            equal to the maximun number of nondominated solutions
	 *            requierde by the user.
	 * @param softLimit
	 *            the maximum size to which the Archive may be filled before
	 *            clustering is used its size to hardLimit.
	 */
	public ArchiveMOSA(int hardLimit, int softLimit) {
		this.hardLimit = hardLimit;
		this.softLimit = softLimit;
		this.solutionList = new ArrayList<S>(softLimit);
	}

	public boolean add(S solution) {
		solutionList.add(solution);
		return true;
	}

	public boolean remove(S solution) {
		solutionList.remove(solution);
		return true;
	}

	public S get(int index) {
		return this.solutionList.get(index);
	}

	public List<S> getSolutionList() {
		return this.solutionList;
	}

	public int size() {
		return solutionList.size();
	}

	public int getHardLimit() {
		return hardLimit;
	}

	public int getSoftLimit() {
		return softLimit;
	}

	public void clustering() {
		if (solutionList.size() > this.hardLimit)
			singleLinkageclustering(solutionList);
	}

	private void singleLinkageclustering(List<S> population) {
		System.out.println("Clustering...");

		double[][] distance = new double[population.size()][population.size()];
		int totalClusters = population.size();
		int totalObj = population.get(0).getNumberOfObjectives();
		double min = Double.MAX_VALUE;
		int minIdxI = 0;
		int minIdxJ = 0;
		int[] cluster = new int[population.size()];

		ArrayList<S> newPopulaiton = new ArrayList<S>(this.hardLimit);

		ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>(population.size());

		for (int solI = 0; solI < distance.length; solI++) {
			cluster[solI] = solI;
			ArrayList<Integer> temp = new ArrayList<Integer>(this.getHardLimit());
			temp.add(solI);
			clusters.add(solI, temp);
			for (int solJ = solI + 1; solJ < distance.length; solJ++) {
				for (int nObj = 0; nObj < totalObj; nObj++)
					distance[solI][solJ] += Math
							.pow(population.get(solI).getObjective(nObj) - population.get(solJ).getObjective(nObj), 2);

				distance[solI][solJ] = Math.sqrt(distance[solI][solJ]);
				distance[solJ][solI] = distance[solI][solJ];
				if (distance[solI][solJ] < min) {
					min = distance[solI][solJ];
					minIdxI = solI;
					minIdxJ = solJ;
				}
			}
		}
		cluster[minIdxJ] = cluster[minIdxI];
		totalClusters--;
		clusters.get(minIdxI).addAll(clusters.get(minIdxJ));
		clusters.get(minIdxJ).clear();

		while (totalClusters > this.hardLimit) {
			min = Double.MAX_VALUE;
			for (int solI = 0; solI < distance.length; solI++)
				for (int solJ = solI + 1; solJ < distance.length - 1; solJ++)
					if (distance[solI][solJ] < min && cluster[solI] != cluster[solJ]) {
						min = distance[solI][solJ];
						minIdxI = solI;
						minIdxJ = solJ;
					}

			int oldCluster = cluster[minIdxJ];

			for (int idx = 0; idx < cluster.length; idx++)
				if (cluster[idx] == oldCluster && totalClusters > this.hardLimit) {
					cluster[idx] = cluster[minIdxI];
					clusters.get(cluster[minIdxI]).addAll(clusters.get(idx));
					clusters.get(idx).clear();
				}
			totalClusters--;

		}

		/**
		 * Get the representative member. The solution with min average
		 * distance.
		 **/
		for (ArrayList<Integer> clust : clusters) {
			if (clust.size() == 1 || clust.size() == 2)
				newPopulaiton.add(population.get(clust.get(0)));
			else if (clust.size() > 2) {
				double minAvg = Double.MAX_VALUE;
				int minIdx = clust.get(0);
				for (Integer cOr : clust) {
					double avg = 0;
					for (Integer cDe : clust)
						avg += distance[cOr][cDe];
					avg = avg / (clust.size() - 1);
					if (avg < minAvg) {
						minAvg = avg;
						minIdx = cOr;
					}
				}
				newPopulaiton.add(population.get(minIdx));
			}
		}

		this.solutionList = newPopulaiton;

	}

}

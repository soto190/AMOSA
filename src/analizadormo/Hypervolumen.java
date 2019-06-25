/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadormo;

import java.util.ArrayList;

/**
 *
 * @author Alx
 */
public class Hypervolumen {

	public static double Compute(ArrayList<double[]> frente_aproximado) {
		// primer paso invertir el frente aproximado
		double[][] front = new double[frente_aproximado.size()][AnalizadorMO.objetivos];
		for (int x = 0; x < frente_aproximado.size(); x++) {
			for (int j = 0; j < AnalizadorMO.objetivos; j++) {
				front[x][j] = 1.0 - frente_aproximado.get(x)[j];
			}
		}

		// segundo paso calcular hypervolumen para maximización
		double valor = CalculateHypervolume(front, frente_aproximado.size(), AnalizadorMO.objetivos);
		return valor;
	}

	static double CalculateHypervolume(double[][] front, int noPoints, int noObjectives) {
		int n;
		double volume, distance;

		volume = 0;
		distance = 0;
		n = noPoints;
		while (n > 0) {
			int noNondominatedPoints;
			double tempVolume, tempDistance;

			noNondominatedPoints = FilterNondominatedSet(front, n, noObjectives - 1);
			tempVolume = 0;
			if (noObjectives < 3) {
				if (noNondominatedPoints < 1)
					System.out.println("run-time error");
				tempVolume = front[0][0];
			} else
				tempVolume = CalculateHypervolume(front, noNondominatedPoints, noObjectives - 1);
			tempDistance = SurfaceUnchangedTo(front, n, noObjectives - 1);
			volume += tempVolume * (tempDistance - distance);
			distance = tempDistance;
			n = ReduceNondominatedSet(front, n, noObjectives - 1, distance);
		}
		return volume;

	}

	/*
	 * returns true if 'point1' dominates 'points2' with respect to the to the
	 * first 'noObjectives' objectives
	 */
	static boolean dominates(double point1[], double point2[], int noObjectives) {
		int i;
		int betterInAnyObjective;

		betterInAnyObjective = 0;
		for (i = 0; i < noObjectives && point1[i] >= point2[i]; i++)
			if (point1[i] > point2[i])
				betterInAnyObjective = 1;

		return ((i >= noObjectives) && (betterInAnyObjective > 0));
	} // Dominates

	static void swap(double[][] front, int i, int j) {
		double[] temp;

		temp = front[i];
		front[i] = front[j];
		front[j] = temp;
	} // Swap

	/*
	 * all nondominated points regarding the first 'noObjectives' dimensions are
	 * collected; the points referenced by 'front[0..noPoints-1]' are
	 * considered; 'front' is resorted, such that 'front[0..n-1]' contains the
	 * nondominated points; n is returned
	 */
	static int FilterNondominatedSet(double[][] front, int noPoints, int noObjectives) {
		int i, j;
		int n;

		n = noPoints;
		i = 0;
		while (i < n) {
			j = i + 1;
			while (j < n) {
				if (dominates(front[i], front[j], noObjectives)) {
					/* remove point 'j' */
					n--;
					swap(front, j, n);
				} else if (dominates(front[j], front[i], noObjectives)) {
					/*
					 * remove point 'i'; ensure that the point copied to index
					 * 'i' is considered in the next outer loop (thus, decrement
					 * i)
					 */
					n--;
					swap(front, i, n);
					i--;
					break;
				} else
					j++;
			}
			i++;
		}
		return n;
	} // FilterNondominatedSet

	/*
	 * calculate next value regarding dimension 'objective'; consider points
	 * referenced in 'front[0..noPoints-1]'
	 */
	static double SurfaceUnchangedTo(double[][] front, int noPoints, int objective) {
		int i;
		double minValue, value;

		if (noPoints < 1)
			System.err.println("run-time error");

		minValue = front[0][objective];
		for (i = 1; i < noPoints; i++) {
			value = front[i][objective];
			if (value < minValue)
				minValue = value;
		}
		return minValue;
	} // SurfaceUnchangedTo

	/*
	 * remove all points which have a value <= 'threshold' regarding the
	 * dimension 'objective'; the points referenced by 'front[0..noPoints-1]'
	 * are considered; 'front' is resorted, such that 'front[0..n-1]' contains
	 * the remaining points; 'n' is returned
	 */
	static int ReduceNondominatedSet(double[][] front, int noPoints, int objective, double threshold) {
		int n;
		int i;

		n = noPoints;
		for (i = 0; i < n; i++)
			if (front[i][objective] <= threshold) {
				n--;
				swap(front, i, n);
			}

		return n;
	} // ReduceNondominatedSet

}

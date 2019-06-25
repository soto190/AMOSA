/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadormo;

import java.util.ArrayList;

/**
 *
 * @author alx
 */
public class Comparador {

	public static void agrega(ArrayList<double[]> a, double arreglo[]) {
		int dominancia = 0;
		for (int x = 0; x < a.size(); x++) {
			if (dominancia(a.get(x), arreglo)) {
				dominancia = 1;
			}
		}

		if (dominancia == 0) {
			for (int x = 0; x < a.size(); x++) {
				if (dominancia_y_repetida(arreglo, a.get(x))) {
					a.remove(x);
					x--;
				}
			}
			a.add(arreglo);
		}
	}

	public static boolean dominancia(double a[], double b[]) {

		int bandera = 0;
		int i = 0;
		for (i = 0; i < a.length && a[i] <= b[i]; i++) {
			if (a[i] < b[i]) { // minimizar
				bandera = 1; // mejor en algun objetivo
			}
		}
		if (bandera == 1 && i >= a.length)
			return true;
		else
			return false;
	} // compare

	public static boolean dominancia_y_repetida(double a[], double b[]) {

		int bandera = 1;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i])
				bandera = 0;
		}
		if (bandera == 1)
			return true; // quiere decir que es repetida

		bandera = 0;
		int i = 0;
		for (i = 0; i < a.length && a[i] <= b[i]; i++) {
			if (a[i] < b[i]) { // minimizar
				bandera = 1; // mejor en algun objetivo
			}
		}
		if (bandera == 1 && i >= a.length)
			return true;
		else
			return false;
	} // compare

	public static boolean tiene_referencia(double punto[], double refpoint[]) {
		for (int i = 0; i < refpoint.length; i++) {
			if (punto[i] == refpoint[i]) {
				return true;
			}
		}
		return false;
	}

}

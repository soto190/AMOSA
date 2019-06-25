/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadormo;

/**
 *
 * @author Alx
 **/
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class AnalizadorMO {

	/**
	 * @param args
	 *            the command line arguments
	 */
	static ArrayList<ArrayList<ArrayList<double[]>>> datos;
	public static ArrayList<double[]> frente_real;
	static int objetivos, algoritmos, iteraciones;
	static String problema;

	public static void main(String[] args) throws IOException {
		FileReader fr_main = new FileReader("/Problemas/Problemas.txt");
		BufferedReader bf_main = new BufferedReader(fr_main);
		int contador = 0;
		algoritmos = 3;
		Latex.Inicializa_latex_igd();
		Latex.Inicializa_latex_GS();
		Latex.Inicializa_latex_HV();
		Latex.Inicializa_latex_Epsilon();
		Latex.Inicializa_latex_igd_plus();
		Estadisticos.Inicializa_scripts_R();
		while (bf_main.ready()) {
			contador++;
			StringTokenizer st_names = new StringTokenizer(bf_main.readLine());
			problema = st_names.nextToken();
			System.out.println("Problema " + contador + " " + problema);
			FileReader fr = new FileReader(st_names.nextToken());
			BufferedReader bf = new BufferedReader(fr);
			StringTokenizer st = new StringTokenizer(bf.readLine());
			algoritmos = Integer.valueOf(st.nextToken());
			iteraciones = Integer.valueOf(st.nextToken());
			objetivos = Integer.valueOf(st.nextToken());
			System.out.println("Valor objetivos " + objetivos);
			datos = new ArrayList();
			frente_real = new ArrayList<double[]>();
			FileReader fr2 = new FileReader(bf.readLine());
			BufferedReader bf2 = new BufferedReader(fr2);
			while (bf2.ready()) {
				String cadena = bf2.readLine();
				if (!cadena.isEmpty() && cadena.compareTo("") != 0) {
					StringTokenizer st2 = new StringTokenizer(cadena);
					double temp[] = new double[objetivos];
					for (int x = 0; x < objetivos; x++) {
						temp[x] = Double.parseDouble(st2.nextToken());
					}
					if (frente_real.isEmpty())
						frente_real.add(temp);
					else {
						Comparador.agrega(frente_real, temp);
					}
				}
			}
			bf2.close();
			fr2.close();

			for (int w = 0; w < algoritmos; w++) {
				ArrayList<ArrayList<double[]>> datos_alg = new ArrayList<ArrayList<double[]>>();
				for (int j = 0; j < iteraciones; j++) {
					ArrayList<double[]> frente_aproximado = new ArrayList<double[]>();
					fr2 = new FileReader(bf.readLine());
					bf2 = new BufferedReader(fr2);
					while (bf2.ready()) {
						StringTokenizer st2 = new StringTokenizer(bf2.readLine());
						double temp[] = new double[objetivos];
						for (int x = 0; x < objetivos; x++) {
							temp[x] = Double.parseDouble(st2.nextToken());
						}
						if (frente_aproximado.isEmpty())
							frente_aproximado.add(temp);
						else {
							Comparador.agrega(frente_aproximado, temp);
						}
					}
					bf2.close();
					fr2.close();
					datos_alg.add(frente_aproximado);
				}
				datos.add(datos_alg);
			}

			double maximos[] = new double[objetivos];
			double minimos[] = new double[objetivos];
			// saca los extremos
			MaxMin.extremos(maximos, minimos);
			// Normaliza todos los datos
			MaxMin.normaliza(maximos, minimos);

			// computa los indicadores de calidad para cada ejecución :S
			ArrayList<Double> IGD_mean = new ArrayList<Double>();
			ArrayList<Double> IGD_desvest = new ArrayList<Double>();
			ArrayList<Double> IGD_plus_mean = new ArrayList<Double>();
			ArrayList<Double> IGD_plus_desvest = new ArrayList<Double>();
			ArrayList<Double> GS_mean = new ArrayList<Double>();
			ArrayList<Double> GS_desvest = new ArrayList<Double>();
			ArrayList<Double> HV_mean = new ArrayList<Double>();
			ArrayList<Double> HV_desvest = new ArrayList<Double>();
			ArrayList<Double> Epsilon_mean = new ArrayList<Double>();
			ArrayList<Double> Epsilon_desvest = new ArrayList<Double>();

			for (int x = 0; x < algoritmos; x++) {
				ArrayList<Double> temp_igd = new ArrayList<Double>();
				ArrayList<Double> temp_igd_plus = new ArrayList<Double>();
				ArrayList<Double> temp_gs = new ArrayList<Double>();
				ArrayList<Double> temp_hv = new ArrayList<Double>();
				ArrayList<Double> temp_epsilon = new ArrayList<Double>();
				for (int j = 0; j < iteraciones; j++) {

					double valor = InvertedGenerationalDistance.Compute(frente_real, AnalizadorMO.datos.get(x).get(j));
					temp_igd.add(valor);
					valor = GeneralizedSpread.Compute(frente_real, AnalizadorMO.datos.get(x).get(j));
					temp_gs.add(valor);
					valor = Hypervolumen.Compute(AnalizadorMO.datos.get(x).get(j));
					temp_hv.add(valor);
					valor = Epsilon.Compute_additive(frente_real, AnalizadorMO.datos.get(x).get(j));
					temp_epsilon.add(valor);
					valor = IGDplus.Compute(frente_real, AnalizadorMO.datos.get(x).get(j));
					temp_igd_plus.add(valor);
				}
				IGD_mean.add(PromedioDesvEst.Promedio(temp_igd));
				IGD_desvest.add(PromedioDesvEst.DesviacionEstandar(temp_igd));
				EscrituraArchivo.Escribe_Archivo(temp_igd, problema + "_IGD_Alg" + x);
				IGD_plus_mean.add(PromedioDesvEst.Promedio(temp_igd_plus));
				IGD_plus_desvest.add(PromedioDesvEst.DesviacionEstandar(temp_igd_plus));
				EscrituraArchivo.Escribe_Archivo(temp_igd_plus, problema + "_IGDPlus_Alg" + x);
				GS_mean.add(PromedioDesvEst.Promedio(temp_gs));
				GS_desvest.add(PromedioDesvEst.DesviacionEstandar(temp_gs));
				EscrituraArchivo.Escribe_Archivo(temp_gs, problema + "_GS_Alg" + x);
				HV_mean.add(PromedioDesvEst.Promedio(temp_hv));
				HV_desvest.add(PromedioDesvEst.DesviacionEstandar(temp_hv));
				EscrituraArchivo.Escribe_Archivo(temp_hv, problema + "_HV_Alg" + x);
				Epsilon_mean.add(PromedioDesvEst.Promedio(temp_epsilon));
				Epsilon_desvest.add(PromedioDesvEst.DesviacionEstandar(temp_epsilon));
				EscrituraArchivo.Escribe_Archivo(temp_epsilon, problema + "_Epsilon_Alg" + x);

			}
			Estadisticos.Imprime_scripts_R(problema); // ESTADISTICOS POR
														// PROBLEMA

			System.out.println("Imprime los promedios de IGD de cada algoritmo");
			for (int x = 0; x < IGD_mean.size(); x++) {
				System.out.print(IGD_mean.get(x) + " ");
			}
			System.out.println();
			System.out.println("Imprime los promedios de GS de cada algoritmo");
			for (int x = 0; x < IGD_mean.size(); x++) {
				System.out.print(GS_mean.get(x) + " ");
			}
			System.out.println();
			System.out.println("Imprime los promedios de HV de cada algoritmo");
			for (int x = 0; x < IGD_mean.size(); x++) {
				System.out.print(HV_mean.get(x) + " ");
			}
			System.out.println();
			System.out.println("Imprime los promedios de Epsilon de cada algoritmo");
			for (int x = 0; x < IGD_mean.size(); x++) {
				System.out.print(Epsilon_mean.get(x) + " ");
			}
			System.out.println();
			Latex.printQualityIndicator_IGD(IGD_mean, IGD_desvest);
			Latex.printQualityIndicator_IGD_plus(IGD_plus_mean, IGD_plus_desvest);
			Latex.printQualityIndicator_GS(GS_mean, GS_desvest);
			Latex.printQualityIndicator_HV(HV_mean, HV_desvest);
			Latex.printQualityIndicator_Epsilon(Epsilon_mean, Epsilon_desvest);

		}

		Latex.Termina_latex_IGD();
		Latex.Termina_latex_IGD_plus();
		Latex.Termina_latex_GS();
		Latex.Termina_latex_HV();
		Latex.Termina_latex_Epsilon();
		Estadisticos.Termina_scripts_R();
	}

}

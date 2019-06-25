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
public class GeneralizedSpread {
    
    public static double Compute(ArrayList<double[]> frente_real,ArrayList<double[]> frente_aproximado)
    {
        //ENCUENTRA LAS SOLUCIONES EXTREMAS e_i
        int soluciones_extremas[]=new int[frente_real.get(0).length];
        double minimo;
        int pos;
        for (int j = 0; j < frente_real.get(0).length; j++) //objetivos
        {
            minimo = Double.MAX_VALUE;
            pos = 0;
            for (int x = 0; x < frente_real.size(); x++) //soluciones en real
            {
                if (frente_real.get(x)[j] < minimo) {
                    minimo = frente_real.get(x)[j];
                    pos = x;
                }

            }
            soluciones_extremas[j] = pos;
        }
        
        //CALCULA SUMA DISTANCIAS EXTREMOS A S
        double distancia_min,distancia,distancia_a_extremos,distancia_promedio;
        distancia_a_extremos=0;
        for(int j=0;j<soluciones_extremas.length;j++)
        {
            distancia_min=Double.MAX_VALUE;
            for(int x=0;x<frente_aproximado.size();x++)
            {
                distancia=0;
                for(int w=0;w<frente_aproximado.get(x).length;w++)
                {
                    distancia+=Math.pow(frente_real.get(soluciones_extremas[j])[w]-frente_aproximado.get(x)[w],2);
                }
                distancia=Math.sqrt(distancia);
                if(distancia<distancia_min)
                    distancia_min=distancia;
            }
            distancia_a_extremos+=distancia_min;
        }
        
        //PROMEDIO DISTANCIA FRENTE A APROXIMACION
        distancia_promedio=0;
        double distancias_real_a_aproximado[]=new double[frente_real.size()];
        for(int x=0;x<frente_real.size();x++)
        {
            distancia_min=Double.MAX_VALUE;
            for(int j=0;j<frente_aproximado.size();j++)
            {
                distancia=0;
                for(int w=0;w<frente_aproximado.get(j).length;w++)
                {
                    distancia+=Math.pow(frente_real.get(x)[w]-frente_aproximado.get(j)[w],2);
                }
                distancia=Math.sqrt(distancia);
                if(distancia<distancia_min)
                    distancia_min=distancia;
            }
            distancia_promedio+=distancia_min;
            distancias_real_a_aproximado[x]=distancia_min;
            
        }
        
        distancia_promedio=(distancia_promedio/frente_real.size());
        
        double spread;
        spread=distancia_a_extremos;//primer termino de suma superior
        for(int x=0;x<frente_real.size();x++)//segundo termino de suma superior
        {
            spread+=Math.abs(distancias_real_a_aproximado[x]-distancia_promedio);
        }
        
        spread=(spread/(distancia_a_extremos+(frente_real.size()*distancia_promedio)));
        return spread;
    }
    
}

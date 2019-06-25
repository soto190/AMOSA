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
public class PromedioDesvEst {
    
    
    public static double Promedio(ArrayList <Double> Indicador)
    {
        double promedio=0;
        for(int x=0;x<Indicador.size();x++)
        {
            promedio+=Indicador.get(x);
        }
        promedio=(promedio/Indicador.size());
        return promedio;
    }
    
    public static double DesviacionEstandar(ArrayList <Double> Indicador)        
    {
       double promedio=0;
       double varianza=0;
        for(int x=0;x<Indicador.size();x++)
        {
            promedio+=Indicador.get(x);
        }
        promedio=(promedio/Indicador.size());
         for(int x=0;x<Indicador.size();x++)
        {
            varianza+=Math.pow(Indicador.get(x)-promedio,2);
        }
        varianza=(varianza/Indicador.size());
        return Math.sqrt(varianza);
    }
}

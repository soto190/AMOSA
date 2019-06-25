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
public class Epsilon {
    
    public static double Compute_additive(ArrayList<double[]> frente_real, ArrayList<double[]> frente_aproximado)
    {
        double max_diff=0.0;
        for(int i=0;i<frente_real.size();i++) //la diferencia más grande (Epsilon minimo necesario a cumplir)
        {
            double min_diff=Double.MAX_VALUE;
            for(int j=0;j<frente_aproximado.size();j++)//la diferencia más chica (por lo menos e-dominanr una)
            {
                double diff=0,temp;
                for(int w=0;w<AnalizadorMO.objetivos;w++) //la diferencia más grande en objetivos
                {
                    temp=frente_aproximado.get(j)[w]-frente_real.get(i)[w];
                    if(temp>diff)
                        diff=temp;
                }
                if(diff<min_diff)
                    min_diff=diff;
            }
            if(min_diff>max_diff)
                max_diff=min_diff;
        }
        return max_diff;
    }
    
    
    
}

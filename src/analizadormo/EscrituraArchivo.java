/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadormo;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Alx
 */
public class EscrituraArchivo {
    public static void Escribe_Archivo(ArrayList <Double> Indicador,String nombre) throws FileNotFoundException, IOException
    {
        //mdkir problema!!
        File directorio = new File("data/"+AnalizadorMO.problema);
        directorio.mkdirs();
        FileOutputStream fos=new FileOutputStream("data/"+AnalizadorMO.problema+"/"+nombre);
        PrintStream ps=new PrintStream(fos);
        for(int x=0;x<Indicador.size();x++)
        {
            ps.println(Indicador.get(x));
        }
        ps.close();
        fos.close();

    }
}

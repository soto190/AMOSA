/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadormo;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.text.DecimalFormat;

/**
 *
 * @author Alx
 */
public class Latex {
    
    
 public static void printQualityIndicator_IGD(ArrayList <Double> Indicator_mean,ArrayList <Double> Indicator_desvest) throws IOException {
    FileOutputStream fos = new FileOutputStream("IGD.tex", true);
    PrintStream os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    double menor=Double.MAX_VALUE;
    int pos=-1;
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(Indicator_mean.get(x)<menor)
        {
            menor=Indicator_mean.get(x);
            pos=x;
        }
    }
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
    if(x!=pos)    
     os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
    else
     os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
   
    }
    if(pos==AnalizadorMO.algoritmos-1)
    os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");
    else
    os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");    
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();
    
    /*
    fos = new FileOutputStream("IGD_variance.tex", true);
    os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
     os.print(new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +" &");   
    }
    os.print(new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1)));
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();*/
  }
    
    
 public static void Termina_latex_IGD() throws FileNotFoundException, IOException
 {
    FileOutputStream fos = new FileOutputStream("IGD.tex", true);
    PrintStream os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();
    /*
    fos = new FileOutputStream("IGD_variance.tex", true);
    os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();*/
 }
    
  public static void Inicializa_latex_igd() throws FileNotFoundException, IOException
  {
    //PROMEDIO
    FileOutputStream fos = new FileOutputStream("IGD.tex", false);
    PrintStream os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Inverted Generational Distance}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{IGD}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+2;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(x!=AnalizadorMO.algoritmos-1)
        os.print("Algoritmo"+(x+1)+" &");
        else
        os.print("Algoritmo"+(x+1)); 
    }
    os.print("\\\\");
    os.println("\\hline");
    os.close();
    fos.close();
    /*
    //VARIANZA
    fos = new FileOutputStream("IGD_variance.tex", false);
    os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Inverted Generational Distance Variance}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{IGD variance}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+1;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
        os.print("Algoritmo"+(x+1)+" &");
    }
    os.print("Algoritmo"+AnalizadorMO.algoritmos+" \\");
    os.print("\\");
    os.println("\\hline");
    os.close();
    fos.close();
    */
  }
  
      
 public static void printQualityIndicator_IGD_plus(ArrayList <Double> Indicator_mean,ArrayList <Double> Indicator_desvest) throws IOException {
    FileOutputStream fos = new FileOutputStream("IGDplus.tex", true);
    PrintStream os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    double menor=Double.MAX_VALUE;
    int pos=-1;
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(Indicator_mean.get(x)<menor)
        {
            menor=Indicator_mean.get(x);
            pos=x;
        }
    }
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
    if(x!=pos)    
     os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
    else
     os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
   
    }
    if(pos==AnalizadorMO.algoritmos-1)
    os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");
    else
    os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");    
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();
    
    /*
    fos = new FileOutputStream("IGD_variance.tex", true);
    os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
     os.print(new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +" &");   
    }
    os.print(new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1)));
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();*/
  }
    
    
 public static void Termina_latex_IGD_plus() throws FileNotFoundException, IOException
 {
    FileOutputStream fos = new FileOutputStream("IGDplus.tex", true);
    PrintStream os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();
    /*
    fos = new FileOutputStream("IGD_variance.tex", true);
    os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();*/
 }
    
  public static void Inicializa_latex_igd_plus() throws FileNotFoundException, IOException
  {
    //PROMEDIO
    FileOutputStream fos = new FileOutputStream("IGDplus.tex", false);
    PrintStream os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Inverted Generational Distance Plus}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{IGD Plus}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+2;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(x!=AnalizadorMO.algoritmos-1)
        os.print("Algoritmo"+(x+1)+" &");
        else
        os.print("Algoritmo"+(x+1)); 
    }
    os.print("\\\\");
    os.println("\\hline");
    os.close();
    fos.close();
    /*
    //VARIANZA
    fos = new FileOutputStream("IGD_variance.tex", false);
    os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Inverted Generational Distance Variance}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{IGD variance}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+1;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
        os.print("Algoritmo"+(x+1)+" &");
    }
    os.print("Algoritmo"+AnalizadorMO.algoritmos+" \\");
    os.print("\\");
    os.println("\\hline");
    os.close();
    fos.close();
    */
  }
    
 public static void printQualityIndicator_GS(ArrayList <Double> Indicator_mean,ArrayList <Double> Indicator_desvest) throws IOException {
    FileOutputStream fos = new FileOutputStream("GS.tex", true);
    PrintStream os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    double menor=Double.MAX_VALUE;
    int pos=-1;
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(Indicator_mean.get(x)<menor)
        {
            menor=Indicator_mean.get(x);
            pos=x;
        }
    }
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
    if(x!=pos)    
     os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
    else
     os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
   
    }
    if(pos==AnalizadorMO.algoritmos-1)
    os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");
    else
    os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();
    /*
    fos = new FileOutputStream("GS_variance.tex", true);
    os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
     os.print(new DecimalFormat("0.0000E0").format(Indicator_variance.get(x)) +" &");   
    }
    os.print(new DecimalFormat("0.0000E0").format(Indicator_variance.get(AnalizadorMO.algoritmos-1)));
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();
    */
  }
    
    
 public static void Termina_latex_GS() throws FileNotFoundException, IOException
 {
    FileOutputStream fos = new FileOutputStream("GS.tex", true);
    PrintStream os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();
    /*
    fos = new FileOutputStream("GS_variance.tex", true);
    os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();
    */
 }
    
  public static void Inicializa_latex_GS() throws FileNotFoundException, IOException
  {
    //PROMEDIO
    FileOutputStream fos = new FileOutputStream("GS.tex", false);
    PrintStream os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Generalized Spread}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{GS}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+2;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(x!=AnalizadorMO.algoritmos-1)
        os.print("Algoritmo"+(x+1)+" &");
        else
        os.print("Algoritmo"+(x+1)); 
    }
    os.print("\\\\");
    os.println("\\hline");
    os.close();
    fos.close();
    
    /*
    //VARIANZA
    fos = new FileOutputStream("GS_variance.tex", false);
    os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Generalized Spread Variance}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{IGD variance}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+1;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
        os.print("Algoritmo"+(x+1)+" &");
    }
    os.print("Algoritmo"+AnalizadorMO.algoritmos+" \\");
    os.print("\\");
    os.println("\\hline");
    os.close();
    fos.close();
    */
  }

   public static void printQualityIndicator_HV(ArrayList <Double> Indicator_mean,ArrayList <Double> Indicator_desvest) throws IOException {
    FileOutputStream fos = new FileOutputStream("HV.tex", true);
    PrintStream os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    double mayor=0;
    int pos=-1;
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(Indicator_mean.get(x)>mayor)
        {
            mayor=Indicator_mean.get(x);
            pos=x;
        }
    }
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
    if(x!=pos)    
     os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
    else
     os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
   
    }
    if(pos==AnalizadorMO.algoritmos-1)
    os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");
    else
    os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();
    /*
    fos = new FileOutputStream("HV_variance.tex", true);
    os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
     os.print(new DecimalFormat("0.0000E0").format(Indicator_variance.get(x)) +" &");   
    }
    os.print(new DecimalFormat("0.0000E0").format(Indicator_variance.get(AnalizadorMO.algoritmos-1)));
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();
    */
  }
    
    
 public static void Termina_latex_HV() throws FileNotFoundException, IOException
 {
    FileOutputStream fos = new FileOutputStream("HV.tex", true);
    PrintStream os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();
    /*
    fos = new FileOutputStream("HV_variance.tex", true);
    os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();
    */
 }
    
  public static void Inicializa_latex_HV() throws FileNotFoundException, IOException
  {
    //PROMEDIO
    FileOutputStream fos = new FileOutputStream("HV.tex", false);
    PrintStream os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Hypervolumen}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{HV}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+2;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(x!=AnalizadorMO.algoritmos-1)
        os.print("Algoritmo"+(x+1)+" &");
        else
        os.print("Algoritmo"+(x+1)); 
    }
    os.print("\\\\");
    os.println("\\hline");
    os.close();
    fos.close();
    /*
    //VARIANZA
    fos = new FileOutputStream("HV_variance.tex", false);
    os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Hypervolumen Variance}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{HV variance}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+1;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
        os.print("Algoritmo"+(x+1)+" &");
    }
    os.print("Algoritmo"+AnalizadorMO.algoritmos+" \\");
    os.print("\\");
    os.println("\\hline");
    os.close();
    fos.close();
    */
  }

  
   public static void printQualityIndicator_Epsilon(ArrayList <Double> Indicator_mean,ArrayList <Double> Indicator_desvest) throws IOException {
    FileOutputStream fos = new FileOutputStream("Epsilon.tex", true);
    PrintStream os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    double menor=Double.MAX_VALUE;
    int pos=-1;
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(Indicator_mean.get(x)<menor)
        {
            menor=Indicator_mean.get(x);
            pos=x;
        }
    }
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
    if(x!=pos)    
     os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
    else
     os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(x)) +"_{"+ new DecimalFormat("0.0000E0").format(Indicator_desvest.get(x)) +"}$ &");   
   
    }
    if(pos==AnalizadorMO.algoritmos-1)
    os.print("\\cellcolor{gray95}$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");
    else
    os.print("$"+new DecimalFormat("0.0000E0").format(Indicator_mean.get(AnalizadorMO.algoritmos-1))+"_{"+new DecimalFormat("0.0000E0").format(Indicator_desvest.get(AnalizadorMO.algoritmos-1))+"}$");
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();
    /*
    fos = new FileOutputStream("Epsilon_variance.tex", true);
    os=new PrintStream(fos);
    os.print(AnalizadorMO.problema+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
     os.print(new DecimalFormat("0.0000E0").format(Indicator_variance.get(x)) +" &");   
    }
    os.print(new DecimalFormat("0.0000E0").format(Indicator_variance.get(AnalizadorMO.algoritmos-1)));
    //os.print(Indicator_mean.get(AnalizadorMO.algoritmos-1));
    os.print("\\"+"\\");
    os.print(" \n");
    os.print("\\hline"+"\n");
    os.close();
    fos.close();
    */
  }
    
    
 public static void Termina_latex_Epsilon() throws FileNotFoundException, IOException
 {
    FileOutputStream fos = new FileOutputStream("Epsilon.tex", true);
    PrintStream os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();
    /*
    fos = new FileOutputStream("Epsilon_variance.tex", true);
    os=new PrintStream(fos); 
    os.print("\\end{tabular}"+"\n");
    os.print("\\end{table*}"+"\n");
    os.print("\\end{document}" + "\n");
    os.close();
    fos.close();
    */
 }
    
  public static void Inicializa_latex_Epsilon() throws FileNotFoundException, IOException
  {
    //PROMEDIO
    FileOutputStream fos = new FileOutputStream("Epsilon.tex", false);
    PrintStream os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Epsilon}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{Epsilon}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+2;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos;x++)
    {
        if(x!=AnalizadorMO.algoritmos-1)
        os.print("Algoritmo"+(x+1)+" &");
        else
        os.print("Algoritmo"+(x+1));    
    }
    os.print("\\\\");
    os.println("\\hline");
    os.close();
    fos.close();
    /*
    //VARIANZA
    fos = new FileOutputStream("Epsilon_variance.tex", false);
    os=new PrintStream(fos);
    os.print("\\documentclass{article}" + "\n");
    os.print("\\title{Epsilon Variance}" + "\n");
    os.print("\\usepackage{colortbl}" + "\n");
    os.print("\\usepackage[table*]{xcolor}" + "\n");
    os.print("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.print("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.print("\\author{}" + "\n");
    os.print("\\begin{document}" + "\n");
    os.print("\\maketitle" + "\n");
    os.print("\\begin{table*}[ht!]"+"\n");
    os.print("\\scriptsize"+"\n");
    os.print("\\caption{Epsilon variance}"+"\n");
    os.print("\\centering"); 
    os.print("\\begin{tabular}{");
            //+ "|c|c|c|}"+"\n");
    for(int x=0;x<AnalizadorMO.algoritmos+1;x++)
    {
        os.print("|c|");
    }
    os.println("} \\hline");
    os.print("Problema"+" &");
    for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
    {
        os.print("Algoritmo"+(x+1)+" &");
    }
    os.print("Algoritmo"+AnalizadorMO.algoritmos+" \\");
    os.print("\\");
    os.println("\\hline");
    os.close();
    fos.close();
    */
  }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadormo;

/**
 *
 * @author Alx
 */

import java.io.*;
import java.util.ArrayList;
public class Estadisticos {
    
    
    
    public static void Inicializa_scripts_R() throws FileNotFoundException, IOException
    {
        //////////ENCABEZADO!!
        FileOutputStream fos=new FileOutputStream("Estadisticos.R",false);
        PrintStream ps=new PrintStream(fos);
        ps.println("library(PMCMR)");
        ps.println("write(\"\\\\documentclass{article}\", \"salida.tex\", append=FALSE)");
        ps.println("write(\"\\\\title{Pruebas estad\'isticas}\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\usepackage{colortbl}\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\usepackage[table*]{xcolor}\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\xdefinecolor{gray95}{gray}{0.65}\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\xdefinecolor{gray25}{gray}{0.8}\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\author{}\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\begin{document}\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\maketitle\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\begin{table*}[ht!]\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\scriptsize\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\caption{p-values}\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\centering\", \"salida.tex\", append=TRUE)");
        ps.println("write(\"\\\\begin{tabular}{\", \"salida.tex\", append=TRUE)");
        ps.println("cadena<-\"\"");
        String temp="";
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=1;j<=AnalizadorMO.algoritmos;j++)
            {
                temp+="|c|";
            }
        }
        temp+="}\\\\hline";
        //ps.println("cadena<-paste(cadena,}\\\\\\hline,\"\")");
        ps.println("write(\""+temp+"\",\"salida.tex\",append=TRUE)");
        temp="Indicador & ";
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++)
            {
                if(x==AnalizadorMO.algoritmos-1)
                temp+=String.valueOf(x)+"-"+String.valueOf(j);
                else
                temp+=String.valueOf(x)+"-"+String.valueOf(j)+" & ";    
            }
        }
        temp+="\\\\\\\\\\\\hline";
        //ps.println("cadena<-paste(cadena,}\\\\\\hline,\"\")");
        ps.println("write(\""+temp+"\",\"salida.tex\",append=TRUE)");
        

        ps.close();
        fos.close();
        
    }
    
    public static void Imprime_scripts_R(String problema) throws IOException
    {
        FileOutputStream fos=new FileOutputStream("Estadisticos.R",true);
        PrintStream ps=new PrintStream(fos);
         //EMPIEZA LA TABLA Y EL USO DE LOS DATOS EN R
        //Lee todos los datos en memoria :p
        File miDir = new File (".");
        for(int x=0;x<AnalizadorMO.algoritmos;x++)
        {
            ps.println("datosAlg_igd"+x+"<-read.table(\""+miDir.getCanonicalPath()+"/data/"+problema+"/"+problema+"_IGD_Alg"+x+"\")");
            ps.println("datosAlg_igdplus"+x+"<-read.table(\""+miDir.getCanonicalPath()+"/data/"+problema+"/"+problema+"_IGDPlus_Alg"+x+"\")");
            ps.println("datosAlg_gs"+x+"<-read.table(\""+miDir.getCanonicalPath()+"/data/"+problema+"/"+problema+"_GS_Alg"+x+"\")");
            ps.println("datosAlg_hv"+x+"<-read.table(\""+miDir.getCanonicalPath()+"/data/"+problema+"/"+problema+"_HV_Alg"+x+"\")");
            ps.println("datosAlg_epsilon"+x+"<-read.table(\""+miDir.getCanonicalPath()+"/data/"+problema+"/"+problema+"_Epsilon_Alg"+x+"\")");
        }
        
        //IGD
        String proxima_linea="Datos_igd <- data.frame(c(datosAlg_igd";
        for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
        {
            proxima_linea+=x +"$V1),c(datosAlg_igd";
        }
        proxima_linea+=AnalizadorMO.algoritmos-1+"$V1))";
        ps.println(proxima_linea);
        //ps.println("Datos_igd <- as.matrix(Datos_igd)");
        
        //IGD Plus
        proxima_linea="Datos_igdplus <- data.frame(c(datosAlg_igdplus";
        for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
        {
            proxima_linea+=x +"$V1),c(datosAlg_igdplus";
        }
        proxima_linea+=AnalizadorMO.algoritmos-1+"$V1))";
        ps.println(proxima_linea);
        //ps.println("Datos_igd <- as.matrix(Datos_igdplus)");
        
        //GS
        proxima_linea="Datos_gs <- data.frame(c(datosAlg_gs";
        for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
        {
            proxima_linea+=x +"$V1),c(datosAlg_gs";
        }
        proxima_linea+=AnalizadorMO.algoritmos-1+"$V1))";
        ps.println(proxima_linea);
        //ps.println("Datos_igd <- as.matrix(Datos_gs)");
        
        //HV
        proxima_linea="Datos_hv <- data.frame(c(datosAlg_hv";
        for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
        {
            proxima_linea+=x +"$V1),c(datosAlg_hv";
        }
        proxima_linea+=AnalizadorMO.algoritmos-1+"$V1))";
        ps.println(proxima_linea);
        //ps.println("Datos_igd <- as.matrix(Datos_hv)");
        
        //Epsilon
        proxima_linea="Datos_Epsilon <- data.frame(c(datosAlg_epsilon";
        for(int x=0;x<AnalizadorMO.algoritmos-1;x++)
        {
            proxima_linea+=x +"$V1),c(datosAlg_epsilon";
        }
        proxima_linea+=AnalizadorMO.algoritmos-1+"$V1))";
        ps.println(proxima_linea);
        //ps.println("Datos_igd <- as.matrix(Datos_epsilon)");//solo necesario con friedman no kruskal
        
        ps.println("pvalueIGD <- kruskal.test(Datos_igd)$p.value");
        ps.println("pvalueIGDplus <- kruskal.test(Datos_igdplus)$p.value");
        ps.println("pvalueGS <- kruskal.test(Datos_gs)$p.value");
        ps.println("pvalueHV <- kruskal.test(Datos_hv)$p.value");
        ps.println("pvalueEpsilon <- kruskal.test(Datos_Epsilon)$p.value");
        
        /////////////////AQUI IMPRIME EL INDICADOR IGD
        ps.println("cadena<-\""+AnalizadorMO.problema+"-IGD &\"");
        ps.println("if(pvalueIGD<0.05){"); //<0.05
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++){
            ps.println("pvalueIGD <- get.pvalues(posthoc.kruskal.nemenyi.test(Datos_igd))[\""+String.valueOf(x)+"-"+String.valueOf(j)+"\"]");
            if(x==AnalizadorMO.algoritmos-1)
            {
                ps.println("cadena <-paste(cadena,pvalueIGD,\"\\\\\\\\\")");
                
            }
            else
            {
                ps.println("cadena <-paste(cadena,pvalueIGD,\"&\")");
            }    
                                
            }
            
            
        }
        ps.println("write(cadena, \"salida.tex\", append=TRUE)");
        ps.println("}else{");
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++)
            {
                if(x==AnalizadorMO.algoritmos-1)
                {ps.println("cadena <-paste(cadena,\"-\",\"\\\\\\\\\")");}
                else
                {ps.println("cadena <-paste(cadena,\"-\",\"&\")");} 
                
            }
            
        }     
        
        ps.println("write(cadena, \"salida.tex\", append=TRUE)}");
        ps.println("write(\"\\\\hline\",\"salida.tex\",append=TRUE)");
        /////////////////////////////AQUI LO TERMINA DE IMPRIMIR IGD
        /////////////////AQUI IMPRIME EL INDICADOR IGD PLUS
        ps.println("cadena<-\""+AnalizadorMO.problema+"-IGD+ &\"");
        ps.println("if(pvalueIGDplus<0.05){"); //<0.05
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++){
            ps.println("pvalueIGDplus <- get.pvalues(posthoc.kruskal.nemenyi.test(Datos_igdplus))[\""+String.valueOf(x)+"-"+String.valueOf(j)+"\"]");
            if(x==AnalizadorMO.algoritmos-1)
            {
                ps.println("cadena <-paste(cadena,pvalueIGDplus,\"\\\\\\\\\")");
                
            }
            else
            {
                ps.println("cadena <-paste(cadena,pvalueIGDplus,\"&\")");
            }    
                                
            }
            
            
        }
        ps.println("write(cadena, \"salida.tex\", append=TRUE)");
        ps.println("}else{");
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++)
            {
                if(x==AnalizadorMO.algoritmos-1)
                {ps.println("cadena <-paste(cadena,\"-\",\"\\\\\\\\\")");}
                else
                {ps.println("cadena <-paste(cadena,\"-\",\"&\")");} 
                
            }
            
        }     
        
        ps.println("write(cadena, \"salida.tex\", append=TRUE)}");
        ps.println("write(\"\\\\hline\",\"salida.tex\",append=TRUE)");
        /////////////////////////////AQUI LO TERMINA DE IMPRIMIR IGD PLUS
        /////////////////AQUI IMPRIME EL INDICADOR GS
        ps.println("cadena<-\""+AnalizadorMO.problema+"-GS &\"");
        ps.println("if(pvalueGS<0.05){"); //<0.05
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++){
            ps.println("pvalueGS <- get.pvalues(posthoc.kruskal.nemenyi.test(Datos_gs))[\""+String.valueOf(x)+"-"+String.valueOf(j)+"\"]");
            if(x==AnalizadorMO.algoritmos-1)
            {
                ps.println("cadena <-paste(cadena,pvalueGS,\"\\\\\\\\\")");
                
            }
            else
            {
                ps.println("cadena <-paste(cadena,pvalueGS,\"&\")");
            }    
                                
            }
            
            
        }
        ps.println("write(cadena, \"salida.tex\", append=TRUE)");
        ps.println("}else{");
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++)
            {
                if(x==AnalizadorMO.algoritmos-1)
                {ps.println("cadena <-paste(cadena,\"-\",\"\\\\\\\\\")");}
                else
                {ps.println("cadena <-paste(cadena,\"-\",\"&\")");} 
                
            }
            
        }     
        
        ps.println("write(cadena, \"salida.tex\", append=TRUE)}");
        ps.println("write(\"\\\\hline\",\"salida.tex\",append=TRUE)");
        /////////////////////////////AQUI LO TERMINA DE IMPRIMIR GS
        /////////////////AQUI IMPRIME EL INDICADOR HV
        ps.println("cadena<-\""+AnalizadorMO.problema+"-HV &\"");
        ps.println("if(pvalueHV<0.05){"); //<0.05
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++){
            ps.println("pvalueHV <- get.pvalues(posthoc.kruskal.nemenyi.test(Datos_hv))[\""+String.valueOf(x)+"-"+String.valueOf(j)+"\"]");
            if(x==AnalizadorMO.algoritmos-1)
            {
                ps.println("cadena <-paste(cadena,pvalueHV,\"\\\\\\\\\")");
                
            }
            else
            {
                ps.println("cadena <-paste(cadena,pvalueHV,\"&\")");
            }    
                                
            }
            
            
        }
        ps.println("write(cadena, \"salida.tex\", append=TRUE)");
        ps.println("}else{");
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++)
            {
                if(x==AnalizadorMO.algoritmos-1)
                {ps.println("cadena <-paste(cadena,\"-\",\"\\\\\\\\\")");}
                else
                {ps.println("cadena <-paste(cadena,\"-\",\"&\")");} 
                
            }
            
        }     
        
        ps.println("write(cadena, \"salida.tex\", append=TRUE)}");
        ps.println("write(\"\\\\hline\",\"salida.tex\",append=TRUE)");
        /////////////////////////////AQUI LO TERMINA DE IMPRIMIR HV
        /////////////////AQUI IMPRIME EL INDICADOR EPSILON
        ps.println("cadena<-\""+AnalizadorMO.problema+"-Epsilon &\"");
        ps.println("if(pvalueEpsilon<0.05){"); //<0.05
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++){
            ps.println("pvalueEpsilon <- get.pvalues(posthoc.kruskal.nemenyi.test(Datos_Epsilon))[\""+String.valueOf(x)+"-"+String.valueOf(j)+"\"]");
            if(x==AnalizadorMO.algoritmos-1)
            {
                ps.println("cadena <-paste(cadena,pvalueEpsilon,\"\\\\\\\\\")");
                
            }
            else
            {
                ps.println("cadena <-paste(cadena,pvalueEpsilon,\"&\")");
            }    
                                
            }
            
            
        }
        ps.println("write(cadena, \"salida.tex\", append=TRUE)");
        ps.println("}else{");
        for(int x=1;x<=AnalizadorMO.algoritmos;x++)
        {
            for(int j=x+1;j<=AnalizadorMO.algoritmos;j++)
            {
                if(x==AnalizadorMO.algoritmos-1)
                {ps.println("cadena <-paste(cadena,\"-\",\"\\\\\\\\\")");}
                else
                {ps.println("cadena <-paste(cadena,\"-\",\"&\")");} 
                
            }
            
        }     
        
        ps.println("write(cadena, \"salida.tex\", append=TRUE)}");
        ps.println("write(\"\\\\hline\",\"salida.tex\",append=TRUE)");
        /////////////////////////////AQUI LO TERMINA DE IMPRIMIR EPSILON
        ps.close();
        fos.close();
    }
    
    public static void Termina_scripts_R() throws FileNotFoundException, IOException
    {
                //////////ENCABEZADO!!
        FileOutputStream fos=new FileOutputStream("Estadisticos.R",true);
        PrintStream ps=new PrintStream(fos);
         //Cierra el documento de R
        ps.println("write(\"\\\\end{tabular}\",\"salida.tex\",append=TRUE)");
        ps.println("write(\"\\\\end{table*}\",\"salida.tex\",append=TRUE)");
        ps.println("write(\"\\\\end{document}\",\"salida.tex\",append=TRUE)");
        ps.close();
        fos.close();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementaciones;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Jose Ignacio
 */
public class AdminBD {
    
    private static final String NOMBRE_BD = "clientes";
    private int parametroN;
    private int parametroP;
    
    public AdminBD(){
        
    }
    public AdminBD(int parametroN) {
        this.parametroN = parametroN;
    }

    public void setParametroN(int parametroN) {
        this.parametroN = parametroN;
        this.parametroP = parametroN - 2; //formula de firma umbral
    }

    public int getParametroN() {
        return parametroN;
    }

    public int getParametroP() {
        return parametroP;
    }
    
    
    public String getParticionSecreto(String password, int indice, int total){
        
        String particionSecreto = "";
        int indiceInicial = -1;
        int indiceFinal = -1;
        
        int nroChars = password.length() / total;
            
        indiceInicial = nroChars*indice;
        indiceFinal = indiceInicial + nroChars;
        if(indice == total-1){
            indiceFinal = password.length();
        }
        
        particionSecreto = password.substring(indiceInicial, indiceFinal);
                
        
        return particionSecreto;
        
    }
    public ArrayList<String> entregarDatos(int indice, int total){
        
        ArrayList<String> baseDatos = new ArrayList();
        
        File archivo = null;
        FileReader fr = null;
        BufferedReader br =  null;
        
        try{
            archivo = new File(NOMBRE_BD+".txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            
            String linea;            
            String [] datos;
            while((linea = br.readLine())!=null ){   
                
                datos = linea.split(",");
                datos[1] = getParticionSecreto(datos[1], indice, total);
                
                linea = datos[0]+","+datos[1]+","+indice;
                baseDatos.add(linea);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{        
            try{                    
                if( null != fr ){   
                    fr.close();     
                }                  
            }catch (Exception e2){ 
                e2.printStackTrace();
            }
        }
        
        return baseDatos;
    }
    
    
    public void crearLlavesCripto(){
    
        File archivo = null;
        FileWriter salida = null;
        FileReader fr = null;
        BufferedReader br =  null;
        PrintWriter pw = null;
        
        try{
            archivo = new File(NOMBRE_BD+".txt");
            salida = new FileWriter("BD_comunidad.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            pw = new PrintWriter(salida);
            
            String linea;            
            String [] datos;
            String llavePrivada;
            while((linea = br.readLine())!=null ){   
                
                datos = linea.split(",");
                llavePrivada = ""+(datos[0]+datos[1]);
                linea = datos[0]+datos[1]+llavePrivada;                
                pw.println(linea);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{        
            try{                    
                if( null != fr ){   
                    fr.close();     
                }                  
            }catch (Exception e2){ 
                e2.printStackTrace();
            }
        }
    }
    
    
}

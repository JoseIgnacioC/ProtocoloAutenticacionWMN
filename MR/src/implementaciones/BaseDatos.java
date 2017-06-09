/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementaciones;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Jose Ignacio
 */
public class BaseDatos {
    
    String nodo;
    
    public BaseDatos(){
        
    }
    public BaseDatos(String nodo){
       this.nodo = nodo; 
    }
    
    public void agregarNuevo(String nodoContrario, String llaveSesion, int nonceF ){
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        try{
            fichero = new FileWriter(nodo+".txt",true);
            pw = new PrintWriter(fichero);
            
            pw.println(nodoContrario+","+llaveSesion+","+nonceF+","+nonceF);
            
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{           
                if (null != fichero)
                    fichero.close();
            }catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    public void reescribirArchivo(ArrayList<String> lineas){
        FileWriter fichero = null;
        PrintWriter pw = null;
        try{
            fichero = new FileWriter(nodo+".txt");
            pw = new PrintWriter(fichero);

            for(String linea:lineas){
                pw.println(linea);
            }            
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{           
                if (null != fichero)
                    fichero.close();
            }catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    public int obtenerNonce(String nodoContrario, boolean isRespuesta){
        
        ArrayList<String> lineas = new ArrayList();
        
        String nonce = "";
        File archivo = null;
        FileReader fr = null;
        BufferedReader br =  null;
        
        try{
            archivo = new File(nodo+".txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            
            String linea;            
            while((linea = br.readLine())!=null ){                
                                                
                String [] datos;
                datos = linea.split(",");
                
                if(datos[0].equals(nodoContrario)){
                    
                    int nuevoNonce;
                    
                    if(!isRespuesta){
                        nonce = datos[2];
                        nuevoNonce = Integer.parseInt(datos[2]);
                        nuevoNonce++;
                        linea = datos[0]+","+datos[1]+","+nuevoNonce+","+datos[3];
                    }
                    else{
                        nonce = datos[3];
                        nuevoNonce = Integer.parseInt(datos[3]);
                        nuevoNonce++;
                        linea = datos[0]+","+datos[1]+","+datos[2]+","+nuevoNonce;
                    }                                            
                }                
                lineas.add(linea);
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
        
        reescribirArchivo(lineas);
        
        return Integer.parseInt(nonce);
    }    
    public String obtenerLlaveSesion(String nodoContrario){
        
        String llavePrivada = "";
        File archivo = null;
        FileReader fr = null;
        BufferedReader br =  null;
        
        try{
            archivo = new File(nodo+".txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            
            String linea;            
            while((linea = br.readLine())!=null ){                
                String [] datos;
                datos = linea.split(",");
                if(datos[0].equals(nodoContrario)){
                    
                    llavePrivada = datos[1];
                    break;
                }                                    
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
        
        return llavePrivada;
    }
    
}

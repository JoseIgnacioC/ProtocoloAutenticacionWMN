/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementaciones;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Jose Ignacio
 */
public class Log {
    
    private String nombreNodo;
        
    public Log(String nombre){
        this.nombreNodo = nombre;
    }
    
    public void writeInfo(String emisor, String receptor, String mensaje){
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        
        Date date = Calendar.getInstance().getTime();
        DateFormat dateHourFormat = new SimpleDateFormat("kk:mm:ss S");
        String timestamp = dateHourFormat.format(date);                        
                
        try{
            fichero = new FileWriter("LOG_"+nombreNodo+".txt",true);
            pw = new PrintWriter(fichero);
                        
                pw.println("=== "+timestamp+" - "+emisor+":"+ mensaje+" "+receptor+" ===");
            
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
    
    public void writePaso(String emisor, String receptor, String mensaje){
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        
        Date date = Calendar.getInstance().getTime();
        DateFormat dateHourFormat = new SimpleDateFormat("kk:mm:ss S");
        String timestamp = dateHourFormat.format(date);                        
                
        try{
            fichero = new FileWriter("LOG_"+nombreNodo+".txt",true);
            pw = new PrintWriter(fichero);
            
            pw.println(timestamp+" - "+emisor+"-->"+receptor+": "+mensaje);
            
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
    
    public void writePaso(String emisor, String receptor, String[] datos){
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        
        Date date = Calendar.getInstance().getTime();
        DateFormat dateHourFormat = new SimpleDateFormat("kk:mm:ss S");
        String timestamp = dateHourFormat.format(date);                        
        String mensaje = datos[0]+","+datos[1]+","+datos[2]+","+datos[3];
        
        try{
            fichero = new FileWriter("LOG_"+nombreNodo+".txt",true);
            pw = new PrintWriter(fichero);
            
            pw.println(timestamp+" - "+emisor+"-->"+receptor+": "+mensaje);
            
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
    
}

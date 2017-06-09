/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protocolo;

import java.util.ArrayList;

/**
 *
 * @author Jose Ignacio
 */
public class FirmaUmbral {
    
    public int parametroN;
    public int paramentroP;
    
    
    public FirmaUmbral(){
        
    }
    public FirmaUmbral(int n, int p){
        this.paramentroP = p;
        this.parametroN = n;
    }
    
    public String generarParticionFirmada(String mensaje, String particionSecreto){
        String particion = "";
        
        return particion;
    }
    
    public boolean verifificarParticionFirmada(String particion){
        
        boolean aceptada = true;
        
        return aceptada;        
    }
    
    public String reconstruccionMensaje(ArrayList<String> particiones){
        
        String secreto = "";
        String mensaje = "";
        
        
        for(String particion : particiones ){
                        
            int indiceInicial = particion.indexOf("G");
            int indiceFinal = particion.indexOf("/");
            secreto += particion.substring(indiceInicial+1, indiceFinal);
            mensaje = particion.substring(indiceFinal+1);
        }
        
        mensaje = "SIG"+secreto+"/"+mensaje;
        
        return mensaje;
    }
    public boolean validarMensajeFirmado(String mensaje){
        boolean aceptada = true;
        
        return aceptada;
    }
}
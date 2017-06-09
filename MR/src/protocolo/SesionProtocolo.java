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
public class SesionProtocolo {
    
    public Object interfazContrario;
    public String identificadorContrario;
    public String nombreSesion; //idSolicitate+idVerificador
    public String tipoNodo;
    public int nonceFreshness;    
    public int nonceLivenessPropio;
    public int nonceLivenessContrario;
    public int aleatorioPropio;
    public int aleatorioContrario;
    public int aleatorioZpPropio;
    public int aleatorioZpContrario;    
    public boolean sesionPrincipal;
    public int nroRecibidos;
    public String credencialRecibida;
    public ArrayList<String> partM1Recibidas;
    public ArrayList<String> partM2Recibidas;    
    
    
    public SesionProtocolo(){
        
    }

    public SesionProtocolo(Object interfazContrario, String identificadorContrario, String nombreSesion, String tipoNodo, boolean sesionPrincipal) {
        this.interfazContrario = interfazContrario;
        this.identificadorContrario = identificadorContrario;
        this.nombreSesion = nombreSesion;
        this.sesionPrincipal = sesionPrincipal;
        this.tipoNodo=tipoNodo;
        this.nroRecibidos=0;
        this.partM1Recibidas = new ArrayList();
        this.partM2Recibidas = new ArrayList();        
    }
    
    public boolean isSesionPrincipal() {
        return sesionPrincipal;
    }    

    public void setNonceFreshness(int nonceFreshnessPropio) {
        this.nonceFreshness = nonceFreshnessPropio;
    }   

    public void setNonceLivenessPropio(int nonceLivenessPropio) {
        this.nonceLivenessPropio = nonceLivenessPropio;
    }

    public void setNonceLivenessContrario(int nonceLivenessContrario) {
        this.nonceLivenessContrario = nonceLivenessContrario;
    }

    public void setAleatorioPropio(int aleatorioPropio) {
        this.aleatorioPropio = aleatorioPropio;
    }

    public void setAleatorioContrario(int aleatorioContrario) {
        this.aleatorioContrario = aleatorioContrario;
    }        

    public void setAleatorioZpPropio(int aleatorioZpPropio) {
        this.aleatorioZpPropio = aleatorioZpPropio;
    }

    public void setCredencialRecibida(String credencialRecibida) {
        this.credencialRecibida = credencialRecibida;
    }
    

    public void setAleatorioZpContrario(int aleatorioZpContrario) {
        this.aleatorioZpContrario = aleatorioZpContrario;
    }
    public void addParticionRecibidaM1(String particion){
        this.partM1Recibidas.add(particion);
    }
    public void addParticionRecibidaM2(String particion){
        this.partM2Recibidas.add(particion);
    }
    
    
    public void nuevoMsjeRecibido() {
        this.nroRecibidos++;
    }
}

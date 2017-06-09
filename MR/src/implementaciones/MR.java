/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementaciones;
import protocolo.SesionProtocolo;
import protocolo.CBI;
import java.util.ArrayList;
import protocolo.Conf;
import interfaces.*;
import java.security.KeyPair;
import org.bouncycastle.crypto.CipherParameters;
/**
 *
 * @author Jose Ignacio
 */
public class MR {
    
    private String identificador;
    //private String llavePrivada;
    private CBI cbi;
    private ArrayList<SesionProtocolo> sesionesActivas = new ArrayList();
    private ArrayList interfacesMCsAutenticados = new ArrayList();
    private ArrayList<String> nombresMCsAutenticados = new ArrayList();
    
    public MR(){
        
    }

    public MR(String identificador) throws Exception {
        this.identificador = identificador;
        //this.llavePrivada = llavePrivada;
        this.cbi = new CBI(identificador);        
    }

    public String getIdentificador() {
        return identificador;
    }

    public CBI getCbi() {
        return cbi;
    }

    public void setCbi(/*CipherParameters privateKey, */KeyPair parRsa/*, CipherParameters pmk */) {
        this.cbi.setKeyRsa(parRsa);
        //this.cbi.setPMK(pmk);
        //this.cbi.setSecretKey(privateKey);
    }

    public ArrayList<SesionProtocolo> getSesionesActivas() {
        return sesionesActivas;
    }

    public ArrayList getInterfacesMCsAutenticados() {
        return interfacesMCsAutenticados;
    }

    public ArrayList<String> getNombresMCsAutenticados() {
        return nombresMCsAutenticados;
    }    
    public void addSesionActiva(SesionProtocolo sesion){
        this.sesionesActivas.add(sesion);
    }
    public void addInterfazMCAutenticado(InterfazMC interfaz){
        this.interfacesMCsAutenticados.add(interfaz);
    }
    public void addMCAutenticado(String nombre){
        this.nombresMCsAutenticados.add(nombre);
    }
    
    
}

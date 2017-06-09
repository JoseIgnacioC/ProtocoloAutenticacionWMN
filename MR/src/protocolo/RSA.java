/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protocolo;

/**
 *
 * @author Jose Ignacio
 */
public class RSA {
    
    String llavePublica;
    String llavePrivada;
    
    public RSA(){
        
    }
    public RSA(String llavePrivada){
        this.llavePrivada = llavePrivada;
        this.llavePublica = "";
    }
    public RSA(String llavePublica, String llavePrivada){
        this.llavePrivada = llavePrivada;
        this.llavePublica = llavePublica;
    }
    
    public String encriptar(String mensaje){
        
        mensaje = mensaje.replaceAll(",", ";");
        String txtCifrado = "E-"+llavePrivada+"("+mensaje+")";
        
        return txtCifrado;
    }
    public String desencriptar(String mensaje){
                
        String plano = "";
        
        int indice = mensaje.indexOf("(");
        plano = mensaje.substring(indice+1, mensaje.length()-1);
        
        plano = plano.replaceAll(";", ",");
        return plano;
    }        
}

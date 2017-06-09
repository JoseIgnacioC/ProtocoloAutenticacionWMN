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
public class Hash {
    
    private String cadena;
    private String llave;
    
    public Hash(){
        
    }
    public Hash(String cadena){
        this.cadena=cadena;
    }
    
    public int funcionHash(){
        
        return cadena.hashCode();
    }    
    
    public int funcionHMac(){
        return (cadena+llave).hashCode();
    }
}

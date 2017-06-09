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
public class CadenaParticiones {
    
    public int indice;
    public String particionPublicaSecreto;
    public String particionMensaje1;
    public String particionMensaje2;
    public String valorHash;

    public CadenaParticiones(String mensaje) {
        
        String[] datos = mensaje.split(",");
        
        if(datos.length==5){
            this.indice = Integer.parseInt(datos[0]);
            this.particionPublicaSecreto = datos[1];
            this.particionMensaje1 = datos[2];
            this.particionMensaje2 = datos[3];
            datos[4] = datos[4].replaceAll("=", "");
            this.valorHash = datos[4];
        }
        else
            System.err.println("Error en generacion de cadena de particiones");
    }
    
    
}

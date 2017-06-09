
package protocolo;

import implementaciones.InterfazMCImp;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 *
 * @author Jose Ignacio
 */



public class Mensaje {
    public String identificadorProtocolo;
    public String identificadorEmisor;
    public String identificadorReceptor;
    public String identificadorSolicitante;
    public String identificadorVerificador;
    public int nonceFresh = -1;
    public int nonceFreshRespuesta = -1;
    public int nonceLive = -1;
    public int nonceLiveRespuesta = -1;
    public String timestamp;
    public String cred;
    public int aleatorioVerificador = -1;
    public int aleatorioSolicitante = -1;
    public int valorZpVerificador = -1;
    public int valorZpSolicitante = -1;
    public int numeroSaltos = -1;
    public String  msjeEncriptadoPart;
    public String prueba;
    public int valorHMac = -1;
    public String valorHash;
    public String firmaHash;
    
    public Mensaje(){
        
    }
    public Mensaje(String datos[], String paso){
            
        
        String datosDes[];
        
        switch (paso){
            
            case "1"://Paso 1
                if(datos.length == 4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.nonceFresh=Integer.parseInt(datos[2]);
                    this.firmaHash=datos[3];
                }
                else{
                    System.err.println("Error en la cantidad de datos en el paso 1");
                }                
                break;
            case "2"://Paso 2
                if(datos.length == 4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 4){
                        this.nonceFresh = Integer.parseInt(datosDes[0]);
                        this.nonceFreshRespuesta = Integer.parseInt(datosDes[1]);
                        this.nonceLive = Integer.parseInt(datosDes[2]);
                        this.firmaHash = datosDes[3];
                    }
                    else{
                        System.err.println("Error en la cantidad de datos desencriptados en el paso 2");
                    }
                }
                else{
                    System.err.println("Error en la cantidad de datos en el paso 2");
                }                
                break;
            case "3"://Paso 3
                if(datos.length == 4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 4){
                        this.nonceFreshRespuesta = Integer.parseInt(datosDes[0]);
                        this.nonceLive = Integer.parseInt(datosDes[1]);
                        this.nonceLiveRespuesta = Integer.parseInt(datosDes[2]);                        
                        this.firmaHash = datosDes[3];
                    }
                    else{
                        System.err.println("Error en la cantidad de datos desencriptados en el paso 3");
                    }
                }
                else{
                    System.err.println("Error en la cantidad de datos en el paso 3");
                }
                break;
            case "4"://Paso 4
                if(datos.length == 4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 5){
                        this.nonceFreshRespuesta = Integer.parseInt(datosDes[0]);
                        this.nonceLive = Integer.parseInt(datosDes[1]);
                        this.nonceLiveRespuesta = Integer.parseInt(datosDes[2]);
                        this.aleatorioVerificador = Integer.parseInt(datosDes[3]);
                        this.firmaHash = datosDes[4];
                    }
                    else
                        System.err.println("Error en la cantidad de datos desencriptados en el paso 4");
                }
                else
                    System.err.println("Error en la cantidad de datos en el paso 4");
                break;
            
            case "5"://Paso 5
                if(datos.length == 4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 7 ){
                        this.nonceFreshRespuesta = Integer.parseInt(datosDes[0]);
                        this.nonceLive = Integer.parseInt(datosDes[1]);
                        this.nonceLiveRespuesta = Integer.parseInt(datosDes[2]);
                        this.cred = datosDes[3];
                        this.aleatorioSolicitante = Integer.parseInt(datosDes[4]);
                        this.valorZpSolicitante = Integer.parseInt(datosDes[5]);
                        this.firmaHash = datosDes[6];
                    }
                    else
                        System.err.println("Error en la cantidad de datos desencriptados en el paso 5");
                }
                else
                    System.err.println("Error en la cantidad de datos en el paso 5");
                break;
            
            case "6A" :            
                
                if(datos.length == 4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 7){
                        this.identificadorVerificador = datosDes[0];
                        this.identificadorSolicitante = datosDes[1];
                        this.timestamp = datosDes[2];
                        this.aleatorioVerificador = Integer.parseInt(datosDes[3]);
                        this.aleatorioSolicitante = Integer.parseInt(datosDes[4]);
                        this.valorZpVerificador = Integer.parseInt(datosDes[5]);
                        this.valorHash = datosDes[6];
                    }
                    else
                        System.err.println("Error en la cantidad de datos desencriptados en el paso 6A o 7B");
                }
                else if(datos.length == 10){ //cuando el verificador se reenvia el mensaje para enviar la solicitudn a sus MCs autenticados
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    this.identificadorVerificador = datos[3];
                    this.identificadorSolicitante = datos[4];
                    this.timestamp = datos[5];
                    this.aleatorioVerificador = Integer.parseInt(datos[6]);
                    this.aleatorioSolicitante = Integer.parseInt(datos[7]);
                    this.valorZpVerificador = Integer.parseInt(datos[8]);
                    this.valorHash = "";
                }
                else{
                    System.err.println("Error en la cantidad de datos en el paso 6A o 7B");
                }
                
                break;
            
            case "6B":
                
                this.identificadorProtocolo=datos[0];
                this.identificadorEmisor=datos[1];
                this.identificadorReceptor=datos[2];
                    
                datosDes = datos[3].split(",");
                
                if(datosDes.length == 7){
                    this.identificadorVerificador = datosDes[0];
                    this.identificadorSolicitante = datosDes[1];
                    this.nonceFresh = Integer.parseInt(datosDes[2]);
                    this.aleatorioVerificador = Integer.parseInt(datosDes[3]);
                    this.aleatorioSolicitante = Integer.parseInt(datosDes[4]);
                    this.valorZpVerificador = Integer.parseInt(datosDes[5]);
                    this.valorHash = datosDes[6];
                }
                else
                    System.err.println("Error en la cantidad de datos desencriptados en el paso 6B");
                break;
                
            case "7A":
                
                if(datos.length == 4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 8){
                        this.identificadorVerificador = datosDes[0];
                        this.identificadorSolicitante = datosDes[1];
                        this.nonceFreshRespuesta = Integer.parseInt(datosDes[2]);
                        this.aleatorioVerificador = Integer.parseInt(datosDes[3]);
                        this.aleatorioSolicitante = Integer.parseInt(datosDes[4]);
                        this.valorZpVerificador = Integer.parseInt(datosDes[5]);
                        this.numeroSaltos = Integer.parseInt(datosDes[6]);
                        this.valorHash = datosDes[7];
                    }
                    else
                        System.err.println("Error en la cantidad de datos desencriptados en el paso 6A o 7B");
                }                
                else{
                    System.err.println("Error en la cantidad de datos en el paso 7A");
                }
                
                break;                
            
            case "8A":
            
                if(datos.length == 4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 5){
                        this.identificadorVerificador = datosDes[0];
                        this.identificadorSolicitante = datosDes[1];
                        this.nonceFreshRespuesta = Integer.parseInt(datosDes[2]);
                        this.msjeEncriptadoPart = datosDes[3];
                        this.valorHash = datosDes[4];
                    }
                    else{
                        System.err.println("Error 8A-1");
                    }
                }
                else
                    System.err.println("Error 8A-2");
                
                break;            
                
            case "9A"://sirve para 10B
                if(datos.length==4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 5){
                        this.identificadorVerificador = datosDes[0];
                        this.identificadorSolicitante = datosDes[1];
                        this.timestamp = datosDes[2];
                        this.msjeEncriptadoPart = datosDes[3];
                        this.valorHash = datosDes[4];
                    }
                    else
                        System.err.println("Error en paso 9A o 10B");
                }
                break;
                
            case "10A":
                if(datos.length==4){
                    this.identificadorProtocolo=datos[0];
                    this.identificadorEmisor=datos[1];
                    this.identificadorReceptor=datos[2];
                    
                    datosDes = datos[3].split(",");
                    if(datosDes.length == 6){
                        this.nonceFreshRespuesta = Integer.parseInt(datosDes[0]);
                        this.nonceLiveRespuesta = Integer.parseInt(datosDes[1]);
                        this.valorZpVerificador = Integer.parseInt(datosDes[2]);
                        this.prueba = datosDes[3];
                        this.valorHMac = Integer.parseInt(datosDes[4]);
                        this.valorHash = datosDes[5];
                    }
                }
                break;
                
            default:
                System.out.println("Aqui?");
                break;
         
        }
    }
    
    public String crearStrPaso1(){
        String mensaje = identificadorProtocolo+","+identificadorEmisor+","+nonceFresh+","+firmaHash;
        return mensaje;
    }        
    public String crearStrPaso2(CBI cbi, PublicKey llavePublica) throws Exception{
        String mensaje;
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        
        String txtCifrado = nonceFresh+","+nonceFreshRespuesta+","+nonceLive+","+firmaHash;
                
        while( ( (txtCifrado.length() - 3) % 4) != 0){
            txtCifrado+= "1";
        }
        
        txtCifrado = cbi.encriptar(txtCifrado, llavePublica);
        mensaje = txtPlano+","+txtCifrado;
        
        return mensaje;
    }
    public String crearStrPaso3(CBI cbi, PublicKey llavePublica) throws Exception{
        String mensaje;
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        String txtCifrado = nonceFreshRespuesta+","+nonceLive+","+nonceLiveRespuesta+","+firmaHash;
        
        while( ( (txtCifrado.length() - 3) % 4) != 0){
            txtCifrado+= "1";
        }
        
        txtCifrado = cbi.encriptar(txtCifrado, llavePublica);
        mensaje = txtPlano+","+txtCifrado;
        
        return mensaje;
    }
    public String crearStrPaso4(CBI cbi, PublicKey llavePublica) throws Exception{
        String mensaje;
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        String txtCifrado = nonceFreshRespuesta+","+nonceLive+","+nonceLiveRespuesta+","+aleatorioVerificador+","+firmaHash;
        
        while( ( (txtCifrado.length() - 4) % 4) != 0){
            txtCifrado+= "1";
        }
        
        txtCifrado = cbi.encriptar(txtCifrado, llavePublica);
        mensaje = txtPlano+","+txtCifrado;
        
        return mensaje;
    }
    public String crearStrPaso5(CBI cbi, PublicKey llavePublica) throws Exception{
        String mensaje;
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        String txtCifrado = nonceFreshRespuesta+","+nonceLive+","+nonceLiveRespuesta+","+cred+","+aleatorioSolicitante+","+valorZpSolicitante+","+firmaHash;
        
        while( ( (txtCifrado.length() - 6) % 4) != 0){
            txtCifrado+= "1";
        }
        
        txtCifrado = cbi.encriptar(txtCifrado, llavePublica);
        mensaje = txtPlano+","+txtCifrado;
        
        return mensaje;
    }
    public String crearStrPaso6A7B(DES des) throws Exception{
        String mensaje;
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        String txtCifrado = identificadorVerificador+","+identificadorSolicitante+","+timestamp+","+aleatorioVerificador+","+aleatorioSolicitante+","+valorZpVerificador+","+valorHash;

        if(des != null){
            txtCifrado = des.encriptar(txtCifrado);            
        }
        mensaje = txtPlano+","+txtCifrado;        
        
        return mensaje;
    }
    public String crearStrPaso7A8B(DES des) throws Exception{
        String mensaje;
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        String txtCifrado = identificadorVerificador+","+identificadorSolicitante+","+nonceFreshRespuesta+","+aleatorioVerificador+","+aleatorioSolicitante+","+valorZpVerificador+","+numeroSaltos+","+valorHash;
        
        txtCifrado = des.encriptar(txtCifrado);
        mensaje = txtPlano+","+txtCifrado;
        
        return mensaje;
    }
    public String crearStrPaso8A9B(DES des){
        String mensaje;
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        String txtCifrado = identificadorVerificador+","+identificadorSolicitante+","+nonceFreshRespuesta+","+msjeEncriptadoPart+","+valorHash;
        
        try{
            txtCifrado = des.encriptar(txtCifrado);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mensaje = txtPlano+","+txtCifrado;
    
        return mensaje;
    }
    public String crearStrPaso9A10B(DES des) throws Exception{
        String mensaje;
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        String txtCifrado = identificadorVerificador+","+identificadorSolicitante+","+timestamp+","+msjeEncriptadoPart+","+valorHash;
        
        try{
            txtCifrado = des.encriptar(txtCifrado);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mensaje = txtPlano+","+txtCifrado;
    
        return mensaje;        
    }
    
    public String crearStrPaso10A12B(CBI cbi, PublicKey llavePublica) throws Exception{
        String mensaje;
        
        String txtPlano = identificadorProtocolo+","+identificadorEmisor+","+identificadorReceptor;
        String txtCifrado = nonceFreshRespuesta+","+nonceLiveRespuesta+","+prueba+","+valorHMac+","+valorHash;
        
        while( ( (txtCifrado.length() - 4) % 4) != 0){
            txtCifrado+= "1";
        }
        
        txtCifrado = cbi.encriptar(txtCifrado, llavePublica);
        
        mensaje = txtPlano + "," + txtCifrado;
        
        return mensaje;
    }
    
    public void setValorHMAC(int clave){
         int aux = (identificadorVerificador+","+identificadorSolicitante+","+nonceLiveRespuesta).hashCode();
         this.valorHMac = aux * clave;
    }
    
    public void setValorHash(){
        
        int hash;
        String cadena = getStringPlano();
        hash = cadena.hashCode();
        
        hash = Math.abs(hash);
        hash = hash % 99999999;        
        
        this.valorHash = hash+"";
    }
    public void setValorHash(CBI cbi){
                        
        int hash;
        String cadena = getStringPlano();
        hash = cadena.hashCode();                
        
        hash = Math.abs(hash);
        hash = hash % 99999999;
        
        String hashFirmado = cbi.firmar(hash+"");
        this.firmaHash = hashFirmado;
    }
    
    public String getStringPlano(){
        
        String mensaje;        
        String mensajePlano = identificadorProtocolo+identificadorEmisor+identificadorReceptor;        
        String mensajeCifrado = "";
        
        if(identificadorSolicitante != null || identificadorVerificador != null){
            mensajeCifrado+= identificadorSolicitante +","+identificadorVerificador+",";
        }
        if(nonceFresh != -1){
            mensajeCifrado+= nonceFresh+",";
        }
        if(nonceFreshRespuesta!= -1){
            mensajeCifrado+=nonceFreshRespuesta+",";
        }
        if(timestamp!= null){
            mensajeCifrado+=timestamp+",";
        }
        if(nonceLive!= -1){
            mensajeCifrado+=nonceLive+",";
        }
        if(nonceLiveRespuesta!=-1){
            mensajeCifrado+=nonceLiveRespuesta+",";
        }
        if(cred != null){
            mensajeCifrado+=cred+",";
        }
        if(aleatorioSolicitante!= -1){
            mensajeCifrado+=aleatorioSolicitante+",";
        }
        if(aleatorioVerificador!= -1){
            mensajeCifrado+=aleatorioVerificador+",";
        }
        if(valorZpSolicitante!= -1){
            mensajeCifrado+=valorZpSolicitante+",";
        }
        if(valorZpVerificador!=-1){
            mensajeCifrado+=valorZpVerificador+",";
        }
        if(numeroSaltos != -1){
            mensajeCifrado+=numeroSaltos+",";
        }
        if(msjeEncriptadoPart != null){
            mensajeCifrado+=msjeEncriptadoPart+",";
        }
        if(prueba!= null){
            mensajeCifrado+=prueba+",";
        }
        if(valorHMac != -1){
            mensajeCifrado+=valorHMac+",";
        }                
        mensaje = mensajePlano+mensajeCifrado;
        return mensaje;        
    }
    
    
    
    
    
    /*
    public String getStringMensaje(CBI cbi){
        
        String mensaje;        
        String mensajePlano = identificadorProtocolo+identificadorEmisor+identificadorVerificador;        
        String mensajeCifrado = "";
        
        if(identificadorSolicitante != null || identificadorVerificador != null){
            mensajeCifrado+= identificadorSolicitante +","+identificadorVerificador+",";
        }
        if(nonceFresh != -1){
            mensajeCifrado+= nonceFresh+",";
        }
        if(nonceFreshRespuesta!= -1){
            mensajeCifrado+=nonceFreshRespuesta+",";
        }
        if(timestamp!= null){
            mensajeCifrado+=timestamp+",";
        }
        if(nonceLive!= -1){
            mensajeCifrado+=nonceLive+",";
        }
        if(nonceLiveRespuesta!=-1){
            mensajeCifrado+=nonceLiveRespuesta+",";
        }
        if(cred != null){
            mensajeCifrado+=cred+",";
        }
        if(aleatorioSolicitante!= -1){
            mensajeCifrado+=aleatorioSolicitante+",";
        }
        if(aleatorioVerificador!= -1){
            mensajeCifrado+=aleatorioVerificador+",";
        }
        if(valorZpSolicitante!= -1){
            mensajeCifrado+=valorZpSolicitante+",";
        }
        if(valorZpVerificador!=-1){
            mensajeCifrado+=valorZpVerificador+",";
        }
        if(numeroSaltos != -1){
            mensajeCifrado+=numeroSaltos+",";
        }
        if(msjeEncriptadoPart != null){
            mensajeCifrado+=msjeEncriptadoPart+",";
        }
        if(prueba!= null){
            mensajeCifrado+=prueba+",";
        }
        if(valorHMac != -1){
            mensajeCifrado+=valorHMac+",";
        }
        if(firmaHash != null){
            mensajeCifrado+=firmaHash+",";
        }
        if(valorHash != -1){
            mensajeCifrado+=valorHash+",";
        }
        mensajeCifrado = cbi.encriptar(mensajeCifrado, identificadorReceptor);                
        mensaje = mensajePlano+mensajeCifrado;
        return mensaje;        
    }            
    */
}

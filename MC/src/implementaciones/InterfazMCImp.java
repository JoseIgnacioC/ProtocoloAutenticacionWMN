/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementaciones;

import interfaces.*;
import vistas.PanelCliente;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;

import protocolo.*;
/**
 *
 * @author Jose Ignacio
 */

public class InterfazMCImp extends UnicastRemoteObject implements InterfazMC {
                
    
    //Datos del cliente
    public String id;
    public String password;
    //public String llavePrivada;    
    public CBI cbi;
    public FirmaUmbral sistemaFU;
    public Log log;
    
    //Interfaces posibles verificadores
    public ArrayList interfacesNodos = new ArrayList();
    public ArrayList<String> nombresNodos = new ArrayList();
    public ArrayList<String> tipoNodos = new ArrayList();
                
    //Sesiones activas
    public ArrayList<SesionProtocolo> sesionesActivas = new ArrayList();
    
    //Canales seguros, lista de nodos con los que mantiene una llave de sesión
    public ArrayList interfacesAutenticados = new ArrayList();
    public ArrayList<String> nombreAutenticados = new ArrayList<>();
    
    
    public ArrayList llavesPublicas = new ArrayList();
    public ArrayList <String> nombresPublicos = new ArrayList();
            
    //Información del verificador
    public ArrayList interfazVerificador = new ArrayList();
    public String nombreVerificador;
    public String tipoVerificador;
            
    public InterfazMCImp() throws RemoteException{        
    }
    
    public InterfazMCImp(String id, String password, String llavePrivada) throws RemoteException{
        this.id = id;
        this.password = password;        
        //this.llavePrivada = llavePrivada;  
        this.log = new Log(id);
    }
    
    @Override
    public void recibirInterfazMRs(InterfazMR interfazMRs, int cantidad) throws RemoteException{
        for(int i=1; i <= cantidad;i++){            
            this.interfacesNodos.add(interfazMRs);
            this.nombresNodos.add("Router"+i);
            this.tipoNodos.add("MR");
        }
        
        PanelCliente.getInstanciaPanelCliente().agregarRoutersListaVerificadores(cantidad);        
    }
    
    @Override
    public void recibirInterfazMC(InterfazMC interfazMC, String identificador) throws RemoteException{
                
        this.interfacesNodos.add(interfazMC);
        this.nombresNodos.add(identificador);
        this.tipoNodos.add("MC");
        
        PanelCliente.getInstanciaPanelCliente().agregarMCsListaVerificadores(identificador);
    }
    
    @Override
    public void enviarInterfaz(String destinatario, String tipo) throws RemoteException{
        Object interfaz = new Object();
        String tipoNodo="";
                
        for (SesionProtocolo sesionActiva : this.sesionesActivas) {
            if(destinatario.equals(sesionActiva.identificadorContrario)){
                
                interfaz = sesionActiva.interfazContrario;
                tipoNodo = sesionActiva.tipoNodo;
                break;
            }
        }
        if(tipoNodo.equals("MR") && tipo.equals("MR")){
            InterfazMR interfazMR = (InterfazMR)interfaz;
            interfazMR.recibirInterfaz(this,this.id,destinatario);
        }
        else if(tipoNodo.equals("MC") && tipo.equals("MC")){
            InterfazMC interfazMC = (InterfazMC)interfaz;
            interfazMC.recibirInterfazMC(this,this.id);
        }
        else
            System.err.println("Error en el tipo de nodo en objeto sesion");
    }        
    
    @Override
    public void recibirInfoGeneral(int n, int p) throws RemoteException{
        sistemaFU = new FirmaUmbral(n, p);
    }
    
    @Override
    public void recibirLlavesPropias(Object parLlaves/*, Object sK, Object PMK*/) throws RemoteException{
        
        KeyPair llavesRsa = (KeyPair) parLlaves;
        //CipherParameters llavePrivada = (CipherParameters) sK;
        //CipherParameters PMK_PKG = (CipherParameters) PMK;        
        
        try {
            this.cbi = new CBI(id, /*llavePrivada, */llavesRsa/*, PMK_PKG*/);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    @Override
    public void recibirPkContrario(Object pK, String identificador) throws RemoteException{
                
        nombresPublicos.add(identificador);
        llavesPublicas.add(pK);
    }
    
    @Override
    public void recibirRouterVerificador(InterfazMR interfazMR, String nombreRouter, String llaveSesion ) throws RemoteException{
        
        interfazVerificador.add(interfazMR);
        nombreVerificador = nombreRouter;
        BaseDatos bd = new BaseDatos(id);
        bd.agregarNuevo(nombreRouter, llaveSesion, 0);
        
        PanelCliente.getInstanciaPanelCliente().actualizarEstado(false, nombreVerificador);    
        PanelCliente.getInstanciaPanelCliente().cambiarVIsta();
        PanelCliente.getInstanciaPanelCliente().agregarCanal(id, nombreVerificador);                                    
    }    
    
    @Override
    public void enviarMensaje(String mensaje, String receptor, boolean enviarPk) throws RemoteException{
                        
        Object interfaz = new Object();
        String tipoNodo="";
        
        //interfazMR.recibirMensajeMC(mensaje,this,emisor,indice);
        for (SesionProtocolo sesionActiva : this.sesionesActivas) {
            if(receptor.equals(sesionActiva.identificadorContrario)){
                
                interfaz = sesionActiva.interfazContrario;
                tipoNodo = sesionActiva.tipoNodo;
                break;
            }
        }
        
        //PanelCliente.getInstanciaPanelCliente().agregarListMensajes(mensaje);
        
        if(tipoNodo.equals("MR")){
            
            InterfazMR interfazMR = (InterfazMR)interfaz;
            
            if (enviarPk)
                interfazMR.recibirPkContrario(cbi.getKeyRsa().getPublic(), id);
            
            log.writePaso(id, receptor, mensaje);
            interfazMR.recibirMensaje(mensaje, this.id, receptor);
        }
        else if(tipoNodo.equals("MC")){
            InterfazMC interfazMC = (InterfazMC)interfaz;
            
            if (enviarPk)
                interfazMC.recibirPkContrario(cbi.getKeyRsa().getPublic(), id);
            
            log.writePaso(id, receptor, mensaje);
            interfazMC.recibirMensaje(mensaje, this.id);
        }
        else
            System.err.println("Error en el tipo de nodo en objeto sesion");                
    }
            
    @Override
    public void recibirMensaje(String mensaje, String emisor) throws RemoteException{                              
        
        boolean findSesion = false;
                
        //PanelCliente.getInstanciaPanelCliente().agregarListMensajes(mensaje);
        
        for(SesionProtocolo sesion : sesionesActivas){
            if(sesion.identificadorContrario.equals(emisor)){//Si se tiene una sesion activa
                findSesion = true;
                
                if(sesion.isSesionPrincipal()){//Si es sesion principal entonces es solicitante y el mensaje viene del verificador
                        
                    sesion.nuevoMsjeRecibido();
                    if(nombreVerificador.isEmpty())
                        sigPasoSolicitante(emisor, mensaje,sesion);
                    else
                        sigPasoVerificador(emisor, mensaje, sesion);                    
                }
                else{//Si no, es la respuesta a un mensaje en donde se colabora
                    System.out.println("Colaborando");
                    sigPasoColaborador(emisor, mensaje, false);
                }                
                break;
            }            
        }
        if(!findSesion){//Si no hay sesion actual, entonces es una solicitud
            
            //verificar si comparte llave de sesion
            BaseDatos bd = new BaseDatos(id);
            String llave = bd.obtenerLlaveSesion(emisor);
            
            if(llave.isEmpty()){ //MC es verificador, y se realiza el paso 2
                System.out.println("MC verificador");
                sigPasoVerificador(emisor, mensaje, null);
            }
            else{                                
                sigPasoColaborador(emisor, mensaje, true);                
            }
        }                
    }
    
    @Override
    public void iniciarProtocolo(String verificador) throws RemoteException{                
        
        int indice;
        String palabrasReceptor[] = verificador.split(" ");
        String identificador = palabrasReceptor[0];
        String tipoNodo = palabrasReceptor[1];
        SesionProtocolo sesion = new SesionProtocolo();
        
        if(tipoNodo.equals("MR")){
            int indexUltimoCar = identificador.length()-1;                                    
            String strIndex = identificador.substring(indexUltimoCar);                                                            
            indice = Integer.parseInt(strIndex);            
            String nombre = "Router"+indice;
            indice = nombresNodos.indexOf(nombre);
            InterfazMR interfazMR = (InterfazMR) interfacesNodos.get(indice);
            sesion = new SesionProtocolo(interfazMR,identificador,this.id+identificador,tipoNodo,true);
            sesionesActivas.add(sesion);                        
        }
        else if(tipoNodo.equals("MC")){
            indice = nombresNodos.indexOf(identificador);
            if(indice > -1){
                InterfazMC interfazMC = (InterfazMC) interfacesNodos.get(indice);
                sesion = new SesionProtocolo(interfazMC,identificador,this.id+verificador,tipoNodo,true);
                sesionesActivas.add(sesion);
            }
            else{
                System.err.println("ERROR GRAVE: Interfaz no encontrada (iniciarProtocolo)");
            }
        }
        else{
            System.err.println("Error en el tipo de nodo");            
        }
        enviarInterfaz(identificador, tipoNodo);
        
        log.writeInfo(id, identificador, "Inicia ejecución para autenticarse ante:");
        
        sigPasoSolicitante(identificador, null, sesion);                       
    }
    
    @Override
    public void sigPasoSolicitante(String verificador, String mensajeRecibido, SesionProtocolo sesion) throws RemoteException{
        
        if(mensajeRecibido == null)//Primer paso                        
            paso1(verificador);
        
        else{//siguientes pasos
                        
            //Buscar llave publica
            int idPublicKey = -1;
            for(String nombre : nombresPublicos){
                idPublicKey++;
                if(nombre.equals(verificador)){
                    break;
                }                
            }
            PublicKey publicKeyVerificador = (PublicKey)llavesPublicas.get(idPublicKey);            
            String datos[] = mensajeRecibido.split(",");
            
            switch (sesion.nroRecibidos){
                case 1://Paso 3 del protocolo, 2do paso del solicitante, 1ra respuesta                    
                    paso3(datos, verificador, publicKeyVerificador);                    
                    break;
                
                case 2://Paso 5 del protocolo, 3er paso del solicitante, 2da Respuesta                    
                    paso5(datos, verificador, publicKeyVerificador);
                    break;
                    
                case 3://paso 11A o 13B                    
                    paso11A13B(datos);
                    break;
                    
                default:
                    System.err.println("Error, paso no encontrado en MC");
                    break;                    
            }          
        }        
    }
    
    @Override
    public void sigPasoColaborador(String emisor, String mensajeRecibido, boolean isNuevo) throws RemoteException{
        
        try {
            //Desencriptar mensaje con llave de sesion
            
            BaseDatos bd = new BaseDatos(id);
            String datos[] = mensajeRecibido.split(",");
            String msjeAux = "";
                        
            String tipoNodo = "MC";
            if(emisor.startsWith("Router")){
                tipoNodo = "MR";
            }   String llaveSesion = bd.obtenerLlaveSesion(emisor);
            
            DES des = new DES(llaveSesion);
            //desencriptar
            if(datos.length == 4){
                try {
                    msjeAux = des.desencriptar(datos[3]);
                } catch (Exception ex) {
                    Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);                
                }
                datos[3] = msjeAux;
            }
            else{
                System.err.println("Error en la cantidad de datos");
            }   
            
            if(isNuevo){
                
                if(emisor.equals(this.nombreVerificador)){//Es el mensaje del paso 7A u 8B                    
                    paso8A9B(datos, emisor, tipoNodo, bd);
                }
                else{//Es un mensaje desde otro nodo (un nodo verificado) -> Un salto del paso 6B
                    System.out.println("Es el escenario B?");
                    saltoPaso6B(datos);
                }                
            }
            else{ //respuestas a mensajes anteriores
                
                if(emisor.equals(this.nombreVerificador)){//Es el mensaje de un salto del paso 11B
                    saltoPaso8A9B11B(datos);
                }
                else
                    saltoPaso8A9B11B(datos);                             
            }
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    @Override
    public void sigPasoVerificador(String emisor, String msjeRecibido, SesionProtocolo sesion) throws RemoteException{
        
        String datos[] = msjeRecibido.split(",");
                        
        //Buscar llave publica
        int idPublicKey = -1;
        for(String nombre : nombresPublicos){
            idPublicKey++;
            if(nombre.equals(emisor)){
                break;
            }                
        }
        PublicKey publicKeySolicitante = (PublicKey)llavesPublicas.get(idPublicKey);
        
        int cont = 0;
        for(String nombreEmisor: nombresNodos){
            if(nombreEmisor.equals(emisor)){                
                break;
            }
            cont++;
        }
        
        if(sesion == null){
            String nombreSesion = this.id+emisor;
            SesionProtocolo sesionNueva = new SesionProtocolo(interfacesNodos.get(cont), emisor, nombreSesion, "MC", true);
            sesionNueva.nuevoMsjeRecibido();
            sesion = sesionNueva;
        }
        
        switch (sesion.nroRecibidos){
            case 1://Paso 2
                
                paso2B(datos, emisor, publicKeySolicitante, sesion);                                                                                                                
                break;
            
            case 2://Paso 4 del protocolo, segundo paso del verificador
                                
                paso4B(datos, emisor, publicKeySolicitante, sesion);
                break;                                
            
            case 3://Paso 6 del protocolo, tercer paso del verificador
                                               
                paso6B(datos, emisor, publicKeySolicitante, sesion);
                break;                
            
            case 4: //Paso 13B, ultimo paso del verificador
                paso12B(datos, emisor, publicKeySolicitante, sesion);
                break;
            default:
                System.err.println("Error, paso no encontrado en MC");
                break;
                
            
        }
        log.writeInfo(this.id, emisor, "Finaliza la ejecución autenticando a:");
    }
        
    public void paso1(String verificador) throws RemoteException{
        
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor= id;
        objMsjeRespuesta.nonceFresh = obtenerNuevoNonce();
        objMsjeRespuesta.setValorHash(cbi);

        String msjeEnviar = objMsjeRespuesta.crearStrPaso1();
        enviarMensaje(msjeEnviar, verificador, true);
    }    
    public void paso2B(String datos[], String emisor, PublicKey publicKeySolicitante, SesionProtocolo sesion) throws RemoteException{
        
        //No hay nada que desencriptar
        Mensaje objMsjeRecibido = new Mensaje(datos, "1");//Construir mensaje del paso 1 en objeto

        log.writeInfo(objMsjeRecibido.identificadorReceptor, objMsjeRecibido.identificadorEmisor, "Inicia la ejecución como verificador ante:");

        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);

        //generar respuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = this.id;
        objMsjeRespuesta.identificadorReceptor = emisor;
        objMsjeRespuesta.nonceFresh = obtenerNuevoNonce();
        objMsjeRespuesta.nonceFreshRespuesta = (objMsjeRespuesta.nonceFresh + objMsjeRecibido.nonceFresh);
        objMsjeRespuesta.nonceLive = obtenerNuevoNonce();
        objMsjeRespuesta.setValorHash(this.cbi);

        sesion.setNonceFreshness(objMsjeRespuesta.nonceFreshRespuesta);
        sesion.setNonceLivenessContrario(-1);
        sesion.setNonceLivenessPropio(objMsjeRespuesta.nonceLive);

        this.sesionesActivas.add(sesion);

        String msjeEnviar = "";
        try {
            msjeEnviar = objMsjeRespuesta.crearStrPaso2(this.cbi,publicKeySolicitante);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        enviarMensaje(msjeEnviar, objMsjeRespuesta.identificadorReceptor, false);
    }    
    public void paso3(String datos[], String verificador, PublicKey publicKeyVerificador) throws RemoteException{
        
        String msjeAux = "";
        
        //desencriptar
        if(!(datos.length < 4)){

            try {
                msjeAux = cbi.desencriptar(datos[3]);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
            datos[3] = msjeAux;                                                
        }
        Mensaje objMsjeRecibido = new Mensaje(datos, "2");//Armo el mensaje del paso 2 en objeto

        log.writePaso(objMsjeRecibido.identificadorEmisor, id, datos);

        //verificacion

        //generarRespuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = this.id;
        objMsjeRespuesta.identificadorReceptor = verificador;
        objMsjeRespuesta.nonceFreshRespuesta = (objMsjeRecibido.nonceFreshRespuesta + 1);
        objMsjeRespuesta.nonceLive = obtenerNuevoNonce();
        objMsjeRespuesta.nonceLiveRespuesta = (objMsjeRecibido.nonceLive - 1);
        objMsjeRespuesta.setValorHash(cbi);

        String msjeEnviar = "";
        {
            try {
                msjeEnviar = objMsjeRespuesta.crearStrPaso3(cbi, publicKeyVerificador);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        enviarMensaje(msjeEnviar, verificador, false);
    }    
    public void paso4B(String datos[], String emisor, PublicKey publicKeySolicitante, SesionProtocolo sesion) throws RemoteException{
        
        //desencriptar
        String msjeAux = "";
        if(!(datos.length < 4)){

            try {
                msjeAux = this.cbi.desencriptar(datos[3]);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
                datos[3] = msjeAux;
        }
        Mensaje objMsjeRecibido = new Mensaje(datos, "3");
        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);

        sesion.setNonceFreshness(objMsjeRecibido.nonceFreshRespuesta);
        sesion.setNonceLivenessContrario(objMsjeRecibido.nonceLive);                

        //verificacion

        //generar respuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = this.id;
        objMsjeRespuesta.identificadorReceptor = emisor;
        objMsjeRespuesta.nonceFreshRespuesta = (objMsjeRecibido.nonceFreshRespuesta + 1);
        objMsjeRespuesta.nonceLive = obtenerNuevoNonce();
        objMsjeRespuesta.nonceLiveRespuesta = (objMsjeRecibido.nonceLive - 1);
        objMsjeRespuesta.aleatorioVerificador = obtenerAleatorioGrande();
        objMsjeRespuesta.setValorHash(this.cbi);

        //almacenar datos a utilizar mas adelante                
        sesion.setAleatorioPropio(objMsjeRespuesta.aleatorioVerificador);
        sesion.setNonceLivenessPropio(objMsjeRespuesta.nonceLive);

        String msjeEnviar = "";
        {
            try {
                msjeEnviar = objMsjeRespuesta.crearStrPaso4(this.cbi,publicKeySolicitante);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        enviarMensaje(msjeEnviar, objMsjeRespuesta.identificadorReceptor, false);
    }    
    public void paso5(String datos[], String verificador, PublicKey publicKeyVerificador) throws RemoteException{
        
        String msjeAux = "";

        //desencriptar
        if(!(datos.length < 4)){

            try {
                msjeAux = cbi.desencriptar(datos[3]);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
                datos[3] = msjeAux;                                                
        }
        Mensaje objMsjeRecibido = new Mensaje(datos, "4");//Armo el mensaje del paso 4 en objeto

        log.writePaso(objMsjeRecibido.identificadorEmisor, id, datos);

        //verificacion 

        //generarRespuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = this.id;
        objMsjeRespuesta.identificadorReceptor = verificador;
        objMsjeRespuesta.nonceFreshRespuesta = (objMsjeRecibido.nonceFreshRespuesta + 1);
        objMsjeRespuesta.nonceLive = obtenerNuevoNonce();
        objMsjeRespuesta.nonceLiveRespuesta = (objMsjeRecibido.nonceLive - 1);
        objMsjeRespuesta.cred = generarCredencial(verificador, objMsjeRecibido.aleatorioVerificador);
        objMsjeRespuesta.aleatorioSolicitante = obtenerAleatorioGrande();
        objMsjeRespuesta.valorZpSolicitante = obtenerValorZp(this.id+verificador);
        objMsjeRespuesta.setValorHash(cbi);
        
        String msjeEnviar = "";
        {
            try {
                msjeEnviar = objMsjeRespuesta.crearStrPaso5(cbi, publicKeyVerificador);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        enviarMensaje(msjeEnviar, verificador, false);
    }   
    public void paso6B(String datos[], String emisor, PublicKey publicKeySolicitante, SesionProtocolo sesion) throws RemoteException{
        
        //desencriptar
        String msjeAux = "";
        if(!(datos.length < 4)){

            try {
                msjeAux = this.cbi.desencriptar(datos[3]);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
                datos[3] = msjeAux;
        }
        Mensaje objMsjeRecibido = new Mensaje(datos, "5");
        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);

        sesion.setNonceFreshness(objMsjeRecibido.nonceFreshRespuesta);
        sesion.setNonceLivenessContrario(objMsjeRecibido.nonceLive);
        sesion.setCredencialRecibida(objMsjeRecibido.cred);
        //verificacion

        //generar respuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = this.id;
        objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorEmisor;
        objMsjeRespuesta.identificadorVerificador = this.id;
        objMsjeRespuesta.nonceFresh = obtenerNuevoNonce();//Esto deberia ser por bd
        objMsjeRespuesta.aleatorioVerificador = sesion.aleatorioPropio;
        objMsjeRespuesta.aleatorioSolicitante = objMsjeRecibido.aleatorioSolicitante;
        objMsjeRespuesta.valorZpVerificador = obtenerValorZp(sesion.nombreSesion);
        objMsjeRespuesta.setValorHash();

        sesion.setNonceLivenessPropio(objMsjeRespuesta.nonceLive);
        sesion.setAleatorioContrario(objMsjeRecibido.aleatorioSolicitante);                
        sesion.setAleatorioZpContrario(objMsjeRecibido.valorZpSolicitante);

        String nombreSesion = objMsjeRespuesta.identificadorVerificador+objMsjeRespuesta.identificadorSolicitante;

        InterfazMC interfazMC;
        InterfazMR interfazMR;
        SesionProtocolo sesionSecundaria;
        if(tipoVerificador.equals("MC")){

            interfazMC = (InterfazMC) interfazVerificador.get(0);
            sesionSecundaria = new SesionProtocolo(interfazMC, nombreVerificador, nombreSesion, "MC", false);
        }
        else{
            interfazMR = (InterfazMR) interfazVerificador.get(0);
            sesionSecundaria = new SesionProtocolo(interfazMR, nombreVerificador, nombreSesion, "MR", false);
        }

        this.sesionesActivas.add(sesionSecundaria);

        BaseDatos bd = new BaseDatos(id);
        DES des = null;
        String llaveSesion = bd.obtenerLlaveSesion(nombreVerificador);

        try {
            des = new DES(llaveSesion);                            
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        objMsjeRespuesta.identificadorReceptor = nombreVerificador;

        String msjeEnviar = "";
        try {
            msjeEnviar = objMsjeRespuesta.crearStrPaso6A7B(des);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Enviar buscando un mesh router
        log.writePaso(objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor, msjeEnviar);                                                                                        
        enviarMensaje(msjeEnviar, objMsjeRespuesta.identificadorReceptor, false);
        
    }    
    public void saltoPaso6B(String datos[]) throws RemoteException{
        
        Mensaje objMsjeRecibido = new Mensaje(datos, "6B");
        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);
        
        //verificar datos
        
        //crear sesion
        String nombreSesion = objMsjeRecibido.identificadorVerificador+objMsjeRecibido.identificadorSolicitante;
        SesionProtocolo sesion = new SesionProtocolo(this, objMsjeRecibido.identificadorEmisor, nombreSesion, "MC", false);
        sesionesActivas.add(sesion);
        
        //crear mensaje nuevo
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = objMsjeRecibido.identificadorEmisor;
        objMsjeRespuesta.identificadorVerificador = objMsjeRecibido.identificadorVerificador;
        objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorSolicitante;                
        objMsjeRespuesta.aleatorioVerificador = objMsjeRecibido.aleatorioVerificador;
        objMsjeRespuesta.aleatorioSolicitante = objMsjeRecibido.aleatorioSolicitante;
        objMsjeRespuesta.valorZpVerificador = objMsjeRecibido.valorZpVerificador;        
        objMsjeRespuesta.setValorHash();

        //Enviar mensaje
        InterfazMC interfazMC;
        InterfazMR interfazMR;
        SesionProtocolo sesionSecundaria;
        if(tipoVerificador.equals("MC")){

            interfazMC = (InterfazMC) interfazVerificador.get(0);
            sesionSecundaria = new SesionProtocolo(interfazMC, nombreVerificador, nombreSesion, "MC", false);
        }
        else{
            interfazMR = (InterfazMR) interfazVerificador.get(0);
            sesionSecundaria = new SesionProtocolo(interfazMR, nombreVerificador, nombreSesion, "MR", false);
        }

        this.sesionesActivas.add(sesionSecundaria);

        BaseDatos bd = new BaseDatos(id);
        DES des = null;
        String llaveSesion = bd.obtenerLlaveSesion(nombreVerificador);

        try {
            des = new DES(llaveSesion);                            
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        objMsjeRespuesta.identificadorReceptor = nombreVerificador;

        String msjeEnviar = "";
        try {
            msjeEnviar = objMsjeRespuesta.crearStrPaso6A7B(des);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Enviar buscando un mesh router
        log.writePaso(objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor, msjeEnviar);                                                                                        
        enviarMensaje(msjeEnviar, objMsjeRespuesta.identificadorReceptor, false);        
    }    
    public void paso8A9B(String datos[], String emisor, String tipoNodoEmisor, BaseDatos bd ) throws RemoteException, Exception{
        
        //se crea la sesion
        Mensaje objMsjeRecibido = new Mensaje(datos, "7A");

        log.writePaso(objMsjeRecibido.identificadorEmisor, id, datos);

        String nombreSesion = objMsjeRecibido.identificadorVerificador+objMsjeRecibido.identificadorSolicitante;
        SesionProtocolo sesion = new SesionProtocolo(this.interfazVerificador.get(0), emisor, nombreSesion, tipoNodoEmisor, false);
        this.sesionesActivas.add(sesion);

        log.writeInfo(id, nombreSesion, "Colabora en la ejecución de:");
        //verificacion

        //generarRespuesta

        String[] datosCliente = bd.obtenerDatosCliente(objMsjeRecibido.identificadorSolicitante);

        if(datosCliente.length == 0){//Se repite el paso 7A pues no se tiene la particion
            saltoPaso7A8B(datos);
        }
        else{ //Paso 8 - Se tiene que responder con las particiones

            //Se busca la sesion a la cual se va a responder
            String particionSecreta = datosCliente[1];
            int indiceParticion = Integer.parseInt(datosCliente[2]);
            int indiceSesion = 0;
            for (SesionProtocolo sesionActiva : this.sesionesActivas) {

                if(sesionActiva.nombreSesion.equals(objMsjeRecibido.identificadorVerificador+objMsjeRecibido.identificadorSolicitante)){
                    break;
                }
                indiceSesion++;
            }
            //Generar respuesta
            Mensaje objMsjeRespuesta = new Mensaje();
            objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
            objMsjeRespuesta.identificadorEmisor = id;
            objMsjeRespuesta.identificadorReceptor = sesionesActivas.get(indiceSesion).identificadorContrario;
            objMsjeRespuesta.identificadorVerificador = objMsjeRecibido.identificadorVerificador;
            objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorSolicitante;
            objMsjeRespuesta.nonceFreshRespuesta = bd.obtenerNonce(objMsjeRespuesta.identificadorReceptor, true);
            String mensajePart1 = objMsjeRecibido.identificadorVerificador+objMsjeRecibido.identificadorSolicitante+objMsjeRecibido.aleatorioVerificador;
            String mensajePart2 = objMsjeRecibido.identificadorVerificador+objMsjeRecibido.valorZpVerificador+objMsjeRecibido.aleatorioSolicitante;
            objMsjeRespuesta.msjeEncriptadoPart = obtenerCadenaParticiones(indiceParticion, particionSecreta, mensajePart1, mensajePart2, objMsjeRespuesta.identificadorVerificador);
            objMsjeRespuesta.setValorHash();

            String llaveSesion =  bd.obtenerLlaveSesion(objMsjeRespuesta.identificadorReceptor);
            DES des = new DES(llaveSesion);

            String msjeEnviar = objMsjeRespuesta.crearStrPaso8A9B(des);
            enviarMensaje(msjeEnviar, objMsjeRespuesta.identificadorReceptor, false);
        }
    }
    public void saltoPaso7A8B(String datos[]) throws RemoteException{
        
        Mensaje objMsjeRecibido =  new Mensaje(datos, "7A");        
        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);
        
        //verificar datos
        
        //crear sesion
        String nombreSesion = objMsjeRecibido.identificadorVerificador+objMsjeRecibido.identificadorSolicitante;
        SesionProtocolo sesion = new SesionProtocolo(this, objMsjeRecibido.identificadorEmisor, nombreSesion, "MC", false);
        sesionesActivas.add(sesion);
        
        //crear mensaje nuevo
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = objMsjeRecibido.identificadorEmisor;
        objMsjeRespuesta.identificadorVerificador = objMsjeRecibido.identificadorVerificador;
        objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorSolicitante;                
        objMsjeRespuesta.aleatorioVerificador = objMsjeRecibido.aleatorioVerificador;
        objMsjeRespuesta.aleatorioSolicitante = objMsjeRecibido.aleatorioSolicitante;
        objMsjeRespuesta.valorZpVerificador = objMsjeRecibido.valorZpVerificador;        
        objMsjeRespuesta.numeroSaltos = objMsjeRecibido.numeroSaltos - 1;
        objMsjeRespuesta.setValorHash();
                        
        //Enviar mensaje
        
        InterfazMC interfazMC = (InterfazMC) interfazVerificador.get(0);
        SesionProtocolo sesionSecundaria = new SesionProtocolo(interfazMC, nombreVerificador, nombreSesion, "MC", false);        
        this.sesionesActivas.add(sesionSecundaria);
        
        BaseDatos bd = new BaseDatos(id);
        DES des = null;
        String llaveSesion = "";
        String msjeEnviar = "";
        
        //Enviar buscando a cada noto autenticado
        for(String nombreAutenticado : nombreAutenticados){
            
            if(nombreAutenticado != objMsjeRecibido.identificadorEmisor){
                
                objMsjeRespuesta.identificadorReceptor = nombreVerificador;
                llaveSesion = bd.obtenerLlaveSesion(nombreAutenticado);
                
                try {
                    des = new DES(llaveSesion);
                    msjeEnviar = objMsjeRespuesta.crearStrPaso7A8B(des);
                    
                    log.writePaso(objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor, msjeEnviar);                                                                                        
                    enviarMensaje(msjeEnviar, objMsjeRespuesta.identificadorReceptor, false);
                } catch (Exception ex) {
                    Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }                               
    }    
    public void saltoPaso8A9B11B(String datos[]) throws RemoteException{
        
        Mensaje objMsjeRecibido =  new Mensaje(datos, "8A");
        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);
        
        //verificar datos
        
        //cerrar sesion recibido
        
        //crear mensaje nuevo
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = objMsjeRecibido.identificadorEmisor;
        objMsjeRespuesta.identificadorReceptor = nombreVerificador;
        objMsjeRespuesta.identificadorVerificador = objMsjeRecibido.identificadorVerificador;
        objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorSolicitante;                
        
        objMsjeRespuesta.msjeEncriptadoPart = objMsjeRecibido.msjeEncriptadoPart;
        objMsjeRespuesta.setValorHash();
                        
        //cerrar sesion enviado
        
        BaseDatos bd = new BaseDatos(id);
        DES des = null;
        String llaveSesion = bd.obtenerLlaveSesion(nombreVerificador);
        String msjeEnviar = "";
        
        objMsjeRespuesta.nonceFreshRespuesta = bd.obtenerNonce(nombreVerificador, true);;
        
        try {
            des = new DES(llaveSesion);
            msjeEnviar = objMsjeRespuesta.crearStrPaso8A9B(des);

            log.writePaso(objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor, msjeEnviar);                                                                                        
            enviarMensaje(msjeEnviar, objMsjeRespuesta.identificadorReceptor, false);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }            
  
        
    
    public void paso12B(String datos[], String emisor, PublicKey publicKeySolicitante, SesionProtocolo sesion) throws RemoteException{
        
        //desencriptar
        String msjeAux = "";
        if(!(datos.length < 4)){

            try {
                msjeAux = this.cbi.desencriptar(datos[3]);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
                datos[3] = msjeAux;
        }
        Mensaje objMsjeRecibido = new Mensaje(datos, "8A");
        
        
        String cadenaParticionesDes = "";                        
        try {
            cadenaParticionesDes =  this.cbi.desencriptar(objMsjeRecibido.msjeEncriptadoPart);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        CadenaParticiones cp = new CadenaParticiones(cadenaParticionesDes);

        //verificar firmas
        boolean particionesValidadas = sistemaFU.verifificarParticionFirmada(cp.particionMensaje1);

        if(particionesValidadas)
            sesion.addParticionRecibidaM1(cp.particionMensaje1);

        particionesValidadas = sistemaFU.verifificarParticionFirmada(cp.particionMensaje2);

        if(particionesValidadas)
            sesion.addParticionRecibidaM2(cp.particionMensaje2);

        /*if( sesion.partM1Recibidas.size() == this.sistemaFU.paramentroP && 
            sesion.partM2Recibidas.size() == this.sistemaFU.paramentroP){*/

        //Saber cuantos routeres existen
        int nroRouters = 0;
        for(String tipoNodo : tipoNodos){
            if(tipoNodo.equals("MR"))
                nroRouters++;
        }
        
        if(sesion.partM1Recibidas.size() == nroRouters){

            //reconstruir
            String mensaje1 = sistemaFU.reconstruccionMensaje(sesion.partM1Recibidas);
            String mensaje2 = sistemaFU.reconstruccionMensaje(sesion.partM2Recibidas);

            //verificar reconstruccion

            boolean mensajesValidados = sistemaFU.validarMensajeFirmado(mensaje1) && sistemaFU.validarMensajeFirmado(mensaje2);

            if(mensajesValidados){

                //revisar credencial y decidir si autenticar
                boolean autenticarSolicitante = true;

                if(autenticarSolicitante){

                    Mensaje objMsjeRespuesta = new Mensaje();
                    
                    //calcular llave de sesion
                    int nuevaLlaveSesion = sesion.aleatorioZpContrario*sesion.aleatorioZpPropio;//cambiar despues, tiene q ser solo en aleatorio propio

                    System.out.println("Nueva llave de sesion es: "+nuevaLlaveSesion);

                    //generar respuesta                            
                    objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
                    objMsjeRespuesta.identificadorEmisor = id;
                    objMsjeRespuesta.identificadorReceptor = objMsjeRecibido.identificadorSolicitante;
                    objMsjeRespuesta.nonceFreshRespuesta = sesion.nonceFreshness;
                    objMsjeRespuesta.nonceLiveRespuesta = sesion.nonceLivenessContrario;
                    objMsjeRespuesta.valorZpVerificador = sesion.aleatorioZpPropio * Conf.PUNTO_P;
                    objMsjeRespuesta.prueba = obtenerPruebaVerificador(cbi, mensaje2 );
                    objMsjeRespuesta.setValorHMAC(nuevaLlaveSesion);
                    objMsjeRespuesta.setValorHash();

                    //Quitar sesion
                    int index = -1;
                    for(String nombres : nombresPublicos){
                        index++;
                        if(nombres.equals(objMsjeRespuesta.identificadorReceptor)){
                            break;
                        }
                    }                                    
                    PublicKey keyPublicaReceptor = (PublicKey) llavesPublicas.get(index);
                    String msjeEnviar = "";
                    try {
                        msjeEnviar = objMsjeRespuesta.crearStrPaso10A12B(cbi, keyPublicaReceptor);
                    } catch (Exception ex) {
                        Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    PanelCliente.getInstanciaPanelCliente().agregarCanal(objMsjeRespuesta.identificadorReceptor, id);                    
                    enviarMensaje(msjeEnviar, objMsjeRespuesta.identificadorReceptor, false);
                }
            } 
        }
        
        
    }    
    
    
    
    
    public void paso11A13B(String datos[]) throws RemoteException{
                
        //desencriptar
        String msjeAux = "";
        if(!(datos.length < 4)){

        try {
                msjeAux = cbi.desencriptar(datos[3]);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMCImp.class.getName()).log(Level.SEVERE, null, ex);
            }
            datos[3] = msjeAux;                                                
        }

        Mensaje objMsjeRecibido = new Mensaje(datos, "10A");//Armo el mensaje del ultimo paso

        log.writePaso(objMsjeRecibido.identificadorEmisor, id, datos);

        int nuevaLlaveSesion = -1;
        System.out.println("Autenticado - FIN, la prueba es: "+objMsjeRecibido.prueba );

        for(SesionProtocolo sesionActiva : sesionesActivas){
            if(sesionActiva.nombreSesion.equals(this.id+objMsjeRecibido.identificadorEmisor)){
                nuevaLlaveSesion = sesionActiva.aleatorioZpPropio*objMsjeRecibido.valorZpVerificador;
            }
        }

        System.out.println("Nueva llave de sesion es: "+nuevaLlaveSesion);

        int hmacRecibido = objMsjeRecibido.valorHMac;
        objMsjeRecibido.setValorHMAC(nuevaLlaveSesion);
        int hmacCalculado = objMsjeRecibido.valorHMac;

        if(hmacRecibido == hmacCalculado)
            System.out.println("Valor HMAC iguales");
        else
            System.out.println("Error, llaves distintas");

        this.nombreVerificador = objMsjeRecibido.identificadorEmisor;
        PanelCliente.getInstanciaPanelCliente().actualizarEstado(true, nombreVerificador);
        PanelCliente.getInstanciaPanelCliente().cambiarVIsta();
        PanelCliente.getInstanciaPanelCliente().agregarCanal(id, nombreVerificador);
    }
    
    @Override
    public void recibirDatos(ArrayList<String> datos) throws RemoteException{
        
        BaseDatos bd = new BaseDatos(id);
        bd.actualizarRegistroClientes(datos);        
    }
    
    public int obtenerNuevoNonce(){
        Random rnd = new Random();
        return (int)(rnd.nextDouble()*9999+1000);
    }                
    public int obtenerAleatorioGrande(){
        Random rnd = new Random();
        return (int)(rnd.nextDouble()*99999999+10000000);
    }
    public int obtenerValorZp(String nombreSesion){
                                        
        int valor;
        Random rnd = new Random();
        valor = (int)(rnd.nextDouble()*Conf.P+3);
        
        for(SesionProtocolo sesion : sesionesActivas){
            if(sesion.nombreSesion.equals(nombreSesion)){
                sesion.aleatorioZpPropio = valor;
                break;
            }
        }
        
        valor = valor * Conf.PUNTO_P;
        return valor;
    }
    public String generarCredencial(String verificador, int valorAleatorio  ){
        
        Hash hash1 = new Hash(password);
        int intSecreto = hash1.funcionHash();
        String secreto = intSecreto+"";
        RSA rsa = new RSA(secreto);
                
        String cadena = this.id + verificador + valorAleatorio;
        String cred = rsa.encriptar(cadena);
        Hash hash2 = new Hash(cred);
        cred = hash2.funcionHash()+"";
                
        return cred;
    }        
    public String generarParticionFirmada(String mensaje, String particionSecreta){
        
        String particionFirmada = "SIG"+particionSecreta+"/"+mensaje+"/";
        
        return particionFirmada;
    }    
    public String obtenerCadenaParticiones(int indice, String particionSecreta, String mensajeFirmado1, String mensajeFirmado2, String idVerificador) throws Exception{
        
        
        String partFirma1 = generarParticionFirmada(mensajeFirmado1, particionSecreta);
        String partFirma2 = generarParticionFirmada(mensajeFirmado2, particionSecreta);
        
        //int intPartSecreta = Integer.parseInt(particionSecreta); Cuando sea numero
        String particionPublica = "PK"+particionSecreta;
        
        String mensajeParticiones = indice+","+particionPublica+","+partFirma1+","+partFirma2;
        int hashMsjePart = Math.abs(mensajeParticiones.hashCode());
        mensajeParticiones+= ","+hashMsjePart;
                      
        while( ( (mensajeParticiones.length() - 4) % 4) != 0){
            mensajeParticiones+= "1";
        }
        
        PublicKey pKey = null;
        
        for(int i = 0; i < nombresPublicos.size(); i++){
            if(nombresPublicos.get(i).equals(idVerificador)){
               pKey = (PublicKey) llavesPublicas.get(i);
            }
        }
        
        mensajeParticiones = cbi.encriptar(mensajeParticiones, pKey);
        
        return mensajeParticiones;
    }
    
    public String obtenerPruebaVerificador(CBI cbi, String mensajeFirmadoSecreto){
        
        String prueba = cbi.firmar(mensajeFirmadoSecreto);
        
        return prueba;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementaciones;

import interfaces.InterfazMC;
import interfaces.InterfazMR;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.bouncycastle.crypto.CipherParameters;
import protocolo.CBI;
import protocolo.CadenaParticiones;
import protocolo.Conf;
import protocolo.DES;
import protocolo.FirmaUmbral;
import protocolo.Mensaje;
import protocolo.SesionProtocolo;
import vistas.PanelRouters;

/**
 *
 * @author Jose Ignacio
 */
public class InterfazMRImp extends UnicastRemoteObject implements InterfazMR {
    
    //public ArrayList interfacesMCsActuales = new ArrayList();
    //public ArrayList <String> nombresMCsActuales = new ArrayList();
    //public ArrayList interfacesMCsAutenticados = new ArrayList();
    //public ArrayList <String> nombreMCsAutenticados = new ArrayList();
    
    //Interfaces recibidas
    public ArrayList interfacesMCs = new ArrayList();
    
    public ArrayList<String> nombresMCs = new ArrayList();  
    
    public ArrayList<MR> meshRouters = new ArrayList();
    
    public ArrayList<String> nombresPublicos = new ArrayList();
    
    public ArrayList llavesPublicas = new ArrayList();
    
    public FirmaUmbral sistemaFU;
    
    public Log log = new Log("MeshRouters");
        
    
    public InterfazMRImp() throws RemoteException{
             
    }
    
    public InterfazMRImp(int cantidadRouters) throws RemoteException, Exception{
        
        
        for(int i = 1; i <= cantidadRouters; i++){
            String id = "Router"+i;
            //String llavePrivada = "SKr"+i;
            
            meshRouters.add(new MR(id));                        
            BaseDatos bd = new BaseDatos(id);
            
            
            for(int j = 1; j <= cantidadRouters; j++){
                if(i!=j){
                    if(i < j)
                        bd.agregarNuevo("Router"+j, "SERouter"+i+"Router"+j, -1);
                    if(i > j)
                        bd.agregarNuevo("Router"+j, "SERouter"+j+"Router"+i, -1);
                    
                    PanelRouters.getInstanciaPanelRouters().agregarRouterAutenticado(i, j);
                }
            }
            PanelRouters.getInstanciaPanelRouters().agregarNotificacion(i, "Router conectado y autenticado");        
        }
    }
    
    @Override
    public void recibirInterfaz(InterfazMC interfaz, String emisor, String routerDestinatario) throws RemoteException{
        
        this.interfacesMCs.add(interfaz);
        this.nombresMCs.add(emisor);
        boolean find = false;
        
        
        for (MR meshRouter : meshRouters) {            
            if(routerDestinatario.equals(meshRouter.getIdentificador())){
                SesionProtocolo sesion = new SesionProtocolo(interfaz, emisor, emisor+routerDestinatario,"MC", true);
                meshRouter.addSesionActiva(sesion);
                find = true;
                interfaz.recibirPkContrario(meshRouter.getCbi().getKeyRsa().getPublic(), routerDestinatario);
                break;
            }
        }
        if(!find)
            System.err.println("ERROR: Router destino no encontrado para entregar interfaz");
 
    }
    
    @Override
    public void enviarPkVerificador(String idVerificador, String emisor, String receptor) throws RemoteException{
        
        Object interfaz = new Object();                        
        for(MR router:meshRouters){  
            
            if(emisor.equals(router.getIdentificador())){                                
                for(SesionProtocolo sesion:router.getSesionesActivas()){                    
                    if(sesion.identificadorContrario.equals(receptor)){                        
                        interfaz = (InterfazMC)sesion.interfazContrario;
                        break;
                    }
                }
                break;
            }            
        }
        PublicKey keyP = null; 
        for(int i = 0; i < nombresPublicos.size(); i++){
            if(nombresPublicos.get(i).equals(idVerificador)){
                 keyP = (PublicKey) llavesPublicas.get(i);
            }
        }
        
        InterfazMC interfazMC = (InterfazMC)interfaz;   
        interfazMC.recibirPkContrario(keyP, idVerificador);
    }
    
    @Override
    public void enviarMensajeMC(String mensaje, String emisor, String receptor) throws RemoteException{
                        
        Object interfaz = new Object();
        int nroRouter = 0;        
        for(MR router:meshRouters){
            
            nroRouter++;
            if(emisor.equals(router.getIdentificador())){                                
                for(SesionProtocolo sesion:router.getSesionesActivas()){                    
                    if(sesion.identificadorContrario.equals(receptor)){                        
                        interfaz = (InterfazMC)sesion.interfazContrario;
                        break;
                    }
                }
                break;
            }
        }                   
        
        log.writePaso(emisor, receptor, mensaje);
        
        //PanelRouters.getInstanciaPanelRouters().agregarMensaje(mensaje,nroRouter);
        InterfazMC interfazMC = (InterfazMC)interfaz;        
        interfazMC.recibirMensaje(mensaje, emisor);                
    }
    
    @Override
    public void recibirMensaje(String mensaje, String emisor, String receptor) throws RemoteException{                                        
                       
        boolean findRouter = false;
        boolean findSesion = false;
        int nroReceptor = 0;                
        
        for (MR meshRouter : meshRouters) {            
            
            nroReceptor++;
            if(receptor.equals(meshRouter.getIdentificador())){//Encontrar al mesh router destinatario
                
                //PanelRouters.getInstanciaPanelRouters().agregarMensaje(mensaje,nroReceptor);
                
                for(SesionProtocolo sesion : meshRouter.getSesionesActivas()){
                    
                    if(sesion.identificadorContrario.equals(emisor)){
                        
                        if(sesion.isSesionPrincipal()){//sesion principal, es decir, el MR es verificador
                                                                                    
                            sesion.nuevoMsjeRecibido();
                            sigPasoVerificador(receptor,emisor,mensaje,sesion);                            
                        }
                        else{//Si se establece comunicación con un usuario en la ejecucion de otra sesion
                             
                            //revisar luego
                            
                            if(emisor.startsWith("Router")){//Respuesta de una solicitud, router receptor es verificador en escenario A
                                
                                sigPasoColaborador(receptor, emisor, mensaje, false);
                            }
                            else{//Respuesta desde un MC tras la inundacion
                                sigPasoColaborador(receptor, emisor, mensaje, false);                                
                            }
                        }
                        findSesion = true;
                        break;
                    }
                }
                if(!findSesion){ //Si no hay sesion actual con la entidad de la cual se recibio el msje
                                        
                    sigPasoColaborador(receptor, emisor, mensaje, true);
                }
                findRouter = true;
                break;
            }
        }
        if(!findRouter){
            System.err.println("ERROR: Router destino no encontrado para entregar interfaz");            
        }                        
    }
             
    @Override
    public void recibirLlavesRouters(Object parLlaves, /*Object sK, Object PMK,*/ String idRouter ) throws RemoteException{
        
        //CipherParameters llavePrivada = (CipherParameters) sK;
        KeyPair parRsa = (KeyPair) parLlaves;
        //CipherParameters pmk = (CipherParameters)PMK;
        
        for(MR router : this.meshRouters){
            if(router.getIdentificador().equals(idRouter)){
                router.setCbi(/*llavePrivada,*/ parRsa/*, pmk*/);
                break;
            }
        }        
        nombresPublicos.add(idRouter);
        llavesPublicas.add(parRsa.getPublic());        
    }
    
    @Override
    public void recibirInfoGeneral(int n, int p) throws RemoteException{
        sistemaFU = new FirmaUmbral(n, p);
    }
    
    @Override
    public void recibirPkContrario(Object publicKey, String id) throws RemoteException{
        nombresPublicos.add(id);
        llavesPublicas.add(publicKey);
    }
    
    @Override
    public void recibirClienteAutenticado(InterfazMC meshCliente, String idCliente, String llaveSesion, String nombreRouter) throws RemoteException{
                
        int indiceRouter = 0;
        for(MR mr : meshRouters){
            indiceRouter++;
            if(mr.getIdentificador().equals(nombreRouter)){
                mr.addMCAutenticado(idCliente);
                mr.addInterfazMCAutenticado(meshCliente);
                break;
            }
        }
        
        BaseDatos bd = new BaseDatos(nombreRouter);
        bd.agregarNuevo(idCliente, llaveSesion, 0);
        
        PanelRouters.getInstanciaPanelRouters().agregarNotificacion("Router"+indiceRouter+" finaliza la autenticación con "+ idCliente +" satisfactoriamente");
        PanelRouters.getInstanciaPanelRouters().agregarAutenticado(indiceRouter, idCliente);        
    }
            
    @Override
    public void sigPasoVerificador(String routerVerificador, String solicitante, String mensaje, SesionProtocolo sesion) throws RemoteException{
                                                      
        String datos[] = mensaje.split(",");
        //Buscar llave publica del solicitante
        int idPublicKey = -1;
        for(String nombre : nombresPublicos){
            idPublicKey++;
            if(nombre.equals(solicitante)){
                break;
            }                
        }
        PublicKey publicKeySolicitante = (PublicKey)llavesPublicas.get(idPublicKey);
        
        //Buscar router verificador
        MR routerActual =  new MR();
        int indiceRouter = -1;
        for(MR router: this.meshRouters){
            indiceRouter++;
            if(routerVerificador.equals(router.getIdentificador())){//Encontrar al mesh router destinatario
                routerActual = router;
                break;
            }            
        }        
        
        switch (sesion.nroRecibidos) {
            case 1://Paso 2 del protocolo, primer paso del verificador
                
                paso2A(datos, routerVerificador, routerActual, solicitante, publicKeySolicitante, sesion);
                break;
                
            case 2://Paso 4 del protocolo, segundo paso del verificador
                
                paso4A(datos, routerVerificador, routerActual, solicitante, publicKeySolicitante, sesion);
                break;
                
            case 3://Paso 6 del protocolo, tercer paso del verificador
                                               
                paso6A(datos, routerVerificador, routerActual, indiceRouter, solicitante, sesion);                
                break;
                
            default:                
                System.err.println("Error, paso no encontrado en MR");
                break;
        }  
        log.writeInfo(routerVerificador, solicitante, "Finaliza la ejecución autenticando a:");
    }
    
    @Override
    public void sigPasoColaborador(String routerColaborador, String emisorMensaje, String mensaje, boolean isNuevo) throws RemoteException{
        
        //Parametros generales
        BaseDatos bd = new BaseDatos(routerColaborador);
        DES des = new DES();
        
        //obtener router colaborador actual        
        int indiceRouter = -1;        
        for(MR router:meshRouters){            
            indiceRouter++;
            if(router.getIdentificador().equals(routerColaborador)){                
                break;
            }            
        }        
        String datos[] = mensaje.split(",");
        String llaveSesion = "";
        String msjeAux = "";

        if(!routerColaborador.equals(emisorMensaje)){//usado para diferenciar los auto mensajes del verificador
            llaveSesion = bd.obtenerLlaveSesion(emisorMensaje);
            try {                        
                des = new DES(llaveSesion);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
            }
            //desencriptar
            if(datos.length == 4){
                try {
                    msjeAux = des.desencriptar(datos[3]);
                } catch (Exception ex) {
                    Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
                }
                datos[3] = msjeAux;
            }
            else{
                System.err.println("Error en la cantidad de datos");
            }
        }
        
        if(isNuevo){//Si es nuevo, hay que crear nueva sesion            
            if(emisorMensaje.startsWith("Router"))//Un nuevo mensaje desde un router -> Paso 7A o 8B
                paso7A8B(datos, emisorMensaje, routerColaborador, indiceRouter, bd, des);
                            
            else//Un nuevo mensaje desde un mesh client --- rebir mensaje 6B, paso 7B
                paso7B(datos, routerColaborador, emisorMensaje, indiceRouter, bd, des);
        }
        else{//Respuesta a un mensaje anterior
            if(emisorMensaje.startsWith("Router")){//Llegada del paso 9A o 10B, iniciar 10A o 11B                                                
                Mensaje objMsjeRecibido = new Mensaje(datos,"9A");                
                log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);
                
                //desencriptar particion y verificar
                                                
                for(SesionProtocolo sesion : meshRouters.get(indiceRouter).getSesionesActivas()){
                    
                    //Paso 10A - Se tiene una sesion activa con el solicitante del mensaje recibido
                    if(sesion.identificadorContrario.equals(objMsjeRecibido.identificadorSolicitante))
                        paso10A(objMsjeRecibido, emisorMensaje, routerColaborador, indiceRouter, sesion, bd, des);
                                                                                 
                    else{//Paso 11B - Recibido de router con sesion activa, pero sin ser verificador del mensaje recibido
                        paso11B(objMsjeRecibido, emisorMensaje, routerColaborador, indiceRouter, sesion, bd, des);
                    }
                }
            }
            else{//si mensaje viene de un MC
                paso9A10B(datos, emisorMensaje, routerColaborador, indiceRouter, bd, des);
            }
        }
    }
    
    public void paso2A(String datos[], String idRouter, MR routerActual, String idSolicitante, PublicKey pKSolicitante, SesionProtocolo sesion)throws RemoteException{
                                
        //No hay nada que desencriptar - Construir mensaje recibido
        Mensaje objMsjeRecibido = new Mensaje(datos, "1");//Construir mensaje del paso 1 en objeto
        

        log.writeInfo(objMsjeRecibido.identificadorReceptor, objMsjeRecibido.identificadorEmisor, "Inicia la ejecución como verificador ante:");
        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);
        
        /*verificacion de todo el mensaje
        objMsjeRecibido.setValorHash();

        String hashRecibido = objMsjeRecibido.firmaHash;
        String hashCalculado = objMsjeRecibido.valorHash+"";

        try {
            hashRecibido = meshRouters.get(indiceRouter).getCbi().verificarFirmaRsa(hashRecibido, publicKeySolicitante);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(hashCalculado.equals(hashRecibido)){
            System.out.println("TOdo OK");
        }
        else
            System.out.println("Error");
        */
        
        //generar respuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = idRouter;
        objMsjeRespuesta.identificadorReceptor = idSolicitante;
        objMsjeRespuesta.nonceFresh = obtenerNuevoNonce();
        objMsjeRespuesta.nonceFreshRespuesta = (objMsjeRespuesta.nonceFresh + objMsjeRecibido.nonceFresh);
        objMsjeRespuesta.nonceLive = obtenerNuevoNonce();
        objMsjeRespuesta.setValorHash(routerActual.getCbi());

        //actualizar sesion
        sesion.setNonceFreshness(objMsjeRespuesta.nonceFreshRespuesta);
        sesion.setNonceLivenessContrario(-1);
        sesion.setNonceLivenessPropio(objMsjeRespuesta.nonceLive);

        //Enviar mensaje
        String msjeEnviar = "";
        try {
            msjeEnviar = objMsjeRespuesta.crearStrPaso2(routerActual.getCbi(),pKSolicitante);
        } catch (Exception ex) {
            Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        enviarMensajeMC(msjeEnviar, idRouter, idSolicitante);
    }
    public void paso4A(String datos[], String idRouter, MR routerActual, String idSolicitante, PublicKey pKSolicitante, SesionProtocolo sesion)throws RemoteException{
                                
        //desencriptar recibido        
        String msjeAux = "";
        if(!(datos.length < 4)){

            try {
                msjeAux = routerActual.getCbi().desencriptar(datos[3]);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
            }
            datos[3] = msjeAux;
        }
        
        //Preparar mensaje recibido        
        Mensaje objMsjeRecibido = new Mensaje(datos, "3");
        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);

        sesion.setNonceFreshness(objMsjeRecibido.nonceFreshRespuesta);
        sesion.setNonceLivenessContrario(objMsjeRecibido.nonceLive);                

        //verificacion

        //generar respuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = idRouter;
        objMsjeRespuesta.identificadorReceptor = idSolicitante;
        objMsjeRespuesta.nonceFreshRespuesta = (objMsjeRecibido.nonceFreshRespuesta + 1);
        objMsjeRespuesta.nonceLive = obtenerNuevoNonce();
        objMsjeRespuesta.nonceLiveRespuesta = (objMsjeRecibido.nonceLive - 1);
        objMsjeRespuesta.aleatorioVerificador = obtenerAleatorioGrande();
        objMsjeRespuesta.setValorHash(routerActual.getCbi());

        //almacenar datos a utilizar mas adelante                
        sesion.setAleatorioPropio(objMsjeRespuesta.aleatorioVerificador);
        sesion.setNonceLivenessPropio(objMsjeRespuesta.nonceLive);

        //Enviar mensaje
        String msjeEnviar = "";
        {
            try {
                msjeEnviar = objMsjeRespuesta.crearStrPaso4(routerActual.getCbi(),pKSolicitante);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        enviarMensajeMC(msjeEnviar, idRouter, idSolicitante);
    }    
    public void paso6A(String datos[], String idRouter, MR routerActual, int indiceRouter, String idSolicitante, SesionProtocolo sesion) throws RemoteException{
                
        //desencriptar mensaje recibido
        String msjeAux = "";
        if(!(datos.length < 4)){

            try {
                msjeAux = routerActual.getCbi().desencriptar(datos[3]);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
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
        objMsjeRespuesta.identificadorEmisor = idRouter;                
        objMsjeRespuesta.identificadorSolicitante = idSolicitante;
        objMsjeRespuesta.identificadorVerificador = idRouter;
        objMsjeRespuesta.timestamp = obtenerTimestamp();                
        objMsjeRespuesta.aleatorioVerificador = sesion.aleatorioPropio;
        objMsjeRespuesta.aleatorioSolicitante = objMsjeRecibido.aleatorioSolicitante;
        objMsjeRespuesta.valorZpVerificador = obtenerValorZp(indiceRouter, idSolicitante+idRouter);
        objMsjeRespuesta.setValorHash();

        sesion.setNonceLivenessPropio(objMsjeRespuesta.nonceLive);
        sesion.setAleatorioContrario(objMsjeRecibido.aleatorioSolicitante);                
        sesion.setAleatorioZpContrario(objMsjeRecibido.valorZpSolicitante);

        String nombreSesion = objMsjeRespuesta.identificadorVerificador+objMsjeRespuesta.identificadorSolicitante;

        //Enviar mensajes 
        String msjeEnviar = "";
        for(MR mr:meshRouters){ //a los otros mesh routers

            if(!mr.getIdentificador().equals(idRouter)){

                SesionProtocolo sesionRouter = new SesionProtocolo(this, mr.getIdentificador(), nombreSesion, "MR", false);

                meshRouters.get(indiceRouter).addSesionActiva(sesionRouter);

                BaseDatos bd = new BaseDatos(idRouter);
                DES des = null;
                String llaveSesion = bd.obtenerLlaveSesion(mr.getIdentificador());
                try {
                    des = new DES(llaveSesion);                            
                } catch (Exception ex) {
                    Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
                }
                objMsjeRespuesta.identificadorReceptor = mr.getIdentificador();
                
                try {
                    msjeEnviar = objMsjeRespuesta.crearStrPaso6A7B(des);
                } catch (Exception ex) {
                    Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
                }
                //PanelRouters.getInstanciaPanelRouters().agregarMensaje(mensajeEnviar,indiceRouter);
                //Broadcast a mesh router
                log.writePaso(objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor, msjeEnviar);
                recibirMensaje(msjeEnviar, idRouter, objMsjeRespuesta.identificadorReceptor);
            }                    
        }

        //enviarse un mensaje a si mismo para luego enviar la solicitud a sus MCs                
        objMsjeRespuesta.identificadorReceptor = idRouter;
        {            
            try {
                msjeEnviar = objMsjeRespuesta.crearStrPaso6A7B(null);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        recibirMensaje(msjeEnviar, idRouter, idRouter);
    }              
    public void paso7A8B(String datos[], String emisorMensaje, String idRouter, int indiceRouter, BaseDatos bd, DES des) throws RemoteException{
                
        Mensaje objMsjeRecibido = new Mensaje(datos,"6A");

        if(!idRouter.equals(emisorMensaje))
            log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);

        String nombreSesion = objMsjeRecibido.identificadorVerificador+objMsjeRecibido.identificadorSolicitante;
        SesionProtocolo sesion = new SesionProtocolo(this, emisorMensaje, nombreSesion, "MR", false);
        meshRouters.get(indiceRouter).addSesionActiva(sesion);

        //verificacion                

        //generarRespuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = idRouter;
        objMsjeRespuesta.identificadorVerificador = objMsjeRecibido.identificadorVerificador;
        objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorSolicitante;                
        objMsjeRespuesta.aleatorioVerificador = objMsjeRecibido.aleatorioVerificador;
        objMsjeRespuesta.aleatorioSolicitante = objMsjeRecibido.aleatorioSolicitante;
        objMsjeRespuesta.valorZpVerificador = objMsjeRecibido.valorZpVerificador;
        objMsjeRespuesta.numeroSaltos = Conf.NS;
        objMsjeRespuesta.setValorHash();

        //Enviar respuestas a los mesh clientes autenticados
        String msjeEnviar = "";
        int indice = 0;
        for (String nombreMCAutenticado : meshRouters.get(indiceRouter).getNombresMCsAutenticados()) {
            //Por cada nodo que esta autenticado al MR

            //Añadir nueva sesion
            SesionProtocolo nuevaSesion = new SesionProtocolo(meshRouters.get(indiceRouter).getInterfacesMCsAutenticados().get(indice), nombreMCAutenticado, nombreSesion, "MC", false);
            meshRouters.get(indiceRouter).addSesionActiva(nuevaSesion);

            //Añadir identificador del receptor y nonce que faltaba
            objMsjeRespuesta.identificadorReceptor = nombreMCAutenticado;
            objMsjeRespuesta.nonceFreshRespuesta = (bd.obtenerNonce(nombreMCAutenticado, false))+1;

            String llaveSesion = bd.obtenerLlaveSesion(nombreMCAutenticado);
            try {
                des = new DES(llaveSesion);
                msjeEnviar = objMsjeRespuesta.crearStrPaso7A8B(des);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
            }
            enviarPkVerificador(objMsjeRespuesta.identificadorVerificador, idRouter, nombreMCAutenticado);
            enviarMensajeMC(msjeEnviar, idRouter, nombreMCAutenticado);
            indice++;
        }
    }    
    public void paso7B(String datos[], String idRouter, String emisorMensaje, int indiceRouter, BaseDatos bd, DES des) throws RemoteException{
        
        Mensaje objMsjeRecibido = new Mensaje(datos,"6B");

        if(!idRouter.equals(emisorMensaje))
            log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);

        String nombreSesion = objMsjeRecibido.identificadorVerificador+objMsjeRecibido.identificadorSolicitante;
        SesionProtocolo sesion = new SesionProtocolo(this, emisorMensaje, nombreSesion, "MC", false);
        meshRouters.get(indiceRouter).addSesionActiva(sesion);
        
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = idRouter;
        objMsjeRespuesta.identificadorVerificador = objMsjeRecibido.identificadorVerificador;
        objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorSolicitante;                
        objMsjeRespuesta.aleatorioVerificador = objMsjeRecibido.aleatorioVerificador;
        objMsjeRespuesta.aleatorioSolicitante = objMsjeRecibido.aleatorioSolicitante;
        objMsjeRespuesta.valorZpVerificador = objMsjeRecibido.valorZpVerificador;               
        objMsjeRespuesta.setValorHash();
        
        //Enviar a cada router
        
        String msjeEnviar = "";
        for(MR mr: meshRouters){
            if(!mr.getIdentificador().equals(idRouter)){

                SesionProtocolo sesionRouter = new SesionProtocolo(this, mr.getIdentificador(), nombreSesion, "MR", false);

                meshRouters.get(indiceRouter).addSesionActiva(sesionRouter);

                String llaveSesion = bd.obtenerLlaveSesion(mr.getIdentificador());
                try {
                    des = new DES(llaveSesion);
                } catch (Exception ex) {
                    Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
                }

                objMsjeRespuesta.identificadorReceptor = mr.getIdentificador();
                try {
                    msjeEnviar = objMsjeRespuesta.crearStrPaso6A7B(des);
                } catch (Exception ex) {
                    Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
                }

                log.writePaso(objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor, msjeEnviar);
                recibirMensaje(msjeEnviar, idRouter, objMsjeRespuesta.identificadorReceptor);
            }
        }
        //enviarse un mensaje a si mismo para luego enviar la solicitud a sus MCs                
        objMsjeRespuesta.identificadorReceptor = idRouter;
        {            
            try {
                msjeEnviar = objMsjeRespuesta.crearStrPaso6A7B(null);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        recibirMensaje(msjeEnviar, idRouter, idRouter);
    }    
    public void paso9A10B(String datos[], String emisorMensaje, String idRouter, int indiceRouter, BaseDatos bd, DES des) throws RemoteException{
        
        //Llegada el paso 8A, iniciar el 9A
        Mensaje objMsjeRecibido = new Mensaje(datos,"8A");

        log.writePaso(objMsjeRecibido.identificadorEmisor, objMsjeRecibido.identificadorReceptor, datos);
        //verificacion

        //Eliminar sesion debido a que llego respuesta

        //Generar respuesta (Paso 9)
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = idRouter;
        objMsjeRespuesta.identificadorReceptor = objMsjeRecibido.identificadorVerificador;
        objMsjeRespuesta.identificadorVerificador = objMsjeRecibido.identificadorVerificador;
        objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorSolicitante;
        objMsjeRespuesta.timestamp = obtenerTimestamp();
        objMsjeRespuesta.msjeEncriptadoPart = objMsjeRecibido.msjeEncriptadoPart;
        objMsjeRespuesta.setValorHash();
        
        //enviar mensaje
        
        String msjeEnviar = "";
        
        if(!idRouter.equals(objMsjeRespuesta.identificadorReceptor)){
            String llaveSesion = bd.obtenerLlaveSesion(objMsjeRespuesta.identificadorReceptor);
            
            try {
                des = new DES(llaveSesion);
                msjeEnviar = objMsjeRespuesta.crearStrPaso9A10B(des);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
            }
            log.writePaso(objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor, msjeEnviar);
        }
        else{
            try {
                msjeEnviar = objMsjeRespuesta.crearStrPaso9A10B(null);
            } catch (Exception ex) {
                Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Enviarselo a otro router (verificador) Escenario A
        recibirMensaje(msjeEnviar, objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor);
    }    
    public void paso10A(Mensaje objMsjeRecibido, String emisorMensaje, String idRouter, int indiceRouter, SesionProtocolo sesion, BaseDatos bd, DES des) throws RemoteException{
                
        String cadenaParticionesDes = "";                        
        try {
            cadenaParticionesDes =  meshRouters.get(indiceRouter).getCbi().desencriptar(objMsjeRecibido.msjeEncriptadoPart);                            
        } catch (Exception ex) {
            Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
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

        if(sesion.partM1Recibidas.size() == meshRouters.size()){                            

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
                    objMsjeRespuesta.identificadorEmisor = idRouter;
                    objMsjeRespuesta.identificadorReceptor = objMsjeRecibido.identificadorSolicitante;
                    objMsjeRespuesta.nonceFreshRespuesta = sesion.nonceFreshness;
                    objMsjeRespuesta.nonceLiveRespuesta = sesion.nonceLivenessContrario;
                    objMsjeRespuesta.valorZpVerificador = sesion.aleatorioZpPropio * Conf.PUNTO_P;
                    objMsjeRespuesta.prueba = obtenerPruebaVerificador(meshRouters.get(indiceRouter).getCbi(), mensaje2 );
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
                        msjeEnviar = objMsjeRespuesta.crearStrPaso10A12B(meshRouters.get(indiceRouter).getCbi(), keyPublicaReceptor);
                    } catch (Exception ex) {
                        Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    PanelRouters.getInstanciaPanelRouters().agregarNotificacion("Router"+(indiceRouter+1)+" finaliza la autenticación con "+ objMsjeRespuesta.identificadorReceptor +" satisfactoriamente");
                    PanelRouters.getInstanciaPanelRouters().agregarAutenticado(indiceRouter+1, objMsjeRespuesta.identificadorReceptor);
                    
                    enviarMensajeMC(msjeEnviar, objMsjeRespuesta.identificadorEmisor, objMsjeRespuesta.identificadorReceptor);
                }
            } 
        }                        
    }            
    public void paso11B(Mensaje objMsjeRecibido, String emisorMensaje, String idRouter, int indiceRouter, SesionProtocolo sesion, BaseDatos bd, DES des) throws RemoteException{
        
        //verificacion
        
        //generarRespuesta
        Mensaje objMsjeRespuesta = new Mensaje();
        objMsjeRespuesta.identificadorProtocolo = Conf.ID_PROTOCOLO;
        objMsjeRespuesta.identificadorEmisor = idRouter;
        objMsjeRespuesta.identificadorVerificador = objMsjeRecibido.identificadorVerificador;
        objMsjeRespuesta.identificadorSolicitante = objMsjeRecibido.identificadorSolicitante;                
        objMsjeRespuesta.msjeEncriptadoPart = objMsjeRecibido.msjeEncriptadoPart;        
        objMsjeRespuesta.setValorHash();
        
        //enviar mensaje
        String msjeEnviar = "";
        String idNodoDestino = "";
        String nombreSesion = objMsjeRespuesta.identificadorVerificador+objMsjeRespuesta.identificadorSolicitante;
        
        for(SesionProtocolo sesionActiva: meshRouters.get(indiceRouter).getSesionesActivas() ){
            if(sesionActiva.nombreSesion.equals(nombreSesion)){
                idNodoDestino = sesionActiva.identificadorContrario;
                break;
            }
        }
        if(idNodoDestino.equals(""))
            System.err.println("Error, sesion no encontrada");
        else{
            objMsjeRespuesta.identificadorReceptor = idNodoDestino;
            objMsjeRespuesta.nonceFresh = objMsjeRespuesta.nonceFreshRespuesta = (bd.obtenerNonce(idNodoDestino, true))+1;
        }
        
        String llaveSesion = bd.obtenerLlaveSesion(idNodoDestino);
        try {
            des = new DES(llaveSesion);
            msjeEnviar = objMsjeRespuesta.crearStrPaso8A9B(des);//mensaje a enviar iguales en este paso
        } catch (Exception ex) {
            Logger.getLogger(InterfazMRImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        enviarMensajeMC(msjeEnviar, idRouter, objMsjeRespuesta.identificadorReceptor);
    }
    
    public int obtenerNuevoNonce(){
        Random rnd = new Random();
        return (int)(rnd.nextDouble()*9999+1000);
    }                
    public int obtenerAleatorioGrande(){
        Random rnd = new Random();
        return (int)(rnd.nextDouble()*99999999+10000000);
    }   
    public String obtenerTimestamp(){
        
        Date date = Calendar.getInstance().getTime();
        DateFormat dateHourFormat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = dateHourFormat.format(date);                        
        return timestamp;
    }    
    public int obtenerValorZp(int indiceVerificador, String nombreSesion){
                                                
        int valor;
        Random rnd = new Random();
        valor = (int)(rnd.nextDouble()*Conf.P+3);
                        
        for(SesionProtocolo sesion : meshRouters.get(indiceVerificador).getSesionesActivas()){
            if(sesion.nombreSesion.equals(nombreSesion)){
                sesion.aleatorioZpPropio = valor;
                break;
            }
        }
        valor = valor * Conf.PUNTO_P;
        return valor;
    }
    public String obtenerPruebaVerificador(CBI cbi, String mensajeFirmadoSecreto){
        
        String prueba = cbi.firmar(mensajeFirmadoSecreto);
        
        return prueba;
    }
}
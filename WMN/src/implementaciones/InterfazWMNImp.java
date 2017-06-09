/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementaciones;

import interfaces.*;
import java.lang.reflect.Array;
import vistas.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jose Ignacio
 */
public class InterfazWMNImp extends UnicastRemoteObject implements InterfazWMN{
    
    public ArrayList <ArrayList>datosMeshClientsNoAut = new ArrayList();    
    public ArrayList <ArrayList>datosMeshClientsAut = new ArrayList();
    
    public ArrayList interfazMeshRouters = new ArrayList();
    public int cantidadMRs;
        
    public int parametroP;
    public int parametroN;
    
    public AdminBD admin = new AdminBD();
    
    public PKG pkg;
    
    public InterfazWMNImp() throws RemoteException, Exception{
        super();        
        pkg = new PKG();        
    }
    @Override
    public synchronized int registrarMCNoAut(InterfazMC meshClient, String identificador, String password) throws RemoteException{
        
        int estado = -1;
        ArrayList list = new ArrayList();
        list.add(meshClient);
        list.add(identificador);
        list.add(password);
        
        int nroRouter = datosMeshClientsAut.size() + 1;
        
        if(cantidadMRs <= datosMeshClientsAut.size()){ //Si ya cada router tiene al menos un cliente
            

            InformacionWMN.getInstanciaInfoWMN().agregarMCNoAut();
            enviarInterfazRouters(meshClient);
            enviarInterfacesClientesAut(meshClient);    
            estado = 0;            
            datosMeshClientsNoAut.add(list);
        }
        else{
            datosMeshClientsAut.add(list);
            InformacionWMN.getInstanciaInfoWMN().agregarMCAut();
            String canal = identificador + " <---> Router"+nroRouter;
            InformacionWMN.getInstanciaInfoWMN().agregarCanalSeguro(canal);            
            autenticacionInstantanea(meshClient, identificador, password, nroRouter);
            estado = 1;            
        }
        
        enviarInfoGeneralMC(meshClient, admin.getParametroN(), admin.getParametroP());
        try {
            enviarClavesMCs(meshClient, identificador);
        } catch (Exception ex) {
            Logger.getLogger(InterfazWMNImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return estado;
    }
    
    @Override
    public synchronized void autenticacionInstantanea(InterfazMC meshClient, String id, String password, int nroRouter) throws RemoteException{
        
        //Parametros de envio
        String nombreRouter = "Router"+nroRouter;
        InterfazMR meshRouter = (InterfazMR) interfazMeshRouters.get(0);
        String llaveSesion = nombreRouter+id;
        
        //Base de datos para el mesh client autenticado
        ArrayList<String> bd = admin.entregarDatos(nroRouter-1,cantidadMRs);//menos 1 o no?
                
        meshClient.recibirRouterVerificador(meshRouter, nombreRouter,llaveSesion);
        meshClient.recibirDatos(bd);
        meshRouter.recibirClienteAutenticado(meshClient, id, llaveSesion, nombreRouter);                
    }
    /*@Override
    public synchronized boolean registrarMCAut(InterfazMC meshClient, String identificador, String password) throws RemoteException{
        
        ArrayList list = new ArrayList();
        list.add(meshClient);
        list.add(identificador);
        list.add(password);
        datosMeshClientsAut.add(list);
        
        broadcastInterfazClienteAut(meshClient, identificador);
        
        return true;
    }*/
    @Override
    public synchronized boolean registrarMR(InterfazMR meshRouters, int cantidad) throws RemoteException{
                
        interfazMeshRouters.add(meshRouters);
        cantidadMRs = cantidad;
        admin.setParametroN(cantidadMRs);
        InformacionWMN.getInstanciaInfoWMN().agregarMRs(cantidad);
        String canal;
        for(int i = 1; i <= cantidadMRs; i++){
            for(int j = i+1; j <= cantidadMRs; j++){                
                canal = "Router"+i+" <---> Router"+j;      
                InformacionWMN.getInstanciaInfoWMN().agregarCanalSeguro(canal);
            }
        }
        
        enviarInfoGeneralMR(meshRouters, admin.getParametroN(), admin.getParametroP());
        
        try {
            enviarClavesMRs();
        } catch (Exception ex) {
            Logger.getLogger(InterfazWMNImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    public synchronized ArrayList enviarClavesMCs(InterfazMC meshClient, String idCliente) throws Exception{
        
        ArrayList llaves = new ArrayList();
        
        Object parllaves = pkg.generarLlavesCliente();
        llaves.add(parllaves);
        Object sK = pkg.generarSk(idCliente);
        llaves.add(sK);
        meshClient.recibirLlavesPropias(parllaves/*, sK, pkg.getKeyPair().getPublic()*/);
        
        return llaves;
    }
    
    public synchronized ArrayList enviarClavesMRs() throws Exception{
        
        ArrayList llaves = new ArrayList();
        ArrayList <ArrayList> llavesRouters = new ArrayList();
        String idRouter;
        
        InterfazMR interfazMr = (InterfazMR)interfazMeshRouters.get(0);
        
        for(int i = 1; i <= cantidadMRs; i++ ){
        
            idRouter = "Router"+i;
            Object parllaves = pkg.generarLlavesCliente();
            llaves.add(parllaves);
            Object sK = pkg.generarSk(idRouter);
            llaves.add(sK);            
            
            interfazMr.recibirLlavesRouters(parllaves, /*sK, pkg.getKeyPair().getPublic(),*/ idRouter );
        }
        return llaves;
    }
    
    public synchronized boolean enviarInterfazRouters(InterfazMC meshClient) throws RemoteException{
                
        InterfazMR interfazMRs = (InterfazMR)interfazMeshRouters.get(0);
        meshClient.recibirInterfazMRs(interfazMRs, cantidadMRs);
        
        return true;
    }
    @Override
    public synchronized boolean enviarInterfacesClientesAut(InterfazMC interfazMCDestinatario) throws RemoteException{
               
        for(int i=0; i < datosMeshClientsAut.size(); i++){
            InterfazMC interfazMC = (InterfazMC)datosMeshClientsAut.get(i).get(0);
            String identificador = (String)datosMeshClientsAut.get(i).get(1);
            interfazMCDestinatario.recibirInterfazMC(interfazMC,identificador);
        }
        return true;
    }
    @Override
    public synchronized boolean broadcastInterfazClienteAut(InterfazMC interfazEnviar, String identificador) throws RemoteException{
        
        for(int i=0; i < datosMeshClientsNoAut.size(); i++){
            InterfazMC interfazDestinatario = (InterfazMC)datosMeshClientsNoAut.get(i).get(0);
            interfazDestinatario.recibirInterfazMC(interfazEnviar, identificador);            
        }        
        return true;
    }       
    
    public synchronized void enviarInfoGeneralMC(InterfazMC interfaz, int n, int p ) throws RemoteException{
        
        int parametroN = this.cantidadMRs;
        int parametroP = n - 2;
        
        interfaz.recibirInfoGeneral(parametroN, parametroP);        
    }
    public synchronized void enviarInfoGeneralMR(InterfazMR interfaz, int n, int p ) throws RemoteException{
        
        int parametroN = this.cantidadMRs;
        int parametroP = n - 2;
        
        interfaz.recibirInfoGeneral(parametroN, parametroP);
    }
    
    @Override
    public synchronized void nuevoAutenticado(String solicitante, String verificador) throws RemoteException{
        
        InformacionWMN.getInstanciaInfoWMN().agregarMCAut();
        InformacionWMN.getInstanciaInfoWMN().quitarMCNoAut();
        
        int i = 0;
        
        for(ArrayList listaDatos : datosMeshClientsNoAut){
            if(listaDatos.get(1).equals(solicitante)){
                datosMeshClientsAut.add(listaDatos);
                //datosMeshClientsNoAut.remove(listaDatos);
                break;
            }
            i++;
        }
        datosMeshClientsNoAut.remove(i);

        String canal = solicitante + " <---> " + verificador;
        InformacionWMN.getInstanciaInfoWMN().agregarCanalSeguro(canal);        
    }
}

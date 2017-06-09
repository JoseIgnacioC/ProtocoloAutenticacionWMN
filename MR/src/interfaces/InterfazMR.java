/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import protocolo.Mensaje;
import protocolo.SesionProtocolo;
/**
 *
 * @author Jose Ignacio
 */
public interface InterfazMR extends Remote {
 
    public void recibirMensaje(String mensaje, String emisor, String receptor) throws RemoteException;    
    public void recibirInterfaz(InterfazMC interfaz, String emisor, String routerDestinatario) throws RemoteException;
    
    public void recibirPkContrario(Object publicKey, String id) throws RemoteException;
    public void recibirInfoGeneral(int n, int p) throws RemoteException;
    
    public void enviarMensajeMC(String mensaje, String emisor, String receptor) throws RemoteException;
    
    public void sigPasoVerificador(String routerVerificador, String solicitante, String mensaje, SesionProtocolo sesion) throws RemoteException;
    public void sigPasoColaborador(String routerColaborador, String emisorMensaje, String mensaje, boolean isNuevo) throws RemoteException;
    
    public void recibirClienteAutenticado(InterfazMC meshCliente, String idCliente, String llaveSesion, String nombreRouter) throws RemoteException;
    
    public void recibirLlavesRouters(Object parLlaves, /*Object sK, Object PMK,*/ String idRouter ) throws RemoteException;
    
    public void enviarPkVerificador(String idVerificador, String emisor, String receptor) throws RemoteException;
}

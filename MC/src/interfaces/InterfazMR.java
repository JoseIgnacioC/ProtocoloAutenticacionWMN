/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import protocolo.Mensaje;
/**
 *
 * @author Jose Ignacio
 */
public interface InterfazMR extends Remote{
    
    public void recibirMensaje(String mensaje, String emisor, String receptor) throws RemoteException;
    public void recibirInterfaz(InterfazMC interfaz, String emisor, String routerDestinatario) throws RemoteException;
    
    public void recibirPkContrario(Object publicKey, String id) throws RemoteException;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Jose Ignacio
 */
public interface InterfazMR extends Remote{
    
    public void recibirClienteAutenticado(InterfazMC meshCliente, String idCliente, String llaveSesion, String nombreRouter) throws RemoteException;
    public void recibirLlavesRouters(Object parLlaves, /*Object sK, Object PMK,*/ String idRouter ) throws RemoteException;
    public void recibirInfoGeneral(int n, int p);
}

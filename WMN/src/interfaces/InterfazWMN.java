/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Jose Ignacio
 */
public interface InterfazWMN extends Remote {
    
    public int registrarMCNoAut(InterfazMC meshClient, String nombre, String password) throws RemoteException;
    
    //public boolean registrarMCAut(InterfazMC meshClient, String identificador, String password) throws RemoteException;
    public boolean registrarMR(InterfazMR meshRouters, int cantidad) throws RemoteException;
    public boolean enviarInterfacesClientesAut(InterfazMC interfazMCDestinatario) throws RemoteException;
    public boolean broadcastInterfazClienteAut(InterfazMC interfazEnviar, String identificador) throws RemoteException;
    
    public void autenticacionInstantanea(InterfazMC meshClient, String id, String password, int nroRouter) throws RemoteException;

    public void nuevoAutenticado(String solicitante, String verificador)throws RemoteException;
}

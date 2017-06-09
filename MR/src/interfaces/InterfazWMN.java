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
public interface InterfazWMN extends Remote{
    
public boolean registrarMR(InterfazMR meshRouters, int cantidad) throws RemoteException;
//public ArrayList enviarInterfazRouters(InterfazMC meshClient);
public boolean enviarInterfacesClientesAut(InterfazMC interfazMCDestinatario) throws RemoteException;
    
}

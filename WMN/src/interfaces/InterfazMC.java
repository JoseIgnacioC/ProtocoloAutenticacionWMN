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
public interface InterfazMC extends Remote{
    
    public void recibirInterfazMRs(InterfazMR interfazMRs, int cantidad) throws RemoteException;    
    public void recibirInterfazMC(InterfazMC interfazMC, String identificador) throws RemoteException;
    
    public void recibirRouterVerificador(InterfazMR interfazMR, String nombreRouter, String llaveSesion ) throws RemoteException;
    
    public void recibirDatos(ArrayList<String> datos) throws RemoteException;
    
    public void recibirLlavesPropias(Object parLlaves/*, Object sK, Object PMK*/) throws RemoteException;
    
    public void recibirInfoGeneral(int n, int p) throws RemoteException;
}

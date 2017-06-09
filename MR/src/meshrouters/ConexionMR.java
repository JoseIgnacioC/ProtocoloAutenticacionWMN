/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meshrouters;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import interfaces.*;
import implementaciones.*;

/**
 *
 * @author Jose Ignacio
 */
public class ConexionMR {
    private static Registry registry;    
    private static InterfazMR meshRouters;    
    private static InterfazWMN wmn;
    
    /**
     * Función para establecer comunicación con los demas RMI
     * @return true si se establece comunicación o false si hubo algun problema
     * @throws RemoteException 
     */
    public boolean comunicarWMNRMI() throws RemoteException{
        try{
            java.security.AllPermission a = new java.security.AllPermission();
            System.setProperty("java.security.policy", "rmi.policy");
            startRegistry("127.0.0.1",1099);
            wmn = (InterfazWMN)registry.lookup("Implementacion");
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Funcion para crear RMI Registry para establecer la comunicación
     * @param host Dirección IP de la comunicación
     * @param Puerto Puerto de la comunicación
     * @throws RemoteException 
     */
    private static void startRegistry(String host, int Puerto) throws RemoteException{
        try{
            registry = LocateRegistry.getRegistry(host, Puerto);
            registry.list();            
        }
        catch(RemoteException e){
            e.printStackTrace();
        }
    }
    public Registry getRegistry() {
        return registry;
    }

    public InterfazMR getMeshRouters() {
        return meshRouters;
    }
    public static InterfazWMN getWmn() {
        return wmn;
    }        
    
    public boolean solicitarRegistroMR_WMN (int cantidadRouters) throws RemoteException, Exception{
        meshRouters = new InterfazMRImp(cantidadRouters);        
        boolean aceptado = wmn.registrarMR(meshRouters, cantidadRouters);
        return aceptado;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmn;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jose Ignacio
 */
public class ConexionWMN {
    private static Registry registry;    

    public Registry getRegistry() throws RemoteException{        
        startRegistry(1099);
        return registry;
    }
    private static void startRegistry(int Puerto) throws RemoteException{
        try{
            registry = LocateRegistry.getRegistry(Puerto);
            registry.list();
        }
        catch(RemoteException e){
            registry = LocateRegistry.createRegistry(Puerto);
            registry.list();
        }
    }  
        public boolean detener() throws RemoteException{
        try {            
            registry.unbind("Implementacion");
        } catch (NotBoundException ex) {
            Logger.getLogger(ConexionWMN.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (AccessException ex) {
            Logger.getLogger(ConexionWMN.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
}

package meshclient;
import implementaciones.*;
import interfaces.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Clase que representa la conexión RMI que tiene el cliente con el servidor
 * @author Jose Ignacio
 */
public class ConexionMC {
    private static Registry registry;    
    private static InterfazMC meshClient;
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
    public InterfazMC getMeshCliente() {
        return meshClient;
    }

    public static InterfazMC getMeshClient() {
        return meshClient;
    }

    public static InterfazWMN getWmn() {
        return wmn;
    }
    
    /**
     * Función para registrar al MC en WMN
     * @param nombre Nombre del cliente 
     * @param password Contraseña del cliente 
     *@param llavePrivada llave criptográfica secreta del cliente
     * @return true si la solicitud de registro fue aceptada, false si no
     * @throws RemoteException 
     */
    public int solicitarRegistroMC_WMN (String nombre, String password, String llavePrivada) throws RemoteException{
        meshClient = new InterfazMCImp(nombre,password,llavePrivada);        
        int aceptado = wmn.registrarMCNoAut(meshClient, nombre, password);
        return aceptado;
    }
    
    public void avisarAutenticación(String solicitante, String verificador) throws RemoteException{
        
        wmn.nuevoAutenticado(solicitante, verificador);
    }
}

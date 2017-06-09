package protocolo;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Jose Ignacio
 */
public class DES {
    private SecretKey key; //Clave usada en los pasos del cifrador DES
    private String llavePrivada; //Clave secreta o privada con el cual se encripta y desencripta
    
    //Constructores
    public DES() {        
    }
    
    public DES(String llavePrivada) throws Exception{
        this.llavePrivada = llavePrivada;
        generarClaveDes();
    }
    
    public DES( SecretKey key, String llavePrivada) {
        this.key = key;
        this.llavePrivada = llavePrivada;
    }
    public SecretKey getKey() {
        return key;
    }
    public String getLlavePrivada() {
        return llavePrivada;
    }    
    /**
     * Función para obtener la clave usada por DES
     * @throws Exception 
     */
    public void generarClaveDes() throws Exception {
        
        DESKeySpec desKeySpec = new DESKeySpec(llavePrivada.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        key = keyFactory.generateSecret(desKeySpec);
    }
    /**
     * Función para encriptar un mensaje haciendo uso del cifrador DES
     * @param textoPlano Texto que se desea encriptar
     * @return Texto encriptado
     * @throws Exception 
     */
    public String encriptar (String textoPlano) throws Exception {
        
        Cipher cifrador = Cipher.getInstance("DES");
        cifrador.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] bytesTextoPlano = textoPlano.getBytes("UTF8");
        byte[] textoEncriptado = cifrador.doFinal(bytesTextoPlano);

        BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encode(textoEncriptado);
        return base64;
    }
    /**
     * Función para desencriptar un mensaje haciendo uso del cifrador DES
     * @param textoEncriptado Texto encriptado que se desea desencriptar
     * @return Texto plano
     * @throws Exception 
     */
    public String desencriptar(String textoEncriptado) throws Exception{
        
        Cipher cifrador = Cipher.getInstance("DES");
        cifrador.init(Cipher.DECRYPT_MODE, key);
        
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] textoDecodificado = decoder.decodeBuffer(textoEncriptado);
        byte[] bytesTextoPlano = cifrador.doFinal(textoDecodificado);
        String textoPlano = new String(bytesTextoPlano, "UTF8");
        return textoPlano;
    }
}

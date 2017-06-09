/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protocolo;

import it.unisa.dia.gas.crypto.jpbc.signature.ps06.engines.PS06Signer;
import it.unisa.dia.gas.crypto.jpbc.signature.ps06.params.*;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.xml.bind.DatatypeConverter;

public class CBI {
    
    //private CipherParameters secretKey;
    
    private KeyPair keyRsa;
    private Cipher cipher;
    
    private String identificador;
    
    private CipherParameters PMK;
    
    //private String llavePrivada;
    //private String PMK;

    public CBI() {        
    }
    public CBI(String identificador)throws Exception{
        this.identificador = identificador;
        this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }
    
    public CBI(String identificador, /*CipherParameters secretKey,*/ KeyPair keys/*, CipherParameters pmk*/) throws Exception{
        this.identificador = identificador;
        //this.secretKey = secretKey;        
        this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        this.keyRsa = keys;
        //this.PMK = pmk;
                
    }

    /*public void setSecretKey(CipherParameters secretKey) {
        this.secretKey = secretKey;
    }*/

    public void setKeyRsa(KeyPair keyRsa) {
        this.keyRsa = keyRsa;
    }

    /*public void setPMK(CipherParameters PMK) {
        this.PMK = PMK;
    }*/
    
    /*public CipherParameters getSecretKey() {
        return secretKey;
    }*/

    public KeyPair getKeyRsa() {
        return keyRsa;
    }
    
    public String getIdentificador() {
        return identificador;
    }    
    
    public String encriptar(String mensaje, PublicKey llavePublica) throws Exception{ //Pasar luego a CBI LLavePublica = PMK + ID
        
        mensaje = mensaje.replaceAll(",", "12345678");
        mensaje = mensaje.replaceAll("-","87654321");
                        
        byte[] txtPlano = Base64.getDecoder().decode( mensaje);
        //Base64.getEncoder().encode(user.getEmail().getBytes(StandardCharsets.UTF_8)));
        
        cipher.init(Cipher.ENCRYPT_MODE, llavePublica);
        byte[] txtCifrado = cipher.doFinal(txtPlano);
        
        String strCifrado = Base64.getEncoder().withoutPadding().encodeToString(txtCifrado);
        
        return strCifrado;
    }
    public String desencriptar(String mensaje) throws Exception{
        
        
        byte[] txtCifrado = Base64.getDecoder().decode(mensaje);
        
        cipher.init(Cipher.DECRYPT_MODE, this.keyRsa.getPrivate());
        byte[] txtPlano = cipher.doFinal(txtCifrado);
        
        String strPlano = Base64.getEncoder().withoutPadding().encodeToString(txtPlano);
        strPlano = strPlano.replaceAll("12345678", ",");
        strPlano = strPlano.replaceAll("87654321","-");
        return strPlano;
    }    
                
    public String firmar(String message) {
        /*byte[] bytes = message.getBytes();

        PS06Signer signer = new PS06Signer(new SHA256Digest());

        signer.init(true, new PS06SignParameters((PS06SecretKeyParameters) secretKey));
        signer.update(bytes, 0, bytes.length);

        byte[] signature = null;
        try {
            signature = signer.generateSignature();

        } catch (CryptoException e) {
            System.err.println(e.getMessage());
        }

        String strSign = new String(signature);*/
        
        int aux = Math.abs(message.hashCode());
        String strSign = ""+aux;
        return strSign;
    }
    public boolean verificarFirma(String message, String identity, byte[] signature) {
        byte[] bytes = message.getBytes();

        PS06Signer signer = new PS06Signer(new SHA256Digest());
        signer.init(false, new PS06VerifyParameters((PS06PublicKeyParameters) PMK, identity));
        signer.update(bytes, 0, bytes.length);

        return signer.verifySignature(signature);
    }
    
    public String firmarRsa(String mensaje) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        mensaje = mensaje.replaceAll(",", "12345678");
        mensaje = mensaje.replaceAll("-","87654321");
                        
        byte[] txtPlano = Base64.getDecoder().decode( mensaje);
        //Base64.getEncoder().encode(user.getEmail().getBytes(StandardCharsets.UTF_8)));
        
        cipher.init(Cipher.ENCRYPT_MODE, this.keyRsa.getPrivate());
        byte[] txtFirmado = cipher.doFinal(txtPlano);
        
        String strFirmado = Base64.getEncoder().withoutPadding().encodeToString(txtFirmado);
        
        return strFirmado;
    }
    public String verificarFirmaRsa(String mensaje, PublicKey llavePublica) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        
        byte[] txtFirmado = Base64.getDecoder().decode(mensaje);
        
        cipher.init(Cipher.DECRYPT_MODE, llavePublica);
        byte[] txtPlano = cipher.doFinal(txtFirmado);
        
        String strPlano = Base64.getEncoder().withoutPadding().encodeToString(txtPlano);
        strPlano = strPlano.replaceAll("12345678", ",");
        strPlano = strPlano.replaceAll("87654321","-");
        return strPlano;                
    }
}

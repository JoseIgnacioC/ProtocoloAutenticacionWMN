/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementaciones;
import it.unisa.dia.gas.crypto.jpbc.signature.ps06.engines.PS06Signer;
import it.unisa.dia.gas.crypto.jpbc.signature.ps06.generators.PS06ParametersGenerator;
import it.unisa.dia.gas.crypto.jpbc.signature.ps06.generators.PS06SecretKeyGenerator;
import it.unisa.dia.gas.crypto.jpbc.signature.ps06.generators.PS06SetupGenerator;
import it.unisa.dia.gas.crypto.jpbc.signature.ps06.params.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
/**
 *
 * @author Jose Ignacio
 */
public class PKG {
    
    private AsymmetricCipherKeyPair keyPair;
    
    public PKG() throws Exception{
        this.keyPair = setup(createParameters(256, 256));        
    }

    public AsymmetricCipherKeyPair getKeyPair() {
        return keyPair;        
    }
    
    //Funciones CBI
    private PS06Parameters createParameters(int nU, int nM) throws Exception{
        // Generate Public PairingParameters
        return new PS06ParametersGenerator().init(
                PairingFactory.getPairingParameters("params/curves/a.properties"),
                nU, nM).generateParameters();
    }   
    private AsymmetricCipherKeyPair setup(PS06Parameters parameters) throws Exception{
        PS06SetupGenerator setup = new PS06SetupGenerator();
        setup.init(new PS06SetupGenerationParameters(null, parameters));

        return setup.generateKeyPair();
    }      
    
    public CipherParameters generarSk(String identity) throws Exception{
        PS06SecretKeyGenerator extract = new PS06SecretKeyGenerator();
        extract.init(new PS06SecretKeyGenerationParameters(keyPair, identity));

        return extract.generateKey();
    }
            
    //Funciones RSA
    public KeyPair generarLlavesCliente() throws Exception{
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keys = keyGen.generateKeyPair();
        
        return keys;
    }
}

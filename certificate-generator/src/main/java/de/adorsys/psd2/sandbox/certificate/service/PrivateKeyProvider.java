package de.adorsys.psd2.sandbox.certificate.service;

import de.adorsys.psd2.sandbox.certificate.exception.CertificateException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;

@Component
public class PrivateKeyProvider {

    /**
     * Load private key from classpath.
     *
     * @param filename Name of the key file. Suffix should be .key
     * @return PrivateKey
     */
    public PrivateKey getKeyFromClassPath(String filename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("certificates/" + filename);
        if (stream == null) {
            throw new CertificateException("Could not read private key from classpath:" + "certificates/" + filename);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            Security.addProvider(new BouncyCastleProvider());
            PEMParser pp = new PEMParser(br);
            PEMKeyPair pemKeyPair = (PEMKeyPair) pp.readObject();
            KeyPair kp = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
            pp.close();
            return kp.getPrivate();
        } catch (IOException ex) {
            throw new CertificateException("Could not read private key from classpath", ex);
        }
    }
}

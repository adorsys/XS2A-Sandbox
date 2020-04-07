package de.adorsys.psd2.sandbox.certificate.service;

import com.nimbusds.jose.util.X509CertUtils;
import de.adorsys.psd2.sandbox.certificate.exception.CertificateException;
import de.adorsys.psd2.sandbox.certificate.model.IssuerData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

@Slf4j
@Service
public class IssuerDataService {
    private static final String ISSUER_PRIVATE_KEY = "MyRootCA.key";
    private static final String ISSUER_CERTIFICATE = "MyRootCA.pem";
    private IssuerData issuerData;

    private final PrivateKeyProvider privateKeyProvider;

    public IssuerDataService(PrivateKeyProvider privateKeyProvider) {
        this.privateKeyProvider = privateKeyProvider;
        this.issuerData = generateIssuerData();
    }

    public IssuerData getIssuerData() {
        return issuerData;
    }

    public X509Certificate getCertificateFromClassPath() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream("certificates/" + ISSUER_CERTIFICATE);

        if (is == null) {
            throw new CertificateException("Could not find certificate in classpath");
        }

        try {
            byte[] bytes = IOUtils.toByteArray(is);
            return X509CertUtils.parse(bytes);
        } catch (IOException ex) {
            throw new CertificateException("Could not read certificate from classpath", ex);
        }
    }

    private IssuerData generateIssuerData() {
        IssuerData data = new IssuerData();
        X509Certificate cert = getCertificateFromClassPath();

        log.debug("Source for issuer data: {} from {}", cert, ISSUER_CERTIFICATE);

        try {
            data.setX500name(new JcaX509CertificateHolder(cert).getSubject());
        } catch (CertificateEncodingException ex) {
            throw new CertificateException("Could not read issuer data from certificate", ex);
        }

        data.setPrivateKey(privateKeyProvider.getKeyFromClassPath(ISSUER_PRIVATE_KEY));
        return data;
    }
}

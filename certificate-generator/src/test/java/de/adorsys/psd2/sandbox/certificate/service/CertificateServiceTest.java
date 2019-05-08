package de.adorsys.psd2.sandbox.certificate.service;

import de.adorsys.psd2.sandbox.certificate.domain.CertificateRequest;
import de.adorsys.psd2.sandbox.certificate.domain.CertificateResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
public class CertificateServiceTest {
    private final CertificateService certificateService = new CertificateService();

    @Test
    public void newCertificateCreatesCertAndKey() {
        CertificateRequest certificateRequest = CertificateRequest.builder()
                                                    .authorizationNumber("12345")
                                                    .countryName("Germany")
                                                    .organizationName("adorsys").build();
        CertificateResponse certificateResponse = certificateService.createCertificate(certificateRequest);
        assertNotNull(certificateResponse.getPrivateKey());
        assertNotNull(certificateResponse.getEncodedCert());
    }

    @Test
    public void exportPrivateKeyToStringResultsInSingleLinePrimaryKey() {
        PrivateKey key = certificateService.getKeyFromClassPath("MyRootCA.key");
        String result = CertificateService.exportToString(key).trim();
        assertTrue(result.startsWith("-----BEGIN RSA PRIVATE KEY-----"));
        assertTrue(result.endsWith("-----END RSA PRIVATE KEY-----"));
    }

    @Test
    public void exportCertificateToStringResultsInSingleLineCertificate() {
        X509Certificate cert = certificateService.getCertificateFromClassPath();
        String result = CertificateService.exportToString(cert).trim();
        log.info(result);
        assertTrue(result.startsWith("-----BEGIN CERTIFICATE-----"));
        assertTrue(result.endsWith("-----END CERTIFICATE-----"));
    }
}

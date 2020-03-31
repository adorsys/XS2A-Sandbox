package de.adorsys.psd2.sandbox.certificate.service;

import de.adorsys.psd2.sandbox.certificate.model.CertificateRequest;
import de.adorsys.psd2.sandbox.certificate.model.CertificateResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static de.adorsys.psd2.sandbox.certificate.service.ExportUtil.exportToString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
public class CertificateServiceTest {
    private final PrivateKeyProvider privateKeyProvider = new PrivateKeyProvider();
    private final IssuerDataService issuerDataService = new IssuerDataService(privateKeyProvider);
    private final CertificateService certificateService = new CertificateService(issuerDataService);

    @Test
    public void newCertificateCreatesCertAndKey() {
        CertificateRequest certificateRequest = CertificateRequest.builder()
                                                    .authorizationNumber("12345")
                                                    .countryName("Germany")
                                                    .organizationName("adorsys")
                                                    .commonName("XS2A Sandbox")
                                                    .build();
        CertificateResponse certificateResponse = certificateService.newCertificate(certificateRequest);
        assertNotNull(certificateResponse.getPrivateKey());
        assertNotNull(certificateResponse.getEncodedCert());
    }

    @Test
    public void exportPrivateKeyToStringResultsInSingleLinePrimaryKey() {
        PrivateKey key = privateKeyProvider.getKeyFromClassPath("MyRootCA.key");
        String result = exportToString(key).trim();
        assertTrue(result.startsWith("-----BEGIN RSA PRIVATE KEY-----"));
        assertTrue(result.endsWith("-----END RSA PRIVATE KEY-----"));
    }

    @Test
    public void exportCertificateToStringResultsInSingleLineCertificate() {
        X509Certificate cert = issuerDataService.getCertificateFromClassPath();
        String result = exportToString(cert).trim();
        log.info("Generated certificate {}", result);
        assertTrue(result.startsWith("-----BEGIN CERTIFICATE-----"));
        assertTrue(result.endsWith("-----END CERTIFICATE-----"));
    }

}

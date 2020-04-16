package de.adorsys.psd2.sandbox.certificate.service;

import de.adorsys.psd2.sandbox.certificate.model.CertificateRequest;
import de.adorsys.psd2.sandbox.certificate.model.CertificateResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static de.adorsys.psd2.sandbox.certificate.service.ExportUtil.exportToString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class CertificateServiceTest {
    private final PrivateKeyProvider privateKeyProvider = new PrivateKeyProvider();
    private final IssuerDataService issuerDataService = new IssuerDataService(privateKeyProvider);
    private final CertificateService certificateService = new CertificateService(issuerDataService);

    @Test
    void newCertificateCreatesCertAndKey() {
        // Given
        CertificateRequest certificateRequest = CertificateRequest.builder()
                                                    .authorizationNumber("12345")
                                                    .countryName("Germany")
                                                    .organizationName("adorsys")
                                                    .commonName("XS2A Sandbox")
                                                    .build();

        // When
        CertificateResponse certificateResponse = certificateService.newCertificate(certificateRequest);

        // Then
        assertNotNull(certificateResponse.getPrivateKey());
        assertNotNull(certificateResponse.getEncodedCert());
    }

    @Test
    void exportPrivateKeyToStringResultsInSingleLinePrimaryKey() {
        // Given
        PrivateKey key = privateKeyProvider.getKeyFromClassPath("MyRootCA.key");
        String result = exportToString(key).trim();

        // Then
        assertTrue(result.startsWith("-----BEGIN RSA PRIVATE KEY-----"));
        assertTrue(result.endsWith("-----END RSA PRIVATE KEY-----"));
    }

    @Test
    void exportCertificateToStringResultsInSingleLineCertificate() {
        // Given
        X509Certificate cert = issuerDataService.getCertificateFromClassPath();
        String result = exportToString(cert).trim();
        log.info("Generated certificate {}", result);

        // Then
        assertTrue(result.startsWith("-----BEGIN CERTIFICATE-----"));
        assertTrue(result.endsWith("-----END CERTIFICATE-----"));
    }

}

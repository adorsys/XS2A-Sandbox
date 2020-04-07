package de.adorsys.psd2.sandbox.certificate.controller;

import de.adorsys.psd2.sandbox.certificate.model.CertificateRequest;
import de.adorsys.psd2.sandbox.certificate.model.CertificateResponse;
import de.adorsys.psd2.sandbox.certificate.service.CertificateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/cert-generator")
@Api(value = "Certificate Controller")
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;

    @ApiOperation(value = "Create a new base64 encoded X509 certificate for authentication at the "
                              + "XS2A API with the corresponding private key and meta data", response = CertificateResponse.class)
    @PostMapping
    public ResponseEntity<CertificateResponse> createCert(@Valid @RequestBody CertificateRequest certData) {
        return ResponseEntity.status(HttpStatus.OK)
                   .body(certificateService.newCertificate(certData));
    }
}

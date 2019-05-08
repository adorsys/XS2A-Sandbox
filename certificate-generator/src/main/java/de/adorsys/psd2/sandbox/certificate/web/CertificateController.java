package de.adorsys.psd2.sandbox.certificate.web;

import de.adorsys.psd2.sandbox.certificate.domain.CertificateRequest;
import de.adorsys.psd2.sandbox.certificate.domain.CertificateResponse;
import de.adorsys.psd2.sandbox.certificate.service.CertificateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/certificate")
@Api(value = "Certificate controller")
public class CertificateController {
    private final CertificateService cerService;

    @ApiOperation(value = "Create a new base64 encoded X509 certificate for authentication at the "
                              + "XS2A API with the corresponding private key and meta data", response = CertificateResponse.class)
    @PostMapping
    public ResponseEntity<CertificateResponse> createCertificate(@Valid @RequestBody CertificateRequest request) {
        return ResponseEntity.ok(cerService.createCertificate(request));
    }
}

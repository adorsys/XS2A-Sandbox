package de.adorsys.psd2.sandbox.certificate.domain;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(description = "Certificate Response", value = "CertificateResponse")
public class CertificateResponse {
    private String encodedCert;
    private String privateKey;
}

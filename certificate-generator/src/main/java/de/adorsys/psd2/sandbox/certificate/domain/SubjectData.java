package de.adorsys.psd2.sandbox.certificate.domain;

import lombok.Value;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Value
public class SubjectData {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X500Name x500name;
    private Integer serialNumber;
    private Date startDate;
    private Date endDate;
}

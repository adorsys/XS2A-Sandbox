package de.adorsys.psd2.sandbox.certificate.domain;

import org.bouncycastle.asn1.DERUTF8String;

public class NcaName extends DERUTF8String {

    public NcaName(String s) {
        super(s);
    }
}

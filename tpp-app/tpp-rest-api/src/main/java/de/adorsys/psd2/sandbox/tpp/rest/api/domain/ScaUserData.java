package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import lombok.Data;

@Data
public class ScaUserData {
    private String id;
    private String methodValue;
    private ScaMethodType scaMethod;
}

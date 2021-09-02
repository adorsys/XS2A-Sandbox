package de.adorsys.psd2.sandbox.admin.rest.api.domain;

import lombok.Data;

@Data
public class ScaUserData {
    private String id;
    private String methodValue;
    private ScaMethodType scaMethod;
    private boolean usesStaticTan;
    private String staticTan;
    private boolean decoupled;
    private boolean isValid;
}

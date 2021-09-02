package de.adorsys.psd2.sandbox.admin.rest.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;

@SuppressWarnings({"PMD.UnusedPrivateField", "java:S1068"})
@JsonIgnoreProperties(allowGetters = true)
public abstract class ScaUserDataMixedIn {
    private String id;
    private ScaMethodTypeTO scaMethod;
    private String methodValue;
    private UserTO userTO;
    private boolean usesStaticTan;
    private String staticTan;
    private boolean decoupled;
}

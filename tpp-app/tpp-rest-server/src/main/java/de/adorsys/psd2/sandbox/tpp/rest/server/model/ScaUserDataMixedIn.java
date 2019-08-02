package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;

@SuppressWarnings("PMD.UnusedPrivateField")
@JsonIgnoreProperties(allowGetters = true)
public abstract class ScaUserDataMixedIn {
    private String id;
    private ScaMethodTypeTO scaMethod;
    private String methodValue;
    private UserTO userTO;
    private boolean usesStaticTan;
    private String staticTan;
}

package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaymentWorkflow {
    private final CmsPaymentResponse paymentResponse;

    private String paymentStatus;
    private String authCodeMessage;
    private PaymentAuthorizeResponse authResponse;
    private final ConsentReference consentReference;
    private SCAResponseTO scaResponse;

    public PaymentWorkflow(@NotNull CmsPaymentResponse paymentResponse, ConsentReference consentReference) {
        if (consentReference == null) {
            throw new IllegalStateException("Do not allow null input.");
        }
        this.paymentResponse = paymentResponse;
        this.consentReference = consentReference;
    }

    public CmsPaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAuthCodeMessage() {
        return authCodeMessage;
    }

    public void setAuthCodeMessage(String authCodeMessage) {
        this.authCodeMessage = authCodeMessage;
    }

    public PaymentAuthorizeResponse getAuthResponse() {
        return authResponse;
    }

    public void setAuthResponse(PaymentAuthorizeResponse authResponse) {
        this.authResponse = authResponse;
    }

    public ConsentReference getConsentReference() {
        return consentReference;
    }

    public String paymentId() {
        return paymentResponse.getPayment().getPaymentId();
    }

    public String authId() {
        return paymentResponse.getAuthorisationId();
    }

    public String encryptedConsentId() {
        return consentReference.getEncryptedConsentId();
    }

    public SCAResponseTO getScaResponse() {
        return scaResponse;
    }

    public void setScaResponse(SCAResponseTO scaResponse) {
        this.scaResponse = scaResponse;
    }

    public BearerTokenTO bearerToken() {
        return scaResponse == null
                   ? null
                   : scaResponse.getBearerToken();
    }

    public PaymentTypeTO paymentType() {
        return PaymentTypeTO.valueOf(paymentResponse.getPayment().getPaymentType().name());
    }

    public boolean singleScaMethod() {
        return scaResponse.getScaMethods() != null && scaResponse.getScaMethods().size() == 1;
    }

    public List<ScaUserDataTO> scaMethods() {
        return scaResponse.getScaMethods();
    }

    public ScaStatusTO scaStatus() {
        return scaResponse.getScaStatus();
    }

    public void processSCAResponse(SCAResponseTO paymentResponse) {
        scaResponse = paymentResponse;
        authResponse.setAuthorisationId(paymentResponse.getAuthorisationId());
        authResponse.setScaStatus(paymentResponse.getScaStatus());
        authResponse.setScaMethods(paymentResponse.getScaMethods());
        authCodeMessage = paymentResponse.getPsuMessage();
    }
}

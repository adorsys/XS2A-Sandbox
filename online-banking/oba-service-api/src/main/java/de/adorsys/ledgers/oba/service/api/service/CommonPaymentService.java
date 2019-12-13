package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;

public interface CommonPaymentService {
    PaymentWorkflow selectScaForPayment(String encryptedPaymentId, String authorisationId, String scaMethodId, String consentAndAccessTokenCookieString, boolean isCancellationOperation, String psuId, BearerTokenTO tokenTO);

    PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId, boolean strict, String consentCookieString, String psuId, BearerTokenTO bearerToken);

    void updateScaStatusPaymentStatusConsentData(String psuId, PaymentWorkflow workflow);

    void processPaymentResponse(PaymentWorkflow paymentWorkflow, SCAPaymentResponseTO paymentResponse);

    void updateAspspConsentData(PaymentWorkflow paymentWorkflow);

    String resolveRedirectUrl(String encryptedPaymentId, String authorisationId, String consentAndAccessTokenCookieString, boolean isOauth2Integrated, String psuId, BearerTokenTO tokenTO);
}

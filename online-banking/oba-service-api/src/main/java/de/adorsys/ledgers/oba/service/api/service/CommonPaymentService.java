package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;

public interface CommonPaymentService {
    PaymentWorkflow selectScaForPayment(String encryptedPaymentId, String authorisationId, String scaMethodId, String consentAndAccessTokenCookieString, boolean isCancellationOperation, String psuId, BearerTokenTO tokenTO);

    PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId, boolean strict, String consentCookieString, String psuId, BearerTokenTO bearerToken);

    void updateAspspConsentData(PaymentWorkflow paymentWorkflow);

    String resolveRedirectUrl(String encryptedPaymentId, String authorisationId, String consentAndAccessTokenCookieString, boolean isOauth2Integrated, String psuId, BearerTokenTO tokenTO);

    PaymentWorkflow initiatePayment(PaymentWorkflow paymentWorkflow, String psuId);

    PaymentWorkflow initiateCancelPayment(PaymentWorkflow paymentWorkflow, String psuId);

    PaymentWorkflow authorizePayment(PaymentWorkflow paymentWorkflow, String psuId, String authCode);

    PaymentWorkflow authorizeCancelPayment(PaymentWorkflow paymentWorkflow, String psuId, String authCode);
}

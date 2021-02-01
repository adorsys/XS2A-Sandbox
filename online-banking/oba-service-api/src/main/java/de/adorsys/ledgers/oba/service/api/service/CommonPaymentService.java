package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;

public interface CommonPaymentService {
    PaymentWorkflow selectScaForPayment(String encryptedPaymentId, String authorisationId, String scaMethodId, String consentAndAccessTokenCookieString, String psuId, BearerTokenTO tokenTO);

    PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId, boolean strict, String consentCookieString, BearerTokenTO bearerToken);

    void updateAspspConsentData(PaymentWorkflow paymentWorkflow);

    String resolveRedirectUrl(String encryptedPaymentId, String authorisationId, String consentAndAccessTokenCookieString, boolean isOauth2Integrated, String psuId, BearerTokenTO tokenTO, String authConfirmationCode);

    PaymentWorkflow initiatePaymentOpr(PaymentWorkflow paymentWorkflow, String psuId, OpTypeTO opType);

    PaymentWorkflow authorizePaymentOpr(PaymentWorkflow paymentWorkflow, String psuId, String authCode, OpTypeTO opType);
}

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.service.impl.mapper.PaymentConverter;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.EnumSet;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.*;
import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.NOT_FOUND;
import static de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode.CONSENT_DATA_UPDATE_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonPaymentServiceImpl implements CommonPaymentService {
    private final ConsentReferencePolicy referencePolicy;
    private final AuthRequestInterceptor authInterceptor;
    private final CmsPsuPisClient cmsPsuPisClient;
    private final PaymentRestClient paymentRestClient;
    private final AspspConsentDataClient aspspConsentDataClient;
    private final TokenStorageService tokenStorageService;
    private final PaymentConverter paymentConverter;
    private final OauthRestClient oauthRestClient;

    @Override
    public PaymentWorkflow selectScaForPayment(String encryptedPaymentId, String authorisationId, String scaMethodId, String consentAndAccessTokenCookieString, boolean isCancellationOperation, String psuId, BearerTokenTO tokenTO) {
        PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, tokenTO);
        selectMethodAndUpdateWorkflow(scaMethodId, workflow, isCancellationOperation);
        updateScaStatusPaymentStatusConsentData(psuId, workflow);
        return workflow;
    }

    @Override
    public void updateScaStatusPaymentStatusConsentData(String psuId, PaymentWorkflow workflow) {
        updateAuthorisationStatus(workflow, psuId);
        updatePaymentStatus(workflow);
        updateAspspConsentData(workflow);
    }

    @Override
    public PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId, boolean strict, String consentCookieString, String psuId, BearerTokenTO bearerToken) {
        ConsentReference consentReference = referencePolicy.fromRequest(encryptedPaymentId, authorizationId, consentCookieString, strict);
        CmsPaymentResponse cmsPaymentResponse = loadPaymentByRedirectId(psuId, consentReference);

        PaymentWorkflow workflow = new PaymentWorkflow(cmsPaymentResponse, consentReference);
        Object convertedPaymentTO = paymentConverter.convertPayment(workflow.paymentType(), cmsPaymentResponse);
        workflow.setAuthResponse(new PaymentAuthorizeResponse(workflow.paymentType(), convertedPaymentTO));
        workflow.getAuthResponse().setAuthorisationId(cmsPaymentResponse.getAuthorisationId());
        workflow.getAuthResponse().setEncryptedConsentId(encryptedPaymentId);
        workflow.setPaymentStatus(resolvePaymentStatus(cmsPaymentResponse.getPayment()));
        if (bearerToken != null) {
            SCAPaymentResponseTO scaPaymentResponseTO = new SCAPaymentResponseTO();
            scaPaymentResponseTO.setBearerToken(bearerToken);
            workflow.setScaResponse(scaPaymentResponseTO);
        }
        return workflow;
    }

    @Override
    public void processPaymentResponse(PaymentWorkflow paymentWorkflow, SCAPaymentResponseTO paymentResponse) {
        paymentWorkflow.processSCAResponse(paymentResponse);
        paymentWorkflow.setPaymentStatus(paymentResponse.getTransactionStatus().name());
    }

    private String resolvePaymentStatus(CmsPayment payment) {
        if (EnumSet.of(PaymentType.SINGLE, PaymentType.PERIODIC).contains(payment.getPaymentType())) {
            CmsSinglePayment singlePayment = (CmsSinglePayment) payment;
            return singlePayment.getPaymentStatus().name();
        } else {
            CmsBulkPayment bulkPayment = (CmsBulkPayment) payment;
            return bulkPayment.getPayments().get(0).getPaymentStatus().name();
        }
    }

    private void selectMethodAndUpdateWorkflow(String scaMethodId, final PaymentWorkflow workflow, boolean isCancellationOperation) {
        try {
            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            SCAPaymentResponseTO paymentResponseTO = isCancellationOperation
                                                         ? paymentRestClient.selecCancelPaymentSCAtMethod(workflow.paymentId(), workflow.authId(), scaMethodId).getBody()
                                                         : paymentRestClient.selectMethod(workflow.paymentId(), workflow.authId(), scaMethodId).getBody();
            processPaymentResponse(workflow, paymentResponseTO);
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    private void updateAuthorisationStatus(PaymentWorkflow workflow, String psuId) {
        String paymentId = workflow.getPaymentResponse().getPayment().getPaymentId();
        String authorisationId = workflow.getPaymentResponse().getAuthorisationId();
        String status = workflow.getAuthResponse().getScaStatus().name();
        cmsPsuPisClient.updateAuthorisationStatus(psuId, null, null, null,
            paymentId, authorisationId, status, CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, null));
    }

    private CmsPaymentResponse loadPaymentByRedirectId(String psuId, ConsentReference consentReference) {
        String psuIdType = null;
        String psuCorporateId = null;
        String psuCorporateIdType = null;
        String redirectId = consentReference.getRedirectId();
        // 4. After user login:
        ResponseEntity<CmsPaymentResponse> cmsResponse = cmsPsuPisClient.getPaymentByRedirectId(
            psuId, psuIdType, psuCorporateId, psuCorporateIdType, redirectId, CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);

        return cmsResponse.getBody();
    }

    private ScaStatusTO loadAuthorization(String authorizationId) {
        try {
            return valueOf(cmsPsuPisClient.getAuthorisationByAuthorisationId(authorizationId, CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID).getBody().getScaStatus().name());
        } catch (FeignException e) {
            throw ObaException.builder()
                      .obaErrorCode(NOT_FOUND)
                      .devMessage("Authorization for payment not found!")
                      .build();
        }
    }

    private void updatePaymentStatus(PaymentWorkflow paymentWorkflow) {
        cmsPsuPisClient.updatePaymentStatus(paymentWorkflow.getPaymentResponse().getPayment().getPaymentId(), paymentWorkflow.getPaymentStatus(), CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);
        paymentWorkflow.getAuthResponse().updatePaymentStatus(TransactionStatusTO.valueOf(paymentWorkflow.getPaymentStatus()));
    }

    @Override
    public void updateAspspConsentData(PaymentWorkflow paymentWorkflow) {
        CmsAspspConsentDataBase64 consentData;
        try {
            consentData = new CmsAspspConsentDataBase64(paymentWorkflow.paymentId(), tokenStorageService.toBase64String(paymentWorkflow.getScaResponse()));
        } catch (IOException e) {
            throw AuthorizationException.builder()
                      .errorCode(CONSENT_DATA_UPDATE_FAILED)
                      .devMessage("Consent data update failed")
                      .build();
        }
        aspspConsentDataClient.updateAspspConsentData(paymentWorkflow.getConsentReference().getEncryptedConsentId(), consentData);
    }

    @Override
    public String resolveRedirectUrl(String encryptedPaymentId, String authorisationId, String consentAndAccessTokenCookieString, boolean isOauth2Integrated, String psuId, BearerTokenTO tokenTO) {
        PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, tokenTO);

        CmsPaymentResponse consentResponse = workflow.getPaymentResponse();

        authInterceptor.setAccessToken(workflow.getScaResponse().getBearerToken().getAccess_token());
        String tppOkRedirectUri = isOauth2Integrated
                                      ? oauthRestClient.oauthCode(consentResponse.getTppOkRedirectUri()).getBody().getRedirectUri()
                                      : consentResponse.getTppOkRedirectUri();

        String tppNokRedirectUri = consentResponse.getTppNokRedirectUri();
        ScaStatusTO scaStatus = loadAuthorization(workflow.authId());

        return FINALISED.equals(scaStatus)
                   ? tppOkRedirectUri
                   : tppNokRedirectUri;
    }
}

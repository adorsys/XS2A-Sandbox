package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.mappers.PaymentMapperTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.AuthorizationService;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.pis.CmsCommonPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.psu.api.CmsPsuAuthorisation;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import de.adorsys.psd2.xs2a.core.exception.AuthorisationIsExpiredException;
import de.adorsys.psd2.xs2a.core.exception.RedirectUrlIsExpiredException;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Optional;

import static de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode.CONSENT_DATA_UPDATE_FAILED;
import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.AUTH_EXPIRED;
import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.NOT_FOUND;
import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonPaymentServiceImpl implements CommonPaymentService {
    private final ConsentReferencePolicy referencePolicy;
    private final AuthRequestInterceptor authInterceptor;
    private final CmsPsuPisService cmsPsuPisService;
    private final PaymentRestClient paymentRestClient;
    private final AspspConsentDataClient aspspConsentDataClient;
    private final CmsAspspConsentDataService dataService;
    private final PaymentMapperTO paymentMapper;
    private final OauthRestClient oauthRestClient;
    private final AuthorizationService authService;
    private final RedirectScaRestClient redirectScaClient;

    @Override
    public PaymentWorkflow selectScaForPayment(String encryptedPaymentId, String authorisationId, String scaMethodId, String consentAndAccessTokenCookieString, String psuId, BearerTokenTO tokenTO) {
        PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, tokenTO);
        selectMethodAndUpdateWorkflow(scaMethodId, encryptedPaymentId, workflow);
        doUpdateAuthData(psuId, workflow);
        return workflow;
    }

    @Override
    public PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId, boolean strict, String consentCookieString, String psuId, BearerTokenTO bearerToken) {
        ConsentReference consentReference = referencePolicy.fromRequest(encryptedPaymentId, authorizationId, consentCookieString, strict);
        CmsPaymentResponse cmsPaymentResponse = loadPaymentByRedirectId(consentReference);
        PaymentWorkflow workflow = new PaymentWorkflow(cmsPaymentResponse, consentReference);
        PaymentTO payment = getPaymentTO(workflow);

        workflow.setAuthResponse(new PaymentAuthorizeResponse(payment));
        workflow.getAuthResponse().setAuthorisationId(cmsPaymentResponse.getAuthorisationId());
        workflow.getAuthResponse().setEncryptedConsentId(encryptedPaymentId);
        workflow.setPaymentStatus(Optional.ofNullable(payment.getTransactionStatus()).map(Enum::name).orElse("RCVD"));
        if (bearerToken != null) {
            GlobalScaResponseTO response = new GlobalScaResponseTO();
            response.setBearerToken(bearerToken);
            workflow.setScaResponse(response);
        }
        return workflow;
    }

    @Override
    public void updateAspspConsentData(PaymentWorkflow paymentWorkflow) {
        CmsAspspConsentDataBase64 consentData;
        try {
            consentData = new CmsAspspConsentDataBase64(paymentWorkflow.paymentId(), dataService.toBase64String(paymentWorkflow.getScaResponse()));
        } catch (IOException e) {
            throw AuthorizationException.builder()
                      .errorCode(CONSENT_DATA_UPDATE_FAILED)
                      .devMessage("Consent data update failed")
                      .build();
        }
        aspspConsentDataClient.updateAspspConsentData(paymentWorkflow.getConsentReference().getEncryptedConsentId(), consentData);
    }

    @Override
    public String resolveRedirectUrl(String encryptedPaymentId, String authorisationId, String consentAndAccessTokenCookieString, boolean isOauth2Integrated, String psuId, BearerTokenTO tokenTO, String authConfirmationCode) {
        PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, tokenTO);

        CmsPaymentResponse consentResponse = workflow.getPaymentResponse();

        authInterceptor.setAccessToken(workflow.getScaResponse().getBearerToken().getAccess_token());
        String tppOkRedirectUri = isOauth2Integrated
                                      ? oauthRestClient.oauthCode(consentResponse.getTppOkRedirectUri()).getBody().getRedirectUri()
                                      : authService.resolveAuthConfirmationCodeRedirectUri(consentResponse.getTppOkRedirectUri(), authConfirmationCode);

        String tppNokRedirectUri = consentResponse.getTppNokRedirectUri();
        ScaStatusTO scaStatus = loadAuthorization(workflow.authId());

        return EnumSet.of(ScaStatusTO.FINALISED, ScaStatusTO.UNCONFIRMED, ScaStatusTO.EXEMPTED).contains(scaStatus)
                   ? tppOkRedirectUri
                   : tppNokRedirectUri;
    }

    @Override
    public PaymentWorkflow initiatePaymentOpr(PaymentWorkflow paymentWorkflow, String psuId, OpTypeTO opType) {
        authInterceptor.setAccessToken(paymentWorkflow.bearerToken().getAccess_token());

        SCAPaymentResponseTO paymentResponseTO = opType == OpTypeTO.PAYMENT
                                                     ? paymentRestClient.initiatePayment(paymentWorkflow.paymentType(), paymentWorkflow.getAuthResponse().getPayment()).getBody()
                                                     : paymentRestClient.initiatePmtCancellation(paymentWorkflow.paymentId()).getBody();
        GlobalScaResponseTO response = dataService.mapToGlobalResponse(paymentResponseTO, opType);

        paymentWorkflow.processSCAResponse(response);
        paymentWorkflow.setPaymentStatus(paymentResponseTO.getTransactionStatus().name());

        doUpdateAuthData(psuId, paymentWorkflow);

        return paymentWorkflow;
    }

    @Override
    public PaymentWorkflow authorizePaymentOpr(PaymentWorkflow paymentWorkflow, String psuId, String authCode, OpTypeTO opType) {
        authInterceptor.setAccessToken(paymentWorkflow.bearerToken().getAccess_token());
        GlobalScaResponseTO response = redirectScaClient.validateScaCode(paymentWorkflow.authId(), authCode).getBody();

        authInterceptor.setAccessToken(response.getBearerToken().getAccess_token());
        SCAPaymentResponseTO scaPaymentResponse = opType == OpTypeTO.PAYMENT
                                                      ? paymentRestClient.executePayment(paymentWorkflow.paymentId()).getBody()
                                                      : paymentRestClient.executeCancelPayment(paymentWorkflow.paymentId()).getBody();

        paymentWorkflow.processSCAResponse(response);
        paymentWorkflow.setPaymentStatus(opType == OpTypeTO.PAYMENT
                                             ? scaPaymentResponse.getTransactionStatus().name()
                                             : TransactionStatusTO.CANC.toString());
        doUpdateAuthData(psuId, paymentWorkflow);
        return paymentWorkflow;
    }

    private void doUpdateAuthData(String psuId, PaymentWorkflow workflow) {
        updateAuthorisationStatus(workflow, psuId);
        updatePaymentStatus(workflow);
        updateAspspConsentData(workflow);
    }

    private void selectMethodAndUpdateWorkflow(String scaMethodId, String externalId, final PaymentWorkflow workflow) {
        try {
            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
            StartScaOprTO opr = new StartScaOprTO(workflow.paymentId(), externalId, workflow.authId(), OpTypeTO.PAYMENT);

            GlobalScaResponseTO response = redirectScaClient.startSca(opr).getBody();
            response = redirectScaClient.selectMethod(response.getAuthorisationId(), scaMethodId).getBody();
            workflow.processSCAResponse(response);
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    private void updateAuthorisationStatus(PaymentWorkflow workflow, String psuId) {
        String paymentId = workflow.getPaymentResponse().getPayment().getPaymentId();
        String authorisationId = workflow.getPaymentResponse().getAuthorisationId();
        String status = workflow.getAuthResponse().getScaStatus().name();
        try {
            cmsPsuPisService.updateAuthorisationStatus(new PsuIdData(psuId, null, null, null, null),
                                                       paymentId, authorisationId, ScaStatus.valueOf(status), DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, workflow.getScaResponse().getAuthConfirmationCode()));
        } catch (AuthorisationIsExpiredException e) {
            log.error("Authorization for your payment has expired!");
            throw ObaException.builder()
                      .obaErrorCode(AUTH_EXPIRED)
                      .devMessage(e.getMessage())
                      .build();
        }
    }

    private CmsPaymentResponse loadPaymentByRedirectId(ConsentReference consentReference) {
        String redirectId = consentReference.getRedirectId();
        try {
            return cmsPsuPisService.checkRedirectAndGetPayment(redirectId, DEFAULT_SERVICE_INSTANCE_ID)
                       .orElseThrow(() -> new RedirectUrlIsExpiredException(null));
        } catch (RedirectUrlIsExpiredException e) {
            throw ObaException.builder()
                      .obaErrorCode(NOT_FOUND)
                      .devMessage(String.format("Could not retrieve payment %s from CMS", redirectId))
                      .build();
        }
    }

    private ScaStatusTO loadAuthorization(String authorizationId) {
        return cmsPsuPisService.getAuthorisationByAuthorisationId(authorizationId, DEFAULT_SERVICE_INSTANCE_ID)
                   .map(CmsPsuAuthorisation::getScaStatus)
                   .map(Enum::name)
                   .map(ScaStatusTO::valueOf)
                   .orElseThrow(() -> ObaException.builder()
                                          .obaErrorCode(NOT_FOUND)
                                          .devMessage("Authorization for payment not found!")
                                          .build());

    }

    private void updatePaymentStatus(PaymentWorkflow paymentWorkflow) {
        cmsPsuPisService.updatePaymentStatus(paymentWorkflow.getPaymentResponse().getPayment().getPaymentId(), TransactionStatus.valueOf(paymentWorkflow.getPaymentStatus()), DEFAULT_SERVICE_INSTANCE_ID);
        paymentWorkflow.getAuthResponse().getPayment().setTransactionStatus(TransactionStatusTO.valueOf(paymentWorkflow.getPaymentStatus()));
    }

    private PaymentTO getPaymentTO(PaymentWorkflow workflow) {
        CmsCommonPayment payment = (CmsCommonPayment) workflow.getPaymentResponse().getPayment();
        String paymentString = new String(payment.getPaymentData(), StandardCharsets.UTF_8);
        PaymentTO abstractPayment = paymentMapper.toAbstractPayment(paymentString, workflow.paymentType().name(), payment.getPaymentProduct());
        abstractPayment.setPaymentId(workflow.paymentId());
        abstractPayment.setTransactionStatus(TransactionStatusTO.valueOf(payment.getTransactionStatus().name()));
        return abstractPayment;
    }
}

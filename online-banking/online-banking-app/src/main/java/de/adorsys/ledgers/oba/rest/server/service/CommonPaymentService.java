package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.domain.ValidationCode;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.rest.server.mapper.PaymentConverter;
import de.adorsys.ledgers.oba.rest.server.resource.AuthUtils;
import de.adorsys.ledgers.oba.rest.server.resource.ResponseUtils;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

import static de.adorsys.psd2.xs2a.core.profile.PaymentType.PERIODIC;
import static de.adorsys.psd2.xs2a.core.profile.PaymentType.SINGLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonPaymentService {
    private final ResponseUtils responseUtils;
    private final ConsentReferencePolicy referencePolicy;
    private final AuthRequestInterceptor authInterceptor;
    private final CmsPsuPisClient cmsPsuPisClient;
    private final PaymentRestClient paymentRestClient;
    private final AspspConsentDataClient aspspConsentDataClient;
    private final TokenStorageService tokenStorageService;
    private final PaymentConverter paymentConverter;
    private final MiddlewareAuthentication auth;
    private final UserMgmtRestClient userMgmtRestClient;

    public boolean loginForPaymentOperation(String login, String pin, PaymentWorkflow paymentWorkflow, OpTypeTO operationType) {
        ResponseEntity<SCALoginResponseTO> authoriseForConsent =
            userMgmtRestClient.authoriseForConsent(login, pin, paymentWorkflow.paymentId(), paymentWorkflow.authId(), operationType);
        processSCAResponse(paymentWorkflow, authoriseForConsent.getBody());
        return AuthUtils.success(authoriseForConsent);
    }

    public ResponseEntity<PaymentAuthorizeResponse> selectScaForPayment(String encryptedPaymentId, String authorisationId, String scaMethodId, String consentAndAccessTokenCookieString, HttpServletResponse response, boolean isCancellationOperation) {
        String psuId = AuthUtils.psuId(auth);
        try {
            PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, response, auth.getBearerToken());
            selectMethodAndUpdateWorkflow(scaMethodId, workflow, isCancellationOperation);

            updateScaStatusPaymentStatusConsentData(psuId, workflow, response);

            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        }
    }

    public ResponseEntity<PaymentAuthorizeResponse> resolvePaymentWorkflow(PaymentWorkflow workflow, HttpServletResponse response) {
        switch (workflow.scaStatus()) {
            case PSUIDENTIFIED:
            case FINALISED:
            case EXEMPTED:
            case PSUAUTHENTICATED:
            case SCAMETHODSELECTED:
                responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
                return ResponseEntity.ok(workflow.getAuthResponse());
            case STARTED:
            case FAILED:
            default:
                // failed Message. No repeat. Delete cookies.
                responseUtils.removeCookies(response);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId, boolean strict, String consentCookieString, String psuId, HttpServletResponse response, BearerTokenTO bearerToken) throws PaymentAuthorizeException {
        ConsentReference consentReference;
        try {
            String consentCookie = responseUtils.consentCookie(consentCookieString);
            consentReference = referencePolicy.fromRequest(encryptedPaymentId, authorizationId, consentCookie, strict);
        } catch (InvalidConsentException e) {
            throw new PaymentAuthorizeException(responseUtils.forbidden(authResp(), e.getMessage(), response));
        }

        CmsPaymentResponse cmsPaymentResponse = loadPaymentByRedirectId(psuId, consentReference, response);

        PaymentWorkflow workflow = new PaymentWorkflow(cmsPaymentResponse, consentReference);
        Object convertedPaymentTO = paymentConverter.convertPayment(response, workflow.paymentType(), cmsPaymentResponse);
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

    private String resolvePaymentStatus(CmsPayment payment) {
        if (EnumSet.of(SINGLE, PERIODIC).contains(payment.getPaymentType())) {
            CmsSinglePayment singlePayment = (CmsSinglePayment) payment;
            return singlePayment.getPaymentStatus().name();
        } else {
            CmsBulkPayment bulkPayment = (CmsBulkPayment) payment;
            return bulkPayment.getPayments().get(0).getPaymentStatus().name();
        }
    }

    public void selectMethodAndUpdateWorkflow(String scaMethodId, final PaymentWorkflow workflow, boolean isCancellationOperation) {
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

    public void updateScaStatusPaymentStatusConsentData(String psuId, PaymentWorkflow workflow, HttpServletResponse response) throws PaymentAuthorizeException {
        updateAuthorisationStatus(workflow, psuId, response);
        updatePaymentStatus(response, workflow);
        updateAspspConsentData(workflow, response);
    }

    private void updateAuthorisationStatus(PaymentWorkflow workflow, String psuId, HttpServletResponse response) throws PaymentAuthorizeException {
        String paymentId = workflow.getPaymentResponse().getPayment().getPaymentId();
        String authorisationId = workflow.getPaymentResponse().getAuthorisationId();
        String status = workflow.getAuthResponse().getScaStatus().name();
        ResponseEntity<Void> resp = cmsPsuPisClient.updateAuthorisationStatus(psuId, null, null, null,
            paymentId, authorisationId, status, CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);
        if (resp.getStatusCode() != HttpStatus.OK) {
            throw new PaymentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), "Error updating authorisation status. See error code.", resp.getStatusCode(), response));
        }
    }

    private PaymentAuthorizeResponse authResp() {
        return new PaymentAuthorizeResponse();
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private CmsPaymentResponse loadPaymentByRedirectId(String psuId,
                                                       ConsentReference consentReference, HttpServletResponse response) throws PaymentAuthorizeException {
        String psuIdType = null;
        String psuCorporateId = null;
        String psuCorporateIdType = null;
        String redirectId = consentReference.getRedirectId();
        // 4. After user login:
        ResponseEntity<CmsPaymentResponse> responseEntity = cmsPsuPisClient.getPaymentByRedirectId(
            psuId, psuIdType, psuCorporateId, psuCorporateIdType, redirectId, CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (HttpStatus.OK.equals(statusCode)) {
            return responseEntity.getBody();
        }

        if (HttpStatus.NOT_FOUND.equals(statusCode)) {
            // ---> if(NotFound)
            throw new PaymentAuthorizeException(responseUtils.requestWithRedNotFound(authResp(), response));
        }

        if (HttpStatus.REQUEST_TIMEOUT.equals(statusCode)) {
            // ---> if(Expired, TPP-Redirect-URL)
            // 3.a0) LogOut User
            // 3.a1) Send back to TPP
            CmsPaymentResponse payment = responseEntity.getBody();
            String location = StringUtils.isNotBlank(payment.getTppNokRedirectUri())
                                  ? payment.getTppNokRedirectUri()
                                  : payment.getTppOkRedirectUri();
            throw new PaymentAuthorizeException(responseUtils.redirect(location, response));
        } else if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new PaymentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), responseEntity.getStatusCode(), response));
        }

        throw new PaymentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), statusCode, response));
    }

    public void processPaymentResponse(PaymentWorkflow paymentWorkflow, SCAPaymentResponseTO paymentResponse) {
        processSCAResponse(paymentWorkflow, paymentResponse);
        paymentWorkflow.setPaymentStatus(paymentResponse.getTransactionStatus().name());
    }

    private void processSCAResponse(PaymentWorkflow workflow, SCAResponseTO paymentResponse) {
        workflow.setScaResponse(paymentResponse);
        workflow.getAuthResponse().setAuthorisationId(paymentResponse.getAuthorisationId());
        workflow.getAuthResponse().setScaStatus(paymentResponse.getScaStatus());
        workflow.getAuthResponse().setScaMethods(paymentResponse.getScaMethods());
        workflow.setAuthCodeMessage(paymentResponse.getPsuMessage());
    }

    private void updatePaymentStatus(HttpServletResponse response, PaymentWorkflow paymentWorkflow)
        throws PaymentAuthorizeException {
        ResponseEntity<Void> updatePaymentStatus = cmsPsuPisClient.updatePaymentStatus(
            paymentWorkflow.getPaymentResponse().getPayment().getPaymentId(), paymentWorkflow.getPaymentStatus(), CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);
        paymentWorkflow.getAuthResponse().updatePaymentStatus(TransactionStatusTO.valueOf(paymentWorkflow.getPaymentStatus()));
        if (!HttpStatus.OK.equals(updatePaymentStatus.getStatusCode())) {
            throw new PaymentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), "Could not set payment status. See status code.", updatePaymentStatus.getStatusCode(), response));
        }
    }

    public void updateAspspConsentData(PaymentWorkflow paymentWorkflow, HttpServletResponse httpResp) throws PaymentAuthorizeException {
        CmsAspspConsentDataBase64 consentData;
        try {
            consentData = new CmsAspspConsentDataBase64(paymentWorkflow.paymentId(), tokenStorageService.toBase64String(paymentWorkflow.getScaResponse()));
        } catch (IOException e) {
            throw new PaymentAuthorizeException(
                responseUtils.backToSender(authResp(), paymentWorkflow.getPaymentResponse().getTppNokRedirectUri(),
                    paymentWorkflow.getPaymentResponse().getTppOkRedirectUri(),
                    httpResp, HttpStatus.INTERNAL_SERVER_ERROR, ValidationCode.CONSENT_DATA_UPDATE_FAILED));
        }
        ResponseEntity<?> updateAspspConsentData = aspspConsentDataClient.updateAspspConsentData(
            paymentWorkflow.getConsentReference().getEncryptedConsentId(), consentData);
        if (!HttpStatus.OK.equals(updateAspspConsentData.getStatusCode())) {
            throw new PaymentAuthorizeException(
                responseUtils.backToSender(authResp(), paymentWorkflow.getPaymentResponse().getTppNokRedirectUri(),
                    paymentWorkflow.getPaymentResponse().getTppOkRedirectUri(),
                    httpResp, updateAspspConsentData.getStatusCode(), ValidationCode.CONSENT_DATA_UPDATE_FAILED));
        }
    }

    public ResponseEntity<PaymentAuthorizeResponse> resolveRedirectUrl(String encryptedPaymentId, String authorisationId, String consentAndAccessTokenCookieString, HttpServletResponse response) throws PaymentAuthorizeException {
        String psuId = AuthUtils.psuId(auth);
        PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, response, auth.getBearerToken());

        ScaStatusTO scaStatus = workflow.getScaResponse().getScaStatus();
        CmsPaymentResponse consentResponse = workflow.getPaymentResponse();

        String tppOkRedirectUri = consentResponse.getTppOkRedirectUri();
        String tppNokRedirectUri = consentResponse.getTppNokRedirectUri();

        String redirectURL = ScaStatusTO.FINALISED.equals(scaStatus)
                                 ? tppOkRedirectUri
                                 : tppNokRedirectUri;

        return responseUtils.redirect(redirectURL, response);
    }
}

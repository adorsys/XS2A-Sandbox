package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.*;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.api.resource.PISApi;
import de.adorsys.ledgers.oba.rest.server.mapper.BulkPaymentMapper;
import de.adorsys.ledgers.oba.rest.server.mapper.PeriodicPaymentMapper;
import de.adorsys.ledgers.oba.rest.server.mapper.SinglePaymentMapper;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController(PISController.BASE_PATH)
@RequestMapping(PISController.BASE_PATH)
@Api(value = PISController.BASE_PATH, tags = "PSU PIS", description = "Provides access to online banking payment functionality")
public class PISController extends AbstractXISController implements PISApi {
    @Autowired
    private CmsPsuPisClient cmsPsuPisClient;
    @Autowired
    private PaymentRestClient paymentRestClient;
    @Autowired
    private UserMgmtRestClient userMgmtRestClient;

    @Autowired
    private SinglePaymentMapper singlePaymentMapper;
    @Autowired
    private BulkPaymentMapper bulkPaymentMapper;
    @Autowired
    private PeriodicPaymentMapper periodicPaymentMapper;

    //TODO remove and refactor https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/43
    private ScaStatusTO scaStatus;
    private String tppNokRedirectUri;
    private String tppOkRedirectUri;

    @Override
    public ResponseEntity<AuthorizeResponse> pisAuth(String redirectId, String encryptedPaymentId) {
        return auth(redirectId, ConsentType.PIS, encryptedPaymentId, request, response);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(
        String encryptedPaymentId,
        String authorisationId,
        String login,
        String pin,
        String consentCookieString) {

        PaymentWorkflow workflow;
        try {
            workflow = identifyPayment(encryptedPaymentId, authorisationId, false, consentCookieString, login, response, null);
            CmsPaymentResponse payment = workflow.getPaymentResponse();
            scaStatus = ScaStatusTO.RECEIVED;
            tppNokRedirectUri = payment.getTppNokRedirectUri();
            tppOkRedirectUri = payment.getTppOkRedirectUri();
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        }

        // Authorize
        ResponseEntity<SCALoginResponseTO> authoriseForConsent =
            userMgmtRestClient.authoriseForConsent(login, pin, workflow.paymentId(), workflow.authId(), OpTypeTO.PAYMENT);
        processSCAResponse(workflow, authoriseForConsent.getBody());

        boolean success = AuthUtils.success(authoriseForConsent);

        if (success) {
            String psuId = AuthUtils.psuId(workflow.bearerToken());
            try {
                updateAuthorisationStatus(workflow, psuId, response);
                initiatePayment(workflow, response);

                // Select sca if no alternative.
                if (workflow.singleScaMethod()) {
                    ScaUserDataTO scaUserDataTO = workflow.scaMethods().iterator().next();
                    selectMethod(scaUserDataTO.getId(), workflow);
                }

                updateScaStatusPaymentStatusConsentData(psuId, workflow);
            } catch (PaymentAuthorizeException e) {
                return e.getError();
            }

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
        } else {
            // failed Message. No repeat. Delete cookies.
            responseUtils.removeCookies(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> initiatePayment(
        String encryptedPaymentId, String authorisationId, String consentAndaccessTokenCookieString) {

        try {
            String psuId = AuthUtils.psuId(auth);
            // Identity the link and load the workflow.
            PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());

            // Update status
            workflow.getScaResponse().setScaStatus(ScaStatusTO.PSUAUTHENTICATED);
            updateAuthorisationStatus(workflow, psuId, response);
            scaStatus = workflow.getAuthResponse().getScaStatus();

            initiatePayment(workflow, response);
            updateScaStatusPaymentStatusConsentData(psuId, workflow);

            // Store result in token.
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> selectMethod(
        String encryptedPaymentId, String authorisationId,
        String scaMethodId, String consentAndaccessTokenCookieString) {

        String psuId = AuthUtils.psuId(auth);
        try {
            PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());
            selectMethod(scaMethodId, workflow);

            updateScaStatusPaymentStatusConsentData(psuId, workflow);
            scaStatus = workflow.getAuthResponse().getScaStatus();

            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authrizedPayment(
        String encryptedPaymentId,
        String authorisationId,
        String consentAndaccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.psuId(auth);
        try {
            PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            SCAPaymentResponseTO scaPaymentResponse = paymentRestClient.authorizePayment(workflow.paymentId(), workflow.authId(), authCode).getBody();
            processPaymentResponse(workflow, scaPaymentResponse);

            updateScaStatusPaymentStatusConsentData(psuId, workflow);
            scaStatus = workflow.getAuthResponse().getScaStatus();

            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> failPaymentAuthorisation(String encryptedPaymentId, String authorisationId, String cookieString) {
        String psuId = AuthUtils.psuId(auth);
        try {
            PaymentWorkflow workflow = identifyPayment(encryptedPaymentId, authorisationId, true, cookieString, psuId, response, auth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            workflow.getScaResponse().setScaStatus(ScaStatusTO.FAILED);
            updateAuthorisationStatus(workflow, psuId, response);
            updateAspspConsentData(workflow, response);

            responseUtils.removeCookies(response);
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            responseUtils.removeCookies(response);
            return e.getError();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> pisDone(String encryptedPaymentId, String authorisationId,
                                                            String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp) {
        String redirectURL = ScaStatusTO.FINALISED.equals(scaStatus)
                                 ? tppOkRedirectUri
                                 : tppNokRedirectUri;

        return responseUtils.redirect(redirectURL, response);
    }

    private void updateScaStatusPaymentStatusConsentData(String psuId, PaymentWorkflow workflow)
        throws PaymentAuthorizeException {
        // UPDATE CMS
        SCAResponseTO scaResponse = workflow.getScaResponse();
        if (scaResponse != null && scaResponse.getScaStatus() != ScaStatusTO.EXEMPTED) {
            updateAuthorisationStatus(workflow, psuId, response);
        }
        updatePaymentStatus(response, workflow);
        updateAspspConsentData(workflow, response);
    }

    private void updateAspspConsentData(PaymentWorkflow paymentWorkflow, HttpServletResponse httpResp) throws PaymentAuthorizeException {
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

    PaymentAuthorizeResponse authResp() {
        return new PaymentAuthorizeResponse();
    }

    private void initiatePayment(final PaymentWorkflow paymentWorkflow, HttpServletResponse response) throws PaymentAuthorizeException {
        CmsPaymentResponse paymentResponse = paymentWorkflow.getPaymentResponse();
        Object payment = convertPayment(response, paymentWorkflow.paymentType(), paymentResponse);
        try {
            authInterceptor.setAccessToken(paymentWorkflow.bearerToken().getAccess_token());
            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.initiatePayment(paymentWorkflow.paymentType(), payment).getBody();
            processPaymentResponse(paymentWorkflow, paymentResponseTO);
        } catch (FeignException f) {
            paymentWorkflow.setErrorCode(HttpStatus.valueOf(f.status()));
            throw f;
        } finally {
            authInterceptor.setAccessToken(null);
        }
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

    private Object convertPayment(HttpServletResponse response, PaymentTypeTO paymentType,
                                  CmsPaymentResponse paymentResponse) throws PaymentAuthorizeException {
        switch (paymentType) {
            case SINGLE:
                return singlePaymentMapper.toPayment((CmsSinglePayment) paymentResponse.getPayment());
            case BULK:
                return bulkPaymentMapper.toPayment((CmsBulkPayment) paymentResponse.getPayment());
            case PERIODIC:
                return periodicPaymentMapper.toPayment((CmsPeriodicPayment) paymentResponse.getPayment());
            default:
                throw new PaymentAuthorizeException(responseUtils.badRequest(authResp(), String.format("Payment type %s not supported.", paymentType.name()), response));
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId, boolean strict, String consentCookieString, String psuId, HttpServletResponse response, BearerTokenTO bearerToken) throws PaymentAuthorizeException {
        ConsentReference consentReference = null;
        try {
            String consentCookie = responseUtils.consentCookie(consentCookieString);
            consentReference = referencePolicy.fromRequest(encryptedPaymentId, authorizationId, consentCookie, strict);
        } catch (InvalidConsentException e) {
            throw new PaymentAuthorizeException(responseUtils.forbidden(authResp(), e.getMessage(), response));
        }

        CmsPaymentResponse cmsPaymentResponse = loadPaymentByRedirectId(psuId, consentReference, response);

        PaymentWorkflow workflow = new PaymentWorkflow(cmsPaymentResponse, consentReference);
        Object convertedPaymentTO = convertPayment(response, workflow.paymentType(), cmsPaymentResponse);
        workflow.setAuthResponse(new PaymentAuthorizeResponse(workflow.paymentType(), convertedPaymentTO));
        workflow.getAuthResponse().setAuthorisationId(cmsPaymentResponse.getAuthorisationId());
        workflow.getAuthResponse().setEncryptedConsentId(encryptedPaymentId);
        if (bearerToken != null) {
            SCAPaymentResponseTO scaPaymentResponseTO = new SCAPaymentResponseTO();
            scaPaymentResponseTO.setBearerToken(bearerToken);
            workflow.setScaResponse(scaPaymentResponseTO);
        }
        return workflow;
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

    @Override
    public String getBasePath() {
        return BASE_PATH;
    }


    private SCAPaymentResponseTO selectMethod(String scaMethodId, final PaymentWorkflow workflow) {
        try {
            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.selectMethod(workflow.paymentId(), workflow.authId(), scaMethodId).getBody();
            processPaymentResponse(workflow, paymentResponseTO);
            return paymentResponseTO;
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    private void processPaymentResponse(PaymentWorkflow paymentWorkflow, SCAPaymentResponseTO paymentResponse) {
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

}

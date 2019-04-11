package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.domain.ValidationCode;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.api.resource.PisCancellationApi;
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

//TODO refactor and write tests https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/33
@RestController(PisCancellationController.BASE_PATH)
@RequestMapping(PisCancellationController.BASE_PATH)
@Api(value = PisCancellationController.BASE_PATH, tags = "PSU PIS Cancellation", description = "Provides access to online banking payment functionality")
public class PisCancellationController extends AbstractXISController implements PisCancellationApi {

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

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(
        String encryptedPaymentId,
        String authorisationId,
        String login,
        String pin,
        String consentCookieString) {

        PaymentWorkflow cancellationWorkflow;
        try {
            cancellationWorkflow = identifyPayment(encryptedPaymentId, authorisationId, false, consentCookieString, login, response, null);
            CmsPaymentResponse payment = cancellationWorkflow.getPaymentResponse();
            tppNokRedirectUri = payment.getTppNokRedirectUri();
            tppOkRedirectUri = payment.getTppOkRedirectUri();
            scaStatus = ScaStatusTO.RECEIVED;

        } catch (PaymentAuthorizeException e) {
            return e.getError();
        }

        // Authorize
        ResponseEntity<SCALoginResponseTO> authoriseForConsent =
            userMgmtRestClient.authoriseForConsent(login, pin, cancellationWorkflow.paymentId(), cancellationWorkflow.authId(), OpTypeTO.PAYMENT);
        processSCAResponse(cancellationWorkflow, authoriseForConsent.getBody());

        boolean success = AuthUtils.success(authoriseForConsent);

        if (success) {
            String psuId = AuthUtils.psuId(cancellationWorkflow.bearerToken());
            try {
                updateAuthorisationStatus(cancellationWorkflow, psuId, response);
                initiateCancelPayment(cancellationWorkflow);

                if (cancellationWorkflow.singleScaMethod()) {
                    ScaUserDataTO scaUserDataTO = cancellationWorkflow.scaMethods().iterator().next();
                    selectMethod(scaUserDataTO.getId(), cancellationWorkflow);
                }

                updateScaStatusPaymentStatusConsentData(psuId, cancellationWorkflow);
            } catch (PaymentAuthorizeException e) {
                return e.getError();
            }

            switch (cancellationWorkflow.scaStatus()) {
                case PSUIDENTIFIED:
                case FINALISED:
                case EXEMPTED:
                case PSUAUTHENTICATED:
                case SCAMETHODSELECTED:
                    responseUtils.setCookies(response, cancellationWorkflow.getConsentReference(), cancellationWorkflow.bearerToken().getAccess_token(), cancellationWorkflow.bearerToken().getAccessTokenObject());
                    return ResponseEntity.ok(cancellationWorkflow.getAuthResponse());
                case STARTED:
                case FAILED:
                default:
                    responseUtils.removeCookies(response);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            responseUtils.removeCookies(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> selectMethod(
        String encryptedPaymentId, String authorisationId,
        String scaMethodId, String consentAndAccessTokenCookieString) {

        String psuId = AuthUtils.psuId(auth);
        try {
            PaymentWorkflow cancellationWorkflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, response, auth.getBearerToken());
            selectMethod(scaMethodId, cancellationWorkflow);

            updateScaStatusPaymentStatusConsentData(psuId, cancellationWorkflow);
            scaStatus = cancellationWorkflow.getAuthResponse().getScaStatus();

            responseUtils.setCookies(response, cancellationWorkflow.getConsentReference(), cancellationWorkflow.bearerToken().getAccess_token(), cancellationWorkflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(cancellationWorkflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authorisePayment(
        String encryptedPaymentId,
        String authorisationId,
        String consentAndAccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.psuId(auth);
        try {
            PaymentWorkflow cancellationWorkflow = identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, response, auth.getBearerToken());

            authInterceptor.setAccessToken(cancellationWorkflow.bearerToken().getAccess_token());

            SCAPaymentResponseTO scaPaymentResponse = paymentRestClient.authorizeCancelPayment(cancellationWorkflow.paymentId(), cancellationWorkflow.authId(), authCode).getBody();
            processPaymentResponse(cancellationWorkflow, scaPaymentResponse);

            cancellationWorkflow.setPaymentStatus(TransactionStatusTO.CANC.toString());
            updateScaStatusPaymentStatusConsentData(psuId, cancellationWorkflow);
            scaStatus = cancellationWorkflow.getAuthResponse().getScaStatus();

            responseUtils.setCookies(response, cancellationWorkflow.getConsentReference(), cancellationWorkflow.bearerToken().getAccess_token(), cancellationWorkflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(cancellationWorkflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> pisDone(String encryptedPaymentId, String authorisationId,
                                                            String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp) {
        String redirectURL = ScaStatusTO.FINALISED.equals(scaStatus)
                                 ? tppNokRedirectUri
                                 : tppOkRedirectUri;

        return responseUtils.redirect(redirectURL, response);
    }

    private void initiateCancelPayment(final PaymentWorkflow paymentWorkflow) {
        try {
            authInterceptor.setAccessToken(paymentWorkflow.bearerToken().getAccess_token());
            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.initiatePmtCancellation(paymentWorkflow.paymentId()).getBody();
            processPaymentResponse(paymentWorkflow, paymentResponseTO);
        } catch (FeignException f) {
            paymentWorkflow.setErrorCode(HttpStatus.valueOf(f.status()));
            throw f;
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    private void updateScaStatusPaymentStatusConsentData(String psuId, PaymentWorkflow cancellationWorkflow)
        throws PaymentAuthorizeException {
        updateAuthorisationStatus(cancellationWorkflow, psuId, response);
        updateCmsPaymentStatus(response, cancellationWorkflow);
        updateAspspConsentData(cancellationWorkflow, response);
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
        ResponseEntity<?> updateAspspConsentDataResponse = aspspConsentDataClient.updateAspspConsentData(
            paymentWorkflow.getConsentReference().getEncryptedConsentId(), consentData);
        if (updateAspspConsentDataResponse.getStatusCode() != HttpStatus.OK) {
            throw new PaymentAuthorizeException(
                responseUtils.backToSender(authResp(), paymentWorkflow.getPaymentResponse().getTppNokRedirectUri(),
                                           paymentWorkflow.getPaymentResponse().getTppOkRedirectUri(),
                                           httpResp, updateAspspConsentDataResponse.getStatusCode(), ValidationCode.CONSENT_DATA_UPDATE_FAILED));
        }
    }

    PaymentAuthorizeResponse authResp() {
        return new PaymentAuthorizeResponse();
    }

    private void updateCmsPaymentStatus(HttpServletResponse response, PaymentWorkflow paymentWorkflow)
        throws PaymentAuthorizeException {
        ResponseEntity<Void> updatePaymentStatusResponse = cmsPsuPisClient.updatePaymentStatus(
            paymentWorkflow.getPaymentResponse().getPayment().getPaymentId(), paymentWorkflow.getPaymentStatus(), CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);
        paymentWorkflow.getAuthResponse().updatePaymentStatus(TransactionStatusTO.valueOf(paymentWorkflow.getPaymentStatus()));

        if (updatePaymentStatusResponse.getStatusCode() != HttpStatus.OK) {
            throw new PaymentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), "Could not set payment status. See status code.", updatePaymentStatusResponse.getStatusCode(), response));
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

        PaymentWorkflow cancellationWorkflow = new PaymentWorkflow(cmsPaymentResponse, consentReference);

        Object convertedPaymentTO = convertPayment(response, cancellationWorkflow.paymentType(), cmsPaymentResponse);

        cancellationWorkflow.setAuthResponse(new PaymentAuthorizeResponse(cancellationWorkflow.paymentType(), convertedPaymentTO));
        cancellationWorkflow.getAuthResponse().setAuthorisationId(cmsPaymentResponse.getAuthorisationId());
        cancellationWorkflow.getAuthResponse().setEncryptedConsentId(encryptedPaymentId);
        
        if (bearerToken != null) {
            SCAPaymentResponseTO scaPaymentResponseTO = new SCAPaymentResponseTO();
            scaPaymentResponseTO.setBearerToken(bearerToken);
            cancellationWorkflow.setScaResponse(scaPaymentResponseTO);
        }
        
        return cancellationWorkflow;
    }

    private void updateAuthorisationStatus(PaymentWorkflow cancellationWorkflow, String psuId, HttpServletResponse response) throws PaymentAuthorizeException {
        String paymentId = cancellationWorkflow.getPaymentResponse().getPayment().getPaymentId();
        String authorisationId = cancellationWorkflow.getPaymentResponse().getAuthorisationId();
        String status = cancellationWorkflow.getAuthResponse().getScaStatus().name();

        ResponseEntity<Void> cmsResponse = cmsPsuPisClient.updateAuthorisationStatus(psuId, null, null, null,
                                                                              paymentId, authorisationId, status, CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);

        if (cmsResponse.getStatusCode() != HttpStatus.OK) {
            throw new PaymentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), "Error updating authorisation status. See error code.", cmsResponse.getStatusCode(), response));
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private CmsPaymentResponse loadPaymentByRedirectId(String psuId,
                                                       ConsentReference consentReference, HttpServletResponse response) throws PaymentAuthorizeException {

        ResponseEntity<CmsPaymentResponse> responseEntity = cmsPsuPisClient.getPaymentByRedirectIdForCancellation(
            psuId, null, null, null, consentReference.getRedirectId(), CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID);

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            return responseEntity.getBody();
        } else if (statusCode == HttpStatus.NOT_FOUND) {
            throw new PaymentAuthorizeException(responseUtils.requestWithRedNotFound(authResp(), response));
        } else if (statusCode == HttpStatus.REQUEST_TIMEOUT) {

            CmsPaymentResponse payment = responseEntity.getBody();
            String location = StringUtils.isNotBlank(payment.getTppNokRedirectUri())
                                  ? payment.getTppNokRedirectUri()
                                  : payment.getTppOkRedirectUri();

            throw new PaymentAuthorizeException(responseUtils.redirect(location, response));
        }
        throw new PaymentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), statusCode, response));
    }

    @Override
    public String getBasePath() {
        return BASE_PATH;
    }


    private SCAPaymentResponseTO selectMethod(String scaMethodId, final PaymentWorkflow cancellationWorkflow) {
        try {
            authInterceptor.setAccessToken(cancellationWorkflow.bearerToken().getAccess_token());

            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.selecCancelPaymentSCAtMethod(cancellationWorkflow.paymentId(), cancellationWorkflow.authId(), scaMethodId).getBody();
            processPaymentResponse(cancellationWorkflow, paymentResponseTO);
            return paymentResponseTO;
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    private void processPaymentResponse(PaymentWorkflow paymentWorkflow, SCAPaymentResponseTO paymentResponse) {
        processSCAResponse(paymentWorkflow, paymentResponse);
        paymentWorkflow.setPaymentStatus(paymentResponse.getTransactionStatus().name());
    }

    private void processSCAResponse(PaymentWorkflow cancellationWorkflow, SCAResponseTO paymentResponse) {
        cancellationWorkflow.setScaResponse(paymentResponse);
        cancellationWorkflow.getAuthResponse().setAuthorisationId(paymentResponse.getAuthorisationId());
        cancellationWorkflow.getAuthResponse().setScaStatus(paymentResponse.getScaStatus());
        cancellationWorkflow.getAuthResponse().setScaMethods(paymentResponse.getScaMethods());
        cancellationWorkflow.setAuthCodeMessage(paymentResponse.getPsuMessage());
    }

}

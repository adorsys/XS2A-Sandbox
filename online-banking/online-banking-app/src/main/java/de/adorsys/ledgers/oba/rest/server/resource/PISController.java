package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.api.resource.PISApi;
import de.adorsys.ledgers.oba.rest.server.mapper.PaymentConverter;
import de.adorsys.ledgers.oba.rest.server.service.CommonPaymentService;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController(PISController.BASE_PATH)
@RequestMapping(PISController.BASE_PATH)
@Api(value = PISController.BASE_PATH, tags = "PSU PIS", description = "Provides access to online banking payment functionality")
public class PISController extends AbstractXISController implements PISApi {
    @Autowired
    private PaymentRestClient paymentRestClient;
    @Autowired
    private CommonPaymentService paymentService;
    @Autowired
    private PaymentConverter paymentConverter;

    @Override
    public ResponseEntity<AuthorizeResponse> pisAuth(String redirectId, String encryptedPaymentId) {
        return auth(redirectId, ConsentType.PIS, encryptedPaymentId, response);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(
        String encryptedPaymentId,
        String authorisationId,
        String login,
        String pin,
        String consentCookieString) throws PaymentAuthorizeException {

        PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, false, consentCookieString, login, response, null);

        // Authorize
        boolean success = paymentService.loginForPaymentOperation(login, pin, workflow, OpTypeTO.PAYMENT);

        if (success) {
            String psuId = AuthUtils.psuId(workflow.bearerToken());
            try {
                paymentService.updateAuthorisationStatus(workflow, psuId, response);
                initiatePayment(workflow, response);
                paymentService.updateScaStatusPaymentStatusConsentData(psuId, workflow, response);
            } catch (PaymentAuthorizeException e) {
                return e.getError();
            }
            return paymentService.resolvePaymentWorkflow(workflow, response);
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
            PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());

            // Update status
            workflow.getScaResponse().setScaStatus(ScaStatusTO.PSUAUTHENTICATED);
            paymentService.updateAuthorisationStatus(workflow, psuId, response);

            initiatePayment(workflow, response);
            paymentService.updateScaStatusPaymentStatusConsentData(psuId, workflow, response);

            // Store result in token.
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> selectMethod(String encryptedPaymentId, String authorisationId,
                                                                 String scaMethodId, String consentAndaccessTokenCookieString) {
        return paymentService.selectScaForPayment(encryptedPaymentId, authorisationId, scaMethodId, consentAndaccessTokenCookieString, response, false);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authrizedPayment(
        String encryptedPaymentId,
        String authorisationId,
        String consentAndaccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.psuId(auth);
        try {
            PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            SCAPaymentResponseTO scaPaymentResponse = paymentRestClient.authorizePayment(workflow.paymentId(), workflow.authId(), authCode).getBody();
            paymentService.processPaymentResponse(workflow, scaPaymentResponse);

            paymentService.updateScaStatusPaymentStatusConsentData(psuId, workflow, response);

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
            PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, cookieString, psuId, response, auth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            workflow.getScaResponse().setScaStatus(ScaStatusTO.FAILED);
            paymentService.updateAuthorisationStatus(workflow, psuId, response);
            paymentService.updateAspspConsentData(workflow, response);

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
                                                            String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp) throws PaymentAuthorizeException {
        return paymentService.resolveRedirectUrl(encryptedPaymentId, authorisationId, consentAndAccessTokenCookieString, response);
    }

    @Override
    public String getBasePath() {
        return BASE_PATH;
    }

    private void initiatePayment(final PaymentWorkflow paymentWorkflow, HttpServletResponse response) throws PaymentAuthorizeException {
        CmsPaymentResponse paymentResponse = paymentWorkflow.getPaymentResponse();
        Object payment = paymentConverter.convertPayment(response, paymentWorkflow.paymentType(), paymentResponse);
        try {
            authInterceptor.setAccessToken(paymentWorkflow.bearerToken().getAccess_token());
            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.initiatePayment(paymentWorkflow.paymentType(), payment).getBody();
            paymentService.processPaymentResponse(paymentWorkflow, paymentResponseTO);
        } catch (FeignException f) {
            paymentWorkflow.setErrorCode(HttpStatus.valueOf(f.status()));
            throw f;
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

}

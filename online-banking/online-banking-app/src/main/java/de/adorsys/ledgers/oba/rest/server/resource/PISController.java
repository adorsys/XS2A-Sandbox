package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.PISApi;
import de.adorsys.ledgers.oba.rest.api.resource.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.ledgers.oba.service.impl.mapper.PaymentConverter;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AuthorizeResponse> pisAuth(String redirectId, String encryptedPaymentId, String token) {
        return auth(redirectId, ConsentType.PIS, encryptedPaymentId, response);
    }

    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(String encryptedPaymentId, String authorisationId, String login, String pin, String consentCookieString) throws PaymentAuthorizeException {
        String consentCookie = responseUtils.consentCookie(consentCookieString);
        PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, false, consentCookie, login, null);

        // Authorize
        ResponseEntity<SCALoginResponseTO> loginResult = performLoginForConsent(login, pin, workflow.paymentId(), workflow.authId(), OpTypeTO.PAYMENT);
        AuthUtils.checkIfUserInitiatedOperation(loginResult, workflow.getPaymentResponse().getPayment().getPsuIdDatas());
        workflow.processSCAResponse(loginResult.getBody());

        if (!AuthUtils.success(loginResult)) {
            // failed Message. No repeat. Delete cookies.
            responseUtils.removeCookies(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String psuId = AuthUtils.psuId(workflow.bearerToken());
        initiatePayment(workflow);
        paymentService.updateScaStatusPaymentStatusConsentData(psuId, workflow);
        return resolvePaymentWorkflow(workflow, response);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> initiatePayment(
        String encryptedPaymentId, String authorisationId, String consentAndaccessTokenCookieString) {

        try {
            String psuId = AuthUtils.psuId(middlewareAuth);
            // Identity the link and load the workflow.
            String consentCookie = responseUtils.consentCookie(consentAndaccessTokenCookieString);
            PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentCookie, psuId, middlewareAuth.getBearerToken());

            // Update status
            workflow.getScaResponse().setScaStatus(ScaStatusTO.PSUAUTHENTICATED);

            initiatePayment(workflow);
            paymentService.updateScaStatusPaymentStatusConsentData(psuId, workflow);

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
        PaymentWorkflow workflow;
        try {
            String consentCookie = responseUtils.consentCookie(consentAndaccessTokenCookieString);
            workflow = paymentService.selectScaForPayment(encryptedPaymentId, authorisationId, scaMethodId, consentCookie, false, AuthUtils.psuId(middlewareAuth), middlewareAuth.getBearerToken());
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
        } catch (PaymentAuthorizeException p) {
            return p.getError();
        }
        return ResponseEntity.ok(workflow.getAuthResponse());
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authrizedPayment(
        String encryptedPaymentId,
        String authorisationId,
        String consentAndaccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            String consentCookie = responseUtils.consentCookie(consentAndaccessTokenCookieString);
            PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentCookie, psuId, middlewareAuth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            SCAPaymentResponseTO scaPaymentResponse = paymentRestClient.authorizePayment(workflow.paymentId(), workflow.authId(), authCode).getBody();
            paymentService.processPaymentResponse(workflow, scaPaymentResponse);

            paymentService.updateScaStatusPaymentStatusConsentData(psuId, workflow);

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
        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            String consentCookie = responseUtils.consentCookie(cookieString);
            PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentCookie, psuId, middlewareAuth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            workflow.getScaResponse().setScaStatus(ScaStatusTO.FAILED);
            paymentService.updateAspspConsentData(workflow);

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
                                                            String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp, boolean isOauth2Integrated) throws PaymentAuthorizeException {
        String psuId = AuthUtils.psuId(middlewareAuth);
        String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);
        String redirectUrl = paymentService.resolveRedirectUrl(encryptedPaymentId, authorisationId, consentCookie, isOauth2Integrated, psuId, middlewareAuth.getBearerToken());
        return responseUtils.redirect(redirectUrl, response);
    }

    @Override
    public String getBasePath() {
        return BASE_PATH;
    }

    private void initiatePayment(final PaymentWorkflow paymentWorkflow) {
        CmsPaymentResponse paymentResponse = paymentWorkflow.getPaymentResponse();
        Object payment = paymentConverter.convertPayment(paymentWorkflow.paymentType(), paymentResponse);
        authInterceptor.setAccessToken(paymentWorkflow.bearerToken().getAccess_token());
        SCAPaymentResponseTO paymentResponseTO = paymentRestClient.initiatePayment(paymentWorkflow.paymentType(), payment).getBody();
        paymentService.processPaymentResponse(paymentWorkflow, paymentResponseTO);
    }
}

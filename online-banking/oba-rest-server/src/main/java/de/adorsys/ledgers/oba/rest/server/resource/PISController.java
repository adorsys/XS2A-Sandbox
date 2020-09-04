package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.rest.api.resource.PISApi;
import de.adorsys.ledgers.oba.rest.api.resource.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static de.adorsys.ledgers.oba.rest.api.resource.PISApi.BASE_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_PATH)
@Api(value = BASE_PATH, tags = "PSU PIS. Provides access to online banking payment functionality")
public class PISController implements PISApi {
    private final CommonPaymentService paymentService;
    private final XISControllerService xisService;
    private final HttpServletResponse response;
    private final ResponseUtils responseUtils;
    private final ObaMiddlewareAuthentication middlewareAuth;
    private final AuthRequestInterceptor authInterceptor;
    private final TokenAuthenticationService authenticationService;

    @Override
    public ResponseEntity<AuthorizeResponse> pisAuth(String redirectId, String encryptedPaymentId, String token) {
        return xisService.auth(redirectId, ConsentType.PIS, encryptedPaymentId, response);
    }

    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(String encryptedPaymentId, String authorisationId, String login, String pin, String consentCookieString) {
        String consentCookie = responseUtils.consentCookie(consentCookieString);
        PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, false, consentCookie, login, null);

        // Authorize
        GlobalScaResponseTO ledgersResponse = null;
        try {
            ledgersResponse = authenticationService.login(login, pin, authorisationId);
            workflow.processSCAResponse(ledgersResponse);
            AuthUtils.checkIfUserInitiatedOperation(ledgersResponse, workflow.getPaymentResponse().getPayment().getPsuIdDatas());
            workflow = paymentService.initiatePaymentOpr(workflow, workflow.bearerToken().getAccessTokenObject().getLogin(), OpTypeTO.PAYMENT);
        } catch (FeignException e) {
            log.error("Failed to Login user: {}, pass {}", login, pin);
        }

        if (ledgersResponse == null) {
            // failed Message. No repeat. Delete cookies.
            responseUtils.removeCookies(this.response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return xisService.resolvePaymentWorkflow(workflow);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> initiatePayment( //TODO Seems to be unused!!! Subject to removal!
                                                                     String encryptedPaymentId, String authorisationId, String consentAndaccessTokenCookieString) {

        try {
            String psuId = AuthUtils.psuId(middlewareAuth);
            // Identity the link and load the workflow.
            String consentCookie = responseUtils.consentCookie(consentAndaccessTokenCookieString);
            PaymentWorkflow identifyPaymentWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentCookie, psuId, middlewareAuth.getBearerToken());

            // Update status
            identifyPaymentWorkflow.getScaResponse().setScaStatus(ScaStatusTO.PSUAUTHENTICATED);

            PaymentWorkflow initiatePaymentWorkflow = paymentService.initiatePaymentOpr(identifyPaymentWorkflow, psuId, OpTypeTO.PAYMENT);

            // Store result in token.
            responseUtils.setCookies(response, initiatePaymentWorkflow.getConsentReference(), initiatePaymentWorkflow.bearerToken().getAccess_token(), initiatePaymentWorkflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(initiatePaymentWorkflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> selectMethod(String encryptedPaymentId, String authorisationId, String scaMethodId, String consentAndaccessTokenCookieString) {
        return xisService.selectScaMethod(encryptedPaymentId, authorisationId, scaMethodId, consentAndaccessTokenCookieString);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authrizedPayment(String encryptedPaymentId, String authorisationId, String consentAndaccessTokenCookieString, String authCode) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            String consentCookie = responseUtils.consentCookie(consentAndaccessTokenCookieString);
            PaymentWorkflow identifyPaymentWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentCookie, psuId, middlewareAuth.getBearerToken());
            PaymentWorkflow authorizePaymentWorkflow = paymentService.authorizePaymentOpr(identifyPaymentWorkflow, psuId, authCode, OpTypeTO.PAYMENT);

            responseUtils.setCookies(response, authorizePaymentWorkflow.getConsentReference(), authorizePaymentWorkflow.bearerToken().getAccess_token(), authorizePaymentWorkflow.bearerToken().getAccessTokenObject());
            log.info("Confirmation code: {}", authorizePaymentWorkflow.getAuthResponse().getAuthConfirmationCode());
            return ResponseEntity.ok(authorizePaymentWorkflow.getAuthResponse());
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
                                                            String consentAndAccessTokenCookieString, boolean isOauth2Integrated, String authConfirmationCode) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);
        String redirectUrl = paymentService.resolveRedirectUrl(encryptedPaymentId, authorisationId, consentCookie, isOauth2Integrated, psuId, middlewareAuth.getBearerToken(), authConfirmationCode);
        return responseUtils.redirect(redirectUrl, response);
    }
}

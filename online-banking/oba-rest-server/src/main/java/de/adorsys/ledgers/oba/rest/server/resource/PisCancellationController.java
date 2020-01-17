package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.oba.rest.api.resource.PisCancellationApi;
import de.adorsys.ledgers.oba.rest.api.resource.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static de.adorsys.ledgers.oba.rest.api.resource.PisCancellationApi.BASE_PATH;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
@RestController
@RequestMapping(BASE_PATH)
@Api(value = BASE_PATH, tags = "PSU PIS Cancellation. Provides access to online banking payment functionality")
public class PisCancellationController extends AbstractXISController implements PisCancellationApi {
    private final CommonPaymentService paymentService;

    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(
        String encryptedPaymentId,
        String authorisationId,
        String login,
        String pin,
        String consentCookieString) {

        String consentCookie = responseUtils.consentCookie(consentCookieString);
        PaymentWorkflow cancellationWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, false, consentCookie, login, null);
        if (cancellationWorkflow.getPaymentStatus().equals(TransactionStatusTO.RCVD.name())) {
            throw AuthorizationException.builder()
                      .devMessage(String.format("Cancellation of Payment id: %s is not possible thought OnlineBanking as it's status is RECEIVED, cancellation of this payment is only possible though EMBEDDED route", encryptedPaymentId))
                      .errorCode(AuthErrorCode.LOGIN_FAILED)
                      .build();
        }
        // Authorize
        ResponseEntity<SCALoginResponseTO> loginResult = performLoginForConsent(login, pin, cancellationWorkflow.paymentId(), cancellationWorkflow.authId(), OpTypeTO.CANCEL_PAYMENT);
        AuthUtils.checkIfUserInitiatedOperation(loginResult, cancellationWorkflow.getPaymentResponse().getPayment().getPsuIdDatas());
        cancellationWorkflow.processSCAResponse(Objects.requireNonNull(loginResult.getBody()));

        if (!AuthUtils.success(loginResult)) {
            responseUtils.removeCookies(response);
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        String psuId = AuthUtils.psuId(cancellationWorkflow.bearerToken());
        PaymentWorkflow initiateCancelPaymentWorkflow = paymentService.initiateCancelPayment(cancellationWorkflow, psuId);
        return resolvePaymentWorkflow(initiateCancelPaymentWorkflow);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> selectMethod(String encryptedPaymentId, String authorisationId,
                                                                 String scaMethodId, String consentAndAccessTokenCookieString) {
        PaymentWorkflow workflow;
        try {
            String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);
            workflow = paymentService.selectScaForPayment(encryptedPaymentId, authorisationId, scaMethodId, consentCookie, true, AuthUtils.psuId(middlewareAuth), middlewareAuth.getBearerToken());
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
        } catch (PaymentAuthorizeException p) {
            return p.getError();
        }
        return ResponseEntity.ok(workflow.getAuthResponse());
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authorisePayment(String encryptedPaymentId, String authorisationId,
                                                                     String consentAndAccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);

            PaymentWorkflow identifyPaymentWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentCookie, psuId, middlewareAuth.getBearerToken());
            PaymentWorkflow authorizeCancelPaymentWorkflow = paymentService.authorizeCancelPayment(identifyPaymentWorkflow, psuId, authCode);

            responseUtils.setCookies(response, authorizeCancelPaymentWorkflow.getConsentReference(), authorizeCancelPaymentWorkflow.bearerToken().getAccess_token(), authorizeCancelPaymentWorkflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(authorizeCancelPaymentWorkflow.getAuthResponse());
        } catch (PaymentAuthorizeException e) {
            return e.getError();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> pisDone(String encryptedPaymentId, String authorisationId,
                                                            String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp, boolean isOauth2Integrated) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);
        String redirectUrl = paymentService.resolveRedirectUrl(encryptedPaymentId, authorisationId, consentCookie, isOauth2Integrated, psuId, middlewareAuth.getBearerToken());
        return responseUtils.redirect(redirectUrl, response);
    }

    @Override
    public String getBasePath() {
        return BASE_PATH;
    }
}

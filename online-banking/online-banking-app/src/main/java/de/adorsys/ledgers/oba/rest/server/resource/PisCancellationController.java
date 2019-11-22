package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.exception.AuthErrorCode;
import de.adorsys.ledgers.oba.rest.api.exception.AuthorizationException;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.api.resource.PisCancellationApi;
import de.adorsys.ledgers.oba.rest.server.service.CommonPaymentService;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.valueOf;

@RestController(PisCancellationController.BASE_PATH)
@RequestMapping(PisCancellationController.BASE_PATH)
@Api(value = PisCancellationController.BASE_PATH, tags = "PSU PIS Cancellation", description = "Provides access to online banking payment functionality")
public class PisCancellationController extends AbstractXISController implements PisCancellationApi {
    @Autowired
    private PaymentRestClient paymentRestClient;
    @Autowired
    private CommonPaymentService paymentService;

    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(
        String encryptedPaymentId,
        String authorisationId,
        String login,
        String pin,
        String consentCookieString) throws PaymentAuthorizeException {

        PaymentWorkflow cancellationWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, false, consentCookieString, login, response, null);
        if (cancellationWorkflow.getPaymentStatus().equals(TransactionStatusTO.RCVD.name())) {
            throw AuthorizationException.builder()
                      .devMessage(String.format("Cancellation of Payment id: %s is not possible thought OnlineBanking as it's status is RECEIVED, cancellation of this payment is only possible though EMBEDDED route", encryptedPaymentId))
                      .errorCode(AuthErrorCode.LOGIN_FAILED)
                      .build();
        }
        // Authorize
        ResponseEntity<SCALoginResponseTO> loginResult = performLoginForConsent(login, pin, cancellationWorkflow.paymentId(), cancellationWorkflow.authId(), OpTypeTO.CANCEL_PAYMENT);
        AuthUtils.checkIfUserInitiatedOperation(loginResult, cancellationWorkflow.getPaymentResponse().getPayment().getPsuIdDatas());
        processSCAResponse(cancellationWorkflow, loginResult.getBody());

        if (!AuthUtils.success(loginResult)) {
            responseUtils.removeCookies(response);
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        initiateCancelPayment(cancellationWorkflow);
        String psuId = AuthUtils.psuId(cancellationWorkflow.bearerToken());
        paymentService.updateScaStatusPaymentStatusConsentData(psuId, cancellationWorkflow, response);
        return paymentService.resolvePaymentWorkflow(cancellationWorkflow, response);
    }

    private void processSCAResponse(PaymentWorkflow workflow, SCAResponseTO paymentResponse) {
        workflow.setScaResponse(paymentResponse);
        workflow.getAuthResponse().setAuthorisationId(paymentResponse.getAuthorisationId());
        workflow.getAuthResponse().setScaStatus(paymentResponse.getScaStatus());
        workflow.getAuthResponse().setScaMethods(paymentResponse.getScaMethods());
        workflow.setAuthCodeMessage(paymentResponse.getPsuMessage());
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> selectMethod(String encryptedPaymentId, String authorisationId,
                                                                 String scaMethodId, String consentAndAccessTokenCookieString) {
        return paymentService.selectScaForPayment(encryptedPaymentId, authorisationId, scaMethodId, consentAndAccessTokenCookieString, response, true);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authorisePayment(String encryptedPaymentId, String authorisationId,
                                                                     String consentAndAccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            PaymentWorkflow cancellationWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, response, middlewareAuth.getBearerToken());

            authInterceptor.setAccessToken(cancellationWorkflow.bearerToken().getAccess_token());

            SCAPaymentResponseTO scaPaymentResponse = paymentRestClient.authorizeCancelPayment(cancellationWorkflow.paymentId(), cancellationWorkflow.authId(), authCode).getBody();
            paymentService.processPaymentResponse(cancellationWorkflow, scaPaymentResponse);

            cancellationWorkflow.setPaymentStatus(TransactionStatusTO.CANC.toString());
            paymentService.updateScaStatusPaymentStatusConsentData(psuId, cancellationWorkflow, response);

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
                                                            String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp, boolean isOauth2Integrated) throws PaymentAuthorizeException {

        return paymentService.resolveRedirectUrl(encryptedPaymentId, authorisationId, consentAndAccessTokenCookieString, response, isOauth2Integrated);
    }

    private void initiateCancelPayment(final PaymentWorkflow paymentWorkflow) {
        try {
            authInterceptor.setAccessToken(paymentWorkflow.bearerToken().getAccess_token());
            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.initiatePmtCancellation(paymentWorkflow.paymentId()).getBody();
            paymentService.processPaymentResponse(paymentWorkflow, paymentResponseTO);
        } catch (FeignException f) {
            paymentWorkflow.setErrorCode(valueOf(f.status()));
            throw f;
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public String getBasePath() {
        return BASE_PATH;
    }
}

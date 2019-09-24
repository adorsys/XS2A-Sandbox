package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.api.resource.PisCancellationApi;
import de.adorsys.ledgers.oba.rest.server.service.CommonPaymentService;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//TODO refactor and write tests https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/33
@RestController(PisCancellationController.BASE_PATH)
@RequestMapping(PisCancellationController.BASE_PATH)
@Api(value = PisCancellationController.BASE_PATH, tags = "PSU PIS Cancellation", description = "Provides access to online banking payment functionality")
public class PisCancellationController extends AbstractXISController implements PisCancellationApi {
    @Autowired
    private PaymentRestClient paymentRestClient;
    @Autowired
    private CommonPaymentService paymentService;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(
        String encryptedPaymentId,
        String authorisationId,
        String login,
        String pin,
        String consentCookieString) throws PaymentAuthorizeException {

        PaymentWorkflow cancellationWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, false, consentCookieString, login, response, null);

        // Authorize
        boolean success = paymentService.loginForPaymentOperation(login, pin, cancellationWorkflow, OpTypeTO.CANCEL_PAYMENT);

        if (success) {
            String psuId = AuthUtils.psuId(cancellationWorkflow.bearerToken());
            try {
                paymentService.updateAuthorisationStatus(cancellationWorkflow, psuId, response);
                initiateCancelPayment(cancellationWorkflow);

                if (cancellationWorkflow.singleScaMethod()) {
                    ScaUserDataTO scaUserDataTO = cancellationWorkflow.scaMethods().iterator().next();
                    paymentService.selectMethod(scaUserDataTO.getId(), cancellationWorkflow, true);
                }

                paymentService.updateScaStatusPaymentStatusConsentData(psuId, cancellationWorkflow, response);
            } catch (PaymentAuthorizeException e) {
                return e.getError();
            }
            return paymentService.resolvePaymentWorkflow(cancellationWorkflow, response);
        } else {
            responseUtils.removeCookies(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> selectMethod(String encryptedPaymentId, String authorisationId,
                                                                 String scaMethodId, String consentAndAccessTokenCookieString) {
        return paymentService.selectScaForPayment(encryptedPaymentId, authorisationId, scaMethodId, consentAndAccessTokenCookieString, response, true);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authorisePayment(String encryptedPaymentId, String authorisationId,
                                                                     String consentAndAccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.psuId(auth);
        try {
            PaymentWorkflow cancellationWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, true, consentAndAccessTokenCookieString, psuId, response, auth.getBearerToken());

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
                                                            String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp) throws PaymentAuthorizeException {

        return paymentService.resolveRedirectUrl(encryptedPaymentId, authorisationId, consentAndAccessTokenCookieString, response);
    }

    private void initiateCancelPayment(final PaymentWorkflow paymentWorkflow) {
        try {
            authInterceptor.setAccessToken(paymentWorkflow.bearerToken().getAccess_token());
            SCAPaymentResponseTO paymentResponseTO = paymentRestClient.initiatePmtCancellation(paymentWorkflow.paymentId()).getBody();
            paymentService.processPaymentResponse(paymentWorkflow, paymentResponseTO);
        } catch (FeignException f) {
            paymentWorkflow.setErrorCode(HttpStatus.valueOf(f.status()));
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

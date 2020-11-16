package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.StartScaOprTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaCancellationApi;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.EXEMPTED;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.FINALISED;
import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.CANC;
import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID;

@Slf4j
@RestController
@RequestMapping(ObaCancellationApi.BASE_PATH)
@RequiredArgsConstructor
public class ObaCancellationController implements ObaCancellationApi {
    private final CmsPsuPisService cmsPsuPisService;
    private final PaymentRestClient paymentRestClient;
    private final RedirectScaRestClient redirectScaRestClient;
    private final AuthRequestInterceptor auth;

    @Override
    public ResponseEntity<SCAPaymentResponseTO> initCancellation(String paymentId) {
        SCAPaymentResponseTO response = paymentRestClient.initiatePmtCancellation(paymentId).getBody();
        HttpStatus status = resolveStatus(paymentId, response);
        return new ResponseEntity<>(response, status);
    }

    @Override
    public ResponseEntity<SCAPaymentResponseTO> selectSca(String paymentId, String cancellationId, String scaMethodId) {
        StartScaOprTO opr = new StartScaOprTO(paymentId, null,cancellationId, OpTypeTO.CANCEL_PAYMENT);
        redirectScaRestClient.startSca(opr);
        return ResponseEntity.ok(mapToPaymentResponse(redirectScaRestClient.selectMethod(cancellationId, scaMethodId).getBody()));
    }

    private SCAPaymentResponseTO mapToPaymentResponse(GlobalScaResponseTO source) {
        SCAPaymentResponseTO target = new SCAPaymentResponseTO();
        /*target.setTransactionStatus(null);
        target.setPaymentProduct(null);
        target.setPaymentType(null);
        target.setChosenScaMethod(null);*/ //TODO This are missed as irrelevant
        target.setPaymentId(source.getOperationObjectId());
        target.setScaStatus(source.getScaStatus());
        target.setAuthorisationId(source.getAuthorisationId());
        target.setScaMethods(source.getScaMethods());
        target.setChallengeData(source.getChallengeData());
        target.setPsuMessage(source.getPsuMessage());
        target.setStatusDate(source.getStatusDate());
        target.setExpiresInSeconds(source.getExpiresInSeconds());
        target.setMultilevelScaRequired(source.isMultilevelScaRequired());
        target.setAuthConfirmationCode(source.getAuthConfirmationCode());
        target.setBearerToken(source.getBearerToken());
        target.setObjectType(source.getOpType().name());
        return target;
    }

    @Override
    public ResponseEntity<Void> validateTAN(String paymentId, String cancellationId, String authCode) {
        GlobalScaResponseTO validateScaCode = redirectScaRestClient.validateScaCode(cancellationId, authCode).getBody();
        auth.setAccessToken(validateScaCode.getBearerToken().getAccess_token());
        SCAPaymentResponseTO response = paymentRestClient.executeCancelPayment(paymentId).getBody();
        HttpStatus status = resolveStatus(paymentId, response);
        return new ResponseEntity<>(status);
    }

    private HttpStatus resolveStatus(String paymentId, SCAPaymentResponseTO response) {
        if (EnumSet.of(EXEMPTED, FINALISED).contains(response.getScaStatus())) {
            return cmsPsuPisService.updatePaymentStatus(paymentId, CANC, DEFAULT_SERVICE_INSTANCE_ID)
                       ?HttpStatus.NO_CONTENT
                       : HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }
}

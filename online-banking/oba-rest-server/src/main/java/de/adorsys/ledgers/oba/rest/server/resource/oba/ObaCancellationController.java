package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaCancellationApi;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.*;
import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.CANC;
import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping(ObaCancellationApi.BASE_PATH)
@RequiredArgsConstructor
public class ObaCancellationController implements ObaCancellationApi {
    private final CmsPsuPisService cmsPsuPisService;
    private final PaymentRestClient paymentRestClient;

    @Override
    public ResponseEntity<SCAPaymentResponseTO> initCancellation(String paymentId) {
        SCAPaymentResponseTO response = paymentRestClient.initiatePmtCancellation(paymentId).getBody();
        HttpStatus status = resolveStatus(paymentId, response);
        return new ResponseEntity<>(response, status);
    }

    @Override
    public ResponseEntity<SCAPaymentResponseTO> selectSca(String paymentId, String cancellationId, String scaMethodId) {
        return paymentRestClient.selecCancelPaymentSCAtMethod(paymentId, cancellationId, scaMethodId);
    }

    @Override
    public ResponseEntity<Void> validateTAN(String paymentId, String cancellationId, String authCode) {
        SCAPaymentResponseTO response = paymentRestClient.authorizeCancelPayment(paymentId, cancellationId, authCode).getBody();
        HttpStatus status = resolveStatus(paymentId, response);
        return new ResponseEntity<>(status);
    }

    private HttpStatus resolveStatus(String paymentId, SCAPaymentResponseTO response) {
        if (EnumSet.of(EXEMPTED, FINALISED).contains(response.getScaStatus())) {
            return cmsPsuPisService.updatePaymentStatus(paymentId, CANC, DEFAULT_SERVICE_INSTANCE_ID)
                       ? NO_CONTENT
                       : BAD_REQUEST;
        }
        return OK;
    }
}

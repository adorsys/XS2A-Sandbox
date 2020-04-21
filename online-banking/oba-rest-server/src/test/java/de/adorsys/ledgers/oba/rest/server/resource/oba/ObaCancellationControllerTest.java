package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ObaCancellationControllerTest {
    private static final String PMT_ID = "PMT_ID";
    private static final String SEPA = "sepa-credit-transfers";
    private static final String AUTH_ID = "AUTH_ID";
    private static final String METHOD_ID = "methodId";
    private static final String TAN = "123456";

    @InjectMocks
    private ObaCancellationController controller;

    @Mock
    private CmsPsuPisService cmsPsuPisService;
    @Mock
    private PaymentRestClient paymentRestClient;

    // No Sca set success
    @Test
    void initCancellation_no_sca() {
        // Given
        when(paymentRestClient.initiatePmtCancellation(PMT_ID)).thenReturn(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, false, false, OK));
        when(cmsPsuPisService.updatePaymentStatus(PMT_ID, TransactionStatus.CANC, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(true);

        // When
        ResponseEntity<SCAPaymentResponseTO> result = controller.initCancellation(PMT_ID);

        // Then
        assertEquals(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, false, false, NO_CONTENT), result);
    }

    // One and Many Sca set success
    @Test
    void initCancellation() {
        // Given
        when(paymentRestClient.initiatePmtCancellation(PMT_ID)).thenReturn(getResponse(ScaStatusTO.PSUIDENTIFIED, TransactionStatusTO.ACSP, true, false, OK));

        // When
        ResponseEntity<SCAPaymentResponseTO> result = controller.initCancellation(PMT_ID);

        // Then
        assertEquals(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, true, false, OK), result);
    }

    @Test
    void selectSca() {
        // Given
        when(paymentRestClient.selecCancelPaymentSCAtMethod(PMT_ID, AUTH_ID, METHOD_ID)).thenReturn(getResponse(ScaStatusTO.SCAMETHODSELECTED, TransactionStatusTO.ACSP, true, true, OK));

        // When
        ResponseEntity<SCAPaymentResponseTO> result = controller.selectSca(PMT_ID, AUTH_ID, METHOD_ID);

        // Then
        assertEquals(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, true, true, OK), result);
    }

    @Test
    void validateTAN() {
        // Given
        when(paymentRestClient.authorizeCancelPayment(PMT_ID, AUTH_ID, TAN)).thenReturn(getResponse(ScaStatusTO.FINALISED, TransactionStatusTO.CANC, true, true, NO_CONTENT));
        when(cmsPsuPisService.updatePaymentStatus(PMT_ID, TransactionStatus.CANC, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(true);

        // When
        ResponseEntity<Void> result = controller.validateTAN(PMT_ID, AUTH_ID, TAN);

        // Then
        assertEquals(ResponseEntity.noContent().build(), result);
    }

    // failure
    @Test
    void initCancellation_no_sca_fail() {
        when(paymentRestClient.initiatePmtCancellation(PMT_ID)).thenReturn(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, false, false, OK));
        when(cmsPsuPisService.updatePaymentStatus(PMT_ID, TransactionStatus.CANC, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(false);

        // When
        ResponseEntity<SCAPaymentResponseTO> result = controller.initCancellation(PMT_ID);

        // Then
        assertEquals(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, false, false, BAD_REQUEST), result);
    }

    private ResponseEntity<SCAPaymentResponseTO> getResponse(ScaStatusTO status, TransactionStatusTO transactionStatus, boolean hasSCAs, boolean chosenSCA, HttpStatus expectedStatus) {
        SCAPaymentResponseTO to = new SCAPaymentResponseTO();
        to.setPaymentId(PMT_ID);
        to.setTransactionStatus(transactionStatus);
        to.setPaymentProduct(SEPA);
        to.setPaymentType(PaymentTypeTO.SINGLE);
        to.setScaStatus(status);
        to.setAuthorisationId(AUTH_ID);
        to.setScaMethods(hasSCAs ? Collections.singletonList(getScaMethod()) : Collections.emptyList());
        to.setChosenScaMethod(chosenSCA ? getScaMethod() : null);
        to.setChallengeData(null);
        to.setPsuMessage(null);
        to.setStatusDate(LocalDateTime.of(2020, 1, 28, 0, 0));
        to.setExpiresInSeconds(600);
        to.setMultilevelScaRequired(false);
        to.setAuthConfirmationCode(null);
        to.setBearerToken(null);
        return new ResponseEntity<>(to, expectedStatus);
    }

    private ScaUserDataTO getScaMethod() {
        return new ScaUserDataTO(METHOD_ID, ScaMethodTypeTO.EMAIL, "anton.brueckner@adorsys.de", null, false, null, false, false);
    }
}

/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.StartScaOprTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.OperationInitiationRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import static de.adorsys.psd2.consent.aspsp.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ObaCancellationControllerTest {
    private static final String PMT_ID = "PMT_ID";
    private static final String AUTH_ID = "AUTH_ID";
    private static final String METHOD_ID = "methodId";
    private static final String TAN = "123456";

    @InjectMocks
    private ObaCancellationController controller;

    @Mock
    private CmsPsuPisService cmsPsuPisService;
    @Mock
    private OperationInitiationRestClient operationInitiationRestClient;
    @Mock
    private RedirectScaRestClient redirectScaRestClient;
    @Mock
    private AuthRequestInterceptor auth;

    // No Sca set success
    @Test
    void initCancellation_no_sca() {
        // Given
        when(operationInitiationRestClient.initiatePmtCancellation(PMT_ID)).thenReturn(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, false, false, OK));
        when(cmsPsuPisService.updatePaymentStatus(PMT_ID, TransactionStatus.CANC, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(true);

        // When
        ResponseEntity<GlobalScaResponseTO> result = controller.initCancellation(PMT_ID);

        // Then
        assertEquals(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, false, false, NO_CONTENT), result);
    }

    // One and Many Sca set success
    @Test
    void initCancellation() {
        // Given
        when(operationInitiationRestClient.initiatePmtCancellation(PMT_ID)).thenReturn(getResponse(ScaStatusTO.PSUIDENTIFIED, TransactionStatusTO.ACSP, true, false, OK));

        // When
        ResponseEntity<GlobalScaResponseTO> result = controller.initCancellation(PMT_ID);

        // Then
        assertEquals(getResponse(ScaStatusTO.PSUIDENTIFIED, TransactionStatusTO.ACSP, true, false, OK), result);
    }

    @Test
    void selectSca() {
        // Given
        ArgumentCaptor<StartScaOprTO> startScaOprTOCaptor = ArgumentCaptor.forClass(StartScaOprTO.class);
        when(redirectScaRestClient.selectMethod(AUTH_ID, METHOD_ID)).thenReturn(ResponseEntity.ok(getGlobalResponse()));
        // When
        ResponseEntity<GlobalScaResponseTO> result = controller.selectSca(PMT_ID, AUTH_ID, METHOD_ID);

        // Then
        verify(redirectScaRestClient, times(1)).startSca(startScaOprTOCaptor.capture());
        verify(redirectScaRestClient, times(1)).selectMethod(AUTH_ID, METHOD_ID);

        assertNotNull(result.getBody());

        assertEquals(PMT_ID, startScaOprTOCaptor.getValue().getOprId());
        assertEquals(AUTH_ID, startScaOprTOCaptor.getValue().getAuthorisationId());
        assertEquals(OpTypeTO.CANCEL_PAYMENT, startScaOprTOCaptor.getValue().getOpType());
        assertNull(startScaOprTOCaptor.getValue().getExternalId());
    }

    private GlobalScaResponseTO getGlobalResponse() {
        GlobalScaResponseTO response = new GlobalScaResponseTO();
        response.setBearerToken(new BearerTokenTO("", "Bearer", 0, "", new AccessTokenTO(), new HashSet<>()));
        response.setScaStatus(ScaStatusTO.SCAMETHODSELECTED);
        response.setOpType(OpTypeTO.CANCEL_PAYMENT);
        response.setOperationObjectId(PMT_ID);
        response.setAuthorisationId(AUTH_ID);
        response.setStatusDate(LocalDateTime.of(2020, 1, 28, 0, 0));
        response.setScaMethods(Collections.singletonList(getScaMethod()));
        response.setExpiresInSeconds(600);
        return response;
    }

    @Test
    void validateTAN() {
        // Given
        when(operationInitiationRestClient.execution(OpTypeTO.CANCEL_PAYMENT, PMT_ID)).thenReturn(getResponse(ScaStatusTO.FINALISED, TransactionStatusTO.CANC, true, true, NO_CONTENT));
        when(cmsPsuPisService.updatePaymentStatus(PMT_ID, TransactionStatus.CANC, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(true);
        when(redirectScaRestClient.validateScaCode(any(), any())).thenReturn(ResponseEntity.ok(getGlobalResponse()));

        // When
        ResponseEntity<Void> result = controller.validateTAN(PMT_ID, AUTH_ID, TAN);

        // Then
        assertEquals(ResponseEntity.noContent().build(), result);
    }

    // failure
    @Test
    void initCancellation_no_sca_fail() {
        when(operationInitiationRestClient.initiatePmtCancellation(PMT_ID)).thenReturn(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, false, false, OK));
        when(cmsPsuPisService.updatePaymentStatus(PMT_ID, TransactionStatus.CANC, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(false);

        // When
        ResponseEntity<GlobalScaResponseTO> result = controller.initCancellation(PMT_ID);

        // Then
        assertEquals(getResponse(ScaStatusTO.EXEMPTED, TransactionStatusTO.ACSP, false, false, BAD_REQUEST), result);
    }

    private ResponseEntity<GlobalScaResponseTO> getResponse(ScaStatusTO status, TransactionStatusTO transactionStatus, boolean hasSCAs, boolean chosenSCA, HttpStatus expectedStatus) {
        GlobalScaResponseTO to = new GlobalScaResponseTO();
        to.setOperationObjectId(PMT_ID);
        to.setTransactionStatus(transactionStatus);
        to.setScaStatus(status);
        to.setAuthorisationId(AUTH_ID);
        to.setOpType(OpTypeTO.CANCEL_PAYMENT);
        to.setScaMethods(hasSCAs ? Collections.singletonList(getScaMethod()) : Collections.emptyList());
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
        return new ScaUserDataTO(METHOD_ID, ScaMethodTypeTO.SMTP_OTP, "anton.brueckner@adorsys.de", null, false, null, false, false);
    }
}

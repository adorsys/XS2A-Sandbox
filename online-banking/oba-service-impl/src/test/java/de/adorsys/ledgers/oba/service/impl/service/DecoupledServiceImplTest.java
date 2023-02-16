/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.Constants;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.OperationInitiationRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.CmsAspspConsentDataService;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import de.adorsys.psd2.xs2a.core.exception.AuthorisationIsExpiredException;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.AUTH_EXPIRED;
import static de.adorsys.psd2.consent.aspsp.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecoupledServiceImplTest {
    @InjectMocks
    private DecoupledServiceImpl service;

    @Mock
    private KeycloakTokenService tokenService;
    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private OperationInitiationRestClient operationInitiationRestClient;
    @Mock
    private RedirectScaRestClient redirectScaClient;
    @Mock
    private CmsPsuPisService cmsPsuPisService;
    @Mock
    private CmsPsuAisClient cmsPsuAisClient;
    @Mock
    private CmsAspspConsentDataService dataService;
    @Mock
    private AspspConsentDataClient aspspConsentDataClient;

    @Test
    void executeDecoupledOpr_pmt() throws AuthorisationIsExpiredException {
        DecoupledConfRequest request = getDecoupledRequest(OpTypeTO.PAYMENT);

        when(tokenService.exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA)).thenReturn(getToken("sca_token"));
        when(redirectScaClient.validateScaCode(request.getAuthorizationId(), "TAN")).thenReturn(getResponse());
        when(operationInitiationRestClient.execution(OpTypeTO.PAYMENT, request.getObjId())).thenReturn(ResponseEntity.ok(getGlobalScaResponse()));

        boolean result = service.executeDecoupledOpr(request, "login_token");
        assertTrue(result);
        verify(tokenService, times(1)).exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA);
        verify(authInterceptor, times(1)).setAccessToken("sca_token");
        verify(redirectScaClient, times(1)).validateScaCode(request.getAuthorizationId(), "TAN");
        verify(authInterceptor, times(1)).setAccessToken("full_token");
        verify(operationInitiationRestClient, times(1)).execution(OpTypeTO.PAYMENT, request.getObjId());
        verify(cmsPsuPisService, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any());
        verify(cmsPsuPisService, times(1)).updatePaymentStatus(any(), any(), any());
        verify(aspspConsentDataClient, times(1)).updateAspspConsentData(any(), any());
        verify(authInterceptor, times(1)).setAccessToken(null);
    }

    @Test
    void executeDecoupledOpr_cancelPmt() throws AuthorisationIsExpiredException {
        DecoupledConfRequest request = getDecoupledRequest(OpTypeTO.CANCEL_PAYMENT);

        when(tokenService.exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA)).thenReturn(getToken("sca_token"));
        when(redirectScaClient.validateScaCode(request.getAuthorizationId(), "TAN")).thenReturn(getResponse());
        when(operationInitiationRestClient.execution(OpTypeTO.CANCEL_PAYMENT, request.getObjId())).thenReturn(ResponseEntity.ok(getGlobalScaResponse()));

        boolean result = service.executeDecoupledOpr(request, "login_token");
        assertTrue(result);
        verify(tokenService, times(1)).exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA);
        verify(authInterceptor, times(1)).setAccessToken("sca_token");
        verify(redirectScaClient, times(1)).validateScaCode(request.getAuthorizationId(), "TAN");
        verify(authInterceptor, times(1)).setAccessToken("full_token");
        verify(operationInitiationRestClient, times(1)).execution(OpTypeTO.CANCEL_PAYMENT, request.getObjId());
        verify(cmsPsuPisService, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any());
        verify(cmsPsuPisService, times(1)).updatePaymentStatus(any(), any(), any());
        verify(aspspConsentDataClient, times(1)).updateAspspConsentData(any(), any());
        verify(authInterceptor, times(1)).setAccessToken(null);
    }

    @Test
    void executeDecoupledOpr_pmtUnconfirmed() throws AuthorisationIsExpiredException {
        DecoupledConfRequest request = getDecoupledRequest(OpTypeTO.PAYMENT);
        request.setConfirmed(false);

        when(tokenService.exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA)).thenReturn(getToken("sca_token"));
        when(redirectScaClient.validateScaCode(request.getAuthorizationId(), "TAN")).thenReturn(getResponse());
        when(operationInitiationRestClient.execution(OpTypeTO.PAYMENT, request.getObjId())).thenReturn(ResponseEntity.ok(getGlobalScaResponse()));

        boolean result = service.executeDecoupledOpr(request, "login_token");
        assertTrue(result);
        verify(tokenService, times(1)).exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA);
        verify(authInterceptor, times(1)).setAccessToken("sca_token");
        verify(redirectScaClient, times(1)).validateScaCode(request.getAuthorizationId(), "TAN");
        verify(authInterceptor, times(1)).setAccessToken("full_token");
        verify(operationInitiationRestClient, times(1)).execution(OpTypeTO.PAYMENT, request.getObjId());
        verify(cmsPsuPisService, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any());
        verify(cmsPsuPisService, times(1)).updatePaymentStatus(any(), any(), any());
        verify(aspspConsentDataClient, times(1)).updateAspspConsentData(any(), any());
        verify(authInterceptor, times(1)).setAccessToken(null);
    }

    @Test
    void executeDecoupledOpr_pmt_updateFail() throws AuthorisationIsExpiredException {
        DecoupledConfRequest request = getDecoupledRequest(OpTypeTO.PAYMENT);

        when(tokenService.exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA)).thenReturn(getToken("sca_token"));
        when(redirectScaClient.validateScaCode(request.getAuthorizationId(), "TAN")).thenReturn(getResponse());
        when(operationInitiationRestClient.execution(OpTypeTO.PAYMENT, request.getObjId())).thenReturn(ResponseEntity.ok(getGlobalScaResponse()));
        doThrow(AuthorisationIsExpiredException.class).when(cmsPsuPisService).updateAuthorisationStatus(any(), any(), any(), any(), any(), any());

        ObaException exception = assertThrows(ObaException.class, () -> service.executeDecoupledOpr(request, "login_token"));
        assertEquals(AUTH_EXPIRED, exception.getObaErrorCode());

        verify(tokenService, times(1)).exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA);
        verify(authInterceptor, times(1)).setAccessToken("sca_token");
        verify(redirectScaClient, times(1)).validateScaCode(request.getAuthorizationId(), "TAN");
        verify(authInterceptor, times(1)).setAccessToken("full_token");
        verify(operationInitiationRestClient, times(1)).execution(OpTypeTO.PAYMENT, request.getObjId());
        verify(cmsPsuPisService, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any());
        verify(authInterceptor, times(1)).setAccessToken(null);
    }

    @Test
    void executeDecoupledOpr_cns() {
        DecoupledConfRequest request = getDecoupledRequest(OpTypeTO.CONSENT);

        when(tokenService.exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA)).thenReturn(getToken("sca_token"));
        ResponseEntity<GlobalScaResponseTO> response = getResponse();
        when(redirectScaClient.validateScaCode(request.getAuthorizationId(), "TAN")).thenReturn(response);

        boolean result = service.executeDecoupledOpr(request, "login_token");
        assertTrue(result);
        verify(tokenService, times(1)).exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA);
        verify(authInterceptor, times(1)).setAccessToken("sca_token");
        verify(redirectScaClient, times(1)).validateScaCode(request.getAuthorizationId(), "TAN");
        verify(authInterceptor, times(1)).setAccessToken("full_token");
        verify(cmsPsuAisClient, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(aspspConsentDataClient, times(1)).updateAspspConsentData(any(), any());
        verify(cmsPsuAisClient, times(1)).confirmConsent(response.getBody().getOperationObjectId(), DEFAULT_SERVICE_INSTANCE_ID);
        verify(authInterceptor, times(1)).setAccessToken(null);
    }

    @Test
    void executeDecoupledOpr_cns_partialAuthorize() {
        DecoupledConfRequest request = getDecoupledRequest(OpTypeTO.CONSENT);

        GlobalScaResponseTO globalScaResponseTO = new GlobalScaResponseTO(getToken("full_token"));
        globalScaResponseTO.setScaStatus(ScaStatusTO.FINALISED);
        globalScaResponseTO.setPartiallyAuthorised(true);
        ResponseEntity<GlobalScaResponseTO> response = ResponseEntity.ok(globalScaResponseTO);

        when(tokenService.exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA)).thenReturn(getToken("sca_token"));
        when(redirectScaClient.validateScaCode(request.getAuthorizationId(), "TAN")).thenReturn(response);

        boolean result = service.executeDecoupledOpr(request, "login_token");
        assertTrue(result);
        verify(tokenService, times(1)).exchangeToken("login_token", request.getAuthorizationTTL(), Constants.SCOPE_SCA);
        verify(authInterceptor, times(1)).setAccessToken("sca_token");
        verify(redirectScaClient, times(1)).validateScaCode(request.getAuthorizationId(), "TAN");
        verify(authInterceptor, times(1)).setAccessToken("full_token");
        verify(cmsPsuAisClient, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(aspspConsentDataClient, times(1)).updateAspspConsentData(any(), any());
        verify(cmsPsuAisClient, times(1)).authorisePartiallyConsent(globalScaResponseTO.getOperationObjectId(), DEFAULT_SERVICE_INSTANCE_ID);
        verify(authInterceptor, times(1)).setAccessToken(null);
    }

    private GlobalScaResponseTO getGlobalScaResponse() {
        GlobalScaResponseTO globalScaResponseTO = new GlobalScaResponseTO();
        globalScaResponseTO.setOperationObjectId("opId");
        globalScaResponseTO.setTransactionStatus(TransactionStatusTO.ACCC);
        return globalScaResponseTO;
    }

    @NotNull
    private ResponseEntity<GlobalScaResponseTO> getResponse() {
        GlobalScaResponseTO response = new GlobalScaResponseTO(getToken("full_token"));
        response.setScaStatus(ScaStatusTO.FINALISED);
        return ResponseEntity.ok(response);
    }

    @NotNull
    private BearerTokenTO getToken(String tokenVal) {
        return new BearerTokenTO(tokenVal, null, 1000, null, null, Set.of(Constants.SCOPE_SCA));
    }

    private DecoupledConfRequest getDecoupledRequest(OpTypeTO opType) {
        DecoupledConfRequest request = new DecoupledConfRequest();
        request.setConfirmed(true);
        request.setObjId("objId");
        request.setAuthCode("TAN");
        request.setOpType(opType);
        return request;
    }
}

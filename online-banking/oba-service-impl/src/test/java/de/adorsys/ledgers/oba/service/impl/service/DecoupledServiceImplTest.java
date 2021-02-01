package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.Constants;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private PaymentRestClient paymentRestClient;
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
        when(tokenService.exchangeToken(any(), any(), any())).thenReturn(getToken("sca_token"));
        when(redirectScaClient.validateScaCode(any(), eq("TAN"))).thenReturn(getResponse());
        when(paymentRestClient.executePayment(any())).thenReturn(ResponseEntity.ok(getScaPaymentResponse()));

        boolean result = service.executeDecoupledOpr(getDecoupledRequest(OpTypeTO.PAYMENT), "login_token");
        assertTrue(result);
        verify(tokenService, times(1)).exchangeToken(anyString(), anyInt(), eq(Constants.SCOPE_SCA));
        verify(authInterceptor, times(1)).setAccessToken(eq("sca_token"));
        verify(redirectScaClient, times(1)).validateScaCode(any(), eq("TAN"));
        verify(authInterceptor, times(1)).setAccessToken(eq("full_token"));
        verify(paymentRestClient, times(1)).executePayment(any());
        verify(cmsPsuPisService, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any());
        verify(cmsPsuPisService, times(1)).updatePaymentStatus(any(), any(), any());
        verify(aspspConsentDataClient, times(1)).updateAspspConsentData(any(), any());
        verify(authInterceptor, times(1)).setAccessToken(eq(null));
    }

    @Test
    void executeDecoupledOpr__pmt_updateFail() throws AuthorisationIsExpiredException {
        when(tokenService.exchangeToken(any(), any(), any())).thenReturn(getToken("sca_token"));
        when(redirectScaClient.validateScaCode(any(), eq("TAN"))).thenReturn(getResponse());
        when(paymentRestClient.executePayment(any())).thenReturn(ResponseEntity.ok(getScaPaymentResponse()));
        doThrow(AuthorisationIsExpiredException.class).when(cmsPsuPisService).updateAuthorisationStatus(any(), any(), any(), any(), any(), any());

        DecoupledConfRequest request = getDecoupledRequest(OpTypeTO.PAYMENT);
        ObaException exception = assertThrows(ObaException.class, () -> service.executeDecoupledOpr(request, "login_token"));
        assertEquals(AUTH_EXPIRED, exception.getObaErrorCode());

        verify(tokenService, times(1)).exchangeToken(anyString(), anyInt(), eq(Constants.SCOPE_SCA));
        verify(authInterceptor, times(1)).setAccessToken(eq("sca_token"));
        verify(redirectScaClient, times(1)).validateScaCode(any(), eq("TAN"));
        verify(authInterceptor, times(1)).setAccessToken(eq("full_token"));
        verify(paymentRestClient, times(1)).executePayment(any());
        verify(cmsPsuPisService, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any());
        verify(authInterceptor, times(1)).setAccessToken(eq(null));
    }

    @Test
    void executeDecoupledOpr_cns() {
        when(tokenService.exchangeToken(any(), any(), any())).thenReturn(getToken("sca_token"));
        when(redirectScaClient.validateScaCode(any(), eq("TAN"))).thenReturn(getResponse());

        boolean result = service.executeDecoupledOpr(getDecoupledRequest(OpTypeTO.CONSENT), "login_token");
        assertTrue(result);
        verify(tokenService, times(1)).exchangeToken(anyString(), anyInt(), eq(Constants.SCOPE_SCA));
        verify(authInterceptor, times(1)).setAccessToken(eq("sca_token"));
        verify(redirectScaClient, times(1)).validateScaCode(any(), eq("TAN"));
        verify(authInterceptor, times(1)).setAccessToken(eq("full_token"));
        verify(cmsPsuAisClient, times(1)).updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(aspspConsentDataClient, times(1)).updateAspspConsentData(any(), any());
        verify(authInterceptor, times(1)).setAccessToken(eq(null));
    }

    private SCAPaymentResponseTO getScaPaymentResponse() {
        return new SCAPaymentResponseTO("opId", "ACCC", "SINGLE", null);
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

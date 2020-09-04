package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PsuMessage;
import de.adorsys.ledgers.oba.service.api.domain.PsuMessageCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.FINALISED;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.PSUIDENTIFIED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SCAControllerTest {
    private static final String PIN = "12345";
    private static final String LOGIN = "anton.brueckner";
    private static final String ENCRYPTED_ID = "ENC_123";
    private static final String AUTH_ID = "AUTH_1";
    private static final String METHOD_ID = "SCA_1";
    private static final String COOKIE = "COOKIE";

    @InjectMocks
    SCAController controller;

    @Mock
    private UserMgmtRestClient ledgersUserMgmt;
    @Mock
    private ResponseUtils responseUtils;
    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private ObaMiddlewareAuthentication auth;

   /* @Test //TODO Fix me if required!
    void login() {
        // Given
        when(ledgersUserMgmt.authorise(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(getLoginResponse(PSUIDENTIFIED));

        // When
        ResponseEntity<AuthorizeResponse> result = controller.login(LOGIN, PIN);

        // Then
        assertEquals(getExpectedAuthResponse(PSUIDENTIFIED, Collections.emptyList()), result);
    }

    @Test
    void selectMethod() {
        // Given
        when(auth.getBearerToken()).thenReturn(new BearerTokenTO());
        when(ledgersUserMgmt.selectMethod(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(getLoginResponse(PSUIDENTIFIED));

        // When
        ResponseEntity<AuthorizeResponse> result = controller.selectMethod(AUTH_ID, AUTH_ID, METHOD_ID, COOKIE);

        // Then
        assertEquals(getExpectedAuthResponse(PSUIDENTIFIED, null), result);
    }

    @Test
    void validateAuthCode() {
        // Given
        when(auth.getBearerToken()).thenReturn(new BearerTokenTO());
        when(ledgersUserMgmt.authorizeLogin(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(getLoginResponse(FINALISED));

        // When
        ResponseEntity<AuthorizeResponse> result = controller.validateAuthCode(ENCRYPTED_ID, AUTH_ID, PIN, COOKIE);

        // Then
        assertEquals(getExpectedAuthResponse(FINALISED, null), result);
    }*/

   /* private ResponseEntity<SCALoginResponseTO> getLoginResponse(ScaStatusTO status) { //TODO FIX ME!!!
        SCALoginResponseTO res = new SCALoginResponseTO();
        res.setScaStatus(status);
        AccessTokenTO to = new AccessTokenTO();
        to.setAuthorisationId(AUTH_ID);
        res.setBearerToken(new BearerTokenTO(null, null, 0, null, to));
        res.setScaId(ENCRYPTED_ID);
        res.setAuthorisationId(AUTH_ID);
        res.setMultilevelScaRequired(false);
        res.setScaMethods(Collections.emptyList());
        return ResponseEntity.ok(res);
    }*/

    private ResponseEntity<AuthorizeResponse> getExpectedAuthResponse(ScaStatusTO scaStatus, List<ScaUserDataTO> methods) {
        AuthorizeResponse res = new AuthorizeResponse();
        res.setScaStatus(scaStatus);
        res.setScaMethods(methods);
        res.setEncryptedConsentId(ENCRYPTED_ID);
        res.setAuthorisationId(AUTH_ID);
        PsuMessage message = new PsuMessage();
        message.setCategory(PsuMessageCategory.INFO);
        res.setPsuMessages(Collections.singletonList(message));
        return ResponseEntity.ok(res);
    }
}

package de.adorsys.ledgers.oba.rest.server.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SCAControllerTest {
    @Test
    void stubTest() {
        assertTrue(true);
    }
    //TODO Seem has to be completely removed along whith whole controller
   /* private static final String PIN = "12345";  STUB
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

    @Test
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
    }

    private ResponseEntity<SCALoginResponseTO> getLoginResponse(ScaStatusTO status) {
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
    }

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
    }*/
}

package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.service.api.domain.ObaAisConsent;
import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.ledgers.oba.service.api.service.ConsentService;
import de.adorsys.ledgers.oba.service.api.service.TppInfoCmsService;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObaConsentControllerTest {
    private static final String LOGIN = "login";
    private static final String CONSENT_ID = "consentId";
    private static final String AUTH_ID = "auth";
    private static final String TAN = "123456";
    private static final String TOKEN = "TOKEN";

    @InjectMocks
    private ObaConsentController controller;

    @Mock
    private ConsentService consentService;
    @Mock
    private ObaMiddlewareAuthentication auth;
    @Mock
    private TppInfoCmsService tppInfoCmsService;

    @Test
    void consents() {
        // Given
        when(consentService.getListOfConsents(anyString())).thenReturn(getObaAisConsents());

        // When
        ResponseEntity<List<ObaAisConsent>> response = controller.consents(LOGIN);

        // Then
        assertEquals(ResponseEntity.ok(getObaAisConsents()), response);
    }

    private List<ObaAisConsent> getObaAisConsents() {
        return Collections.singletonList(new ObaAisConsent(CONSENT_ID, new CmsAisAccountConsent()));
    }

    @Test
    void revokeConsent() {
        // Given
        when(consentService.revokeConsent(anyString())).thenReturn(true);

        // When
        ResponseEntity<Boolean> response = controller.revokeConsent(CONSENT_ID);

        // Then
        assertEquals(ResponseEntity.ok(true), response);
    }

    @Test
    void confirm() {
        // When
        ResponseEntity<Void> response = controller.confirm(LOGIN, CONSENT_ID, AUTH_ID, TAN);

        // Then
        assertEquals(ResponseEntity.accepted().build(), response);
    }

    @Test
    void createPiis() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(controller, controller.getClass().getDeclaredField("auth"), new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO(), new HashSet<>())));

        // When
        ResponseEntity<Void> response = controller.createPiis(new CreatePiisConsentRequestTO());

        // Then
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void tpps() {
        // Given
        when(tppInfoCmsService.getTpps()).thenReturn(Collections.singletonList(new TppInfoTO()));

        // When
        ResponseEntity<List<TppInfoTO>> response = controller.tpps();

        // Then
        assertEquals(ResponseEntity.ok(Collections.singletonList(new TppInfoTO())), response);
    }

    private AccessTokenTO getAccessTokenTO() {
        AccessTokenTO tokenTO = new AccessTokenTO();
        tokenTO.setLogin(LOGIN);
        tokenTO.setConsent(getAisConsentTO());
        return tokenTO;
    }

    private AisConsentTO getAisConsentTO() {
        return new AisConsentTO(CONSENT_ID, LOGIN, "TPP_ID", 5, null, LocalDate.of(2020, 1, 1), false);
    }

}

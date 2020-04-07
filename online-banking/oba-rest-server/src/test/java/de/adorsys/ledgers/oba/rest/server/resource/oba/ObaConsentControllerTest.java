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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ObaConsentControllerTest {
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
    public void consents() {
        when(consentService.getListOfConsents(anyString())).thenReturn(getObaAisConsents());
        ResponseEntity<List<ObaAisConsent>> response = controller.consents(LOGIN);
        assertThat(response).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getObaAisConsents()));
    }

    private List<ObaAisConsent> getObaAisConsents() {
        return Collections.singletonList(new ObaAisConsent(CONSENT_ID, new CmsAisAccountConsent()));
    }

    @Test
    public void revokeConsent() {
        when(consentService.revokeConsent(anyString())).thenReturn(true);
        ResponseEntity<Boolean> response = controller.revokeConsent(CONSENT_ID);
        assertThat(response).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(true));
    }

    @Test
    public void confirm() {
        ResponseEntity<Void> response = controller.confirm(LOGIN, CONSENT_ID, AUTH_ID, TAN);
        assertThat(response).isEqualToComparingFieldByFieldRecursively(ResponseEntity.accepted().build());
    }

    @Test
    public void createPiis() {
        Whitebox.setInternalState(controller, "auth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        ResponseEntity<Void> response = controller.createPiis(new CreatePiisConsentRequestTO());
        assertThat(response).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok().build());
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

    @Test
    public void tpps() {
        when(tppInfoCmsService.getTpps()).thenReturn(Collections.singletonList(new TppInfoTO()));
        ResponseEntity<List<TppInfoTO>> response = controller.tpps();
        assertThat(response).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(Collections.singletonList(new TppInfoTO())));
    }
}

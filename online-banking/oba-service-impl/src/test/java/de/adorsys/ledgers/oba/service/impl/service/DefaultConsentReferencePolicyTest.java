package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class DefaultConsentReferencePolicyTest {
    private static final String AUTHORIZATION_ID = "authorizationID";
    private static final ConsentType CONSENT_TYPE_AIS = ConsentType.AIS;
    private static final String ENCRYPTED_CONSENT_ID = "encryptedConsentId";
    private static final String REDIRECT_ID = "redirectID";

    @InjectMocks
    private DefaultConsentReferencePolicy defaultConsentReferencePolicy;

    @Test
    public void fromURL() {
        //given
        Whitebox.setInternalState(defaultConsentReferencePolicy, "hmacSecret", "6VFX8YFQG5DLFKZIMNLGH9P406XR1SY4");

        //when
        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);

        //then
        assertThat(reference).isNotNull();
        assertEquals(REDIRECT_ID, reference.getRedirectId());
        assertEquals(CONSENT_TYPE_AIS, reference.getConsentType());
        assertEquals(ENCRYPTED_CONSENT_ID, reference.getEncryptedConsentId());
        assertNotNull(reference.getCookieString());
    }

    @Test
    public void fromRequest() {
        //given
        Whitebox.setInternalState(defaultConsentReferencePolicy, "hmacSecret", "6VFX8YFQG5DLFKZIMNLGH9P406XR1SY4");
        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);

        //when
        ConsentReference consentReference = defaultConsentReferencePolicy.fromRequest(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID, reference.getCookieString(), false);

        //then
        assertThat(consentReference).isNotNull();
        assertEquals(AUTHORIZATION_ID, consentReference.getAuthorizationId());
        assertEquals(REDIRECT_ID, consentReference.getRedirectId());
        assertEquals(CONSENT_TYPE_AIS, consentReference.getConsentType());
        assertEquals(ENCRYPTED_CONSENT_ID, consentReference.getEncryptedConsentId());
        assertNotNull(consentReference.getCookieString());
    }

    @Test(expected = AuthorizationException.class)
    public void fromRequest_authFailed() {
        //when
        defaultConsentReferencePolicy.fromRequest(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID, "cookieString", false);
    }

    @Test(expected = AuthorizationException.class)
    public void fromRequest_missingClaim() {
        //given
        Whitebox.setInternalState(defaultConsentReferencePolicy, "hmacSecret", "6VFX8YFQG5DLFKZIMNLGH9P406XR1SY4");
        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);

        //when
        defaultConsentReferencePolicy.fromRequest(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID, reference.getCookieString(), true);
    }

    @Test(expected = AuthorizationException.class)
    public void fromRequest_wrongEncryptedConsentId() {
        //given
        Whitebox.setInternalState(defaultConsentReferencePolicy, "hmacSecret", "6VFX8YFQG5DLFKZIMNLGH9P406XR1SY4");
        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);

        //when
        defaultConsentReferencePolicy.fromRequest(null, AUTHORIZATION_ID, reference.getCookieString(), false);
    }
}

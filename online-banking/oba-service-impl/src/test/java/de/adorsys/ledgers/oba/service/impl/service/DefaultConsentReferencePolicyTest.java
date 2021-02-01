
package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultConsentReferencePolicyTest {
    private static final String AUTHORIZATION_ID = "authorizationID";
    private static final ConsentType CONSENT_TYPE_AIS = ConsentType.AIS;
    private static final String ENCRYPTED_CONSENT_ID = "encryptedConsentId";
    private static final String REDIRECT_ID = "redirectID";

    @InjectMocks
    private DefaultConsentReferencePolicy defaultConsentReferencePolicy;

    @Test
    void fromURL() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(defaultConsentReferencePolicy, defaultConsentReferencePolicy.getClass().getDeclaredField("hmacSecret"), "6VFX8YFQG5DLFKZIMNLGH9P406XR1SY4");

        // When
        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);

        // Then
        assertNotNull(reference);
        assertEquals(REDIRECT_ID, reference.getRedirectId());
        assertEquals(CONSENT_TYPE_AIS, reference.getConsentType());
        assertEquals(ENCRYPTED_CONSENT_ID, reference.getEncryptedConsentId());
        assertNotNull(reference.getCookieString());
    }

    @Test
    void fromRequest() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(defaultConsentReferencePolicy, defaultConsentReferencePolicy.getClass().getDeclaredField("hmacSecret"), "6VFX8YFQG5DLFKZIMNLGH9P406XR1SY4");

        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);

        // When
        ConsentReference consentReference = defaultConsentReferencePolicy.fromRequest(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID, reference.getCookieString(), false);

        // Then
        assertNotNull(consentReference);
        assertEquals(AUTHORIZATION_ID, consentReference.getAuthorizationId());
        assertEquals(REDIRECT_ID, consentReference.getRedirectId());
        assertEquals(CONSENT_TYPE_AIS, consentReference.getConsentType());
        assertEquals(ENCRYPTED_CONSENT_ID, consentReference.getEncryptedConsentId());
        assertNotNull(consentReference.getCookieString());
    }

    @Test
    void fromRequest_authFailed() {
        // Then
        assertThrows(ObaException.class, () -> defaultConsentReferencePolicy.fromRequest(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID, "cookieString", false));
    }

    @Test
    void fromRequest_missingClaim() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(defaultConsentReferencePolicy, defaultConsentReferencePolicy.getClass().getDeclaredField("hmacSecret"), "6VFX8YFQG5DLFKZIMNLGH9P406XR1SY4");
        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);
        String cookieString = reference.getCookieString();
        // Then
        assertThrows(ObaException.class, () -> defaultConsentReferencePolicy.fromRequest(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID, cookieString, true));
    }

    @Test
    void fromRequest_wrongEncryptedConsentId() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(defaultConsentReferencePolicy, defaultConsentReferencePolicy.getClass().getDeclaredField("hmacSecret"), "6VFX8YFQG5DLFKZIMNLGH9P406XR1SY4");
        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);
        String cookieString = reference.getCookieString();
        // Then
        assertThrows(ObaException.class, () -> defaultConsentReferencePolicy.fromRequest(null, AUTHORIZATION_ID, cookieString, false));
    }
}

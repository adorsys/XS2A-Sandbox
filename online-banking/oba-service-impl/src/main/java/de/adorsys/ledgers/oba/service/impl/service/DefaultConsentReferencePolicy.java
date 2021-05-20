package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultConsentReferencePolicy implements ConsentReferencePolicy {


    @Override
    public ConsentReference fromURL(String redirectId, ConsentType consentType, String encryptedConsentId) {
        ConsentReference cr = new ConsentReference();
        cr.setRedirectId(redirectId);
        cr.setConsentType(consentType);
        cr.setEncryptedConsentId(encryptedConsentId);
        return cr;
    }

    @Override
    public ConsentReference fromRequest(String encryptedConsentId, String authorizationId) {
        return consentReference(encryptedConsentId, authorizationId, ConsentType.AIS, authorizationId);
    }


    private ConsentReference consentReference(String encryptedConsentId, String authorizationId, ConsentType type, String redirectId) {
        ConsentReference cr = new ConsentReference();
        cr.setConsentType(type);
        cr.setRedirectId(redirectId);
        cr.setEncryptedConsentId(encryptedConsentId);
        cr.setAuthorizationId(authorizationId);
        return cr;
    }


}

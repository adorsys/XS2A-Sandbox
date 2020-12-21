package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.oba.service.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.service.api.domain.ObaAisConsent;
import de.adorsys.ledgers.util.domain.CustomPageImpl;

import java.util.List;

public interface ConsentService {
    List<ObaAisConsent> getListOfConsents(String userLogin);

    boolean revokeConsent(String consentId);

    void confirmAisConsentDecoupled(String userLogin, String encryptedConsentId, String authorizationId, String tan);

    void createPiisConsent(CreatePiisConsentRequestTO request, String psuId);

    CustomPageImpl<ObaAisConsent> getListOfConsentsPaged(String userLogin, int page, int size);
}

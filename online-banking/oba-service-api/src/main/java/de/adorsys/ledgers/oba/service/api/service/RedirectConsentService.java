package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.ConsentWorkflow;

import java.util.List;

public interface RedirectConsentService {
    void selectScaMethod(String scaMethodId, final ConsentWorkflow workflow);

    void updateAccessByConsentType(ConsentWorkflow workflow, List<AccountDetailsTO> listOfAccounts);

    void startConsent(final ConsentWorkflow workflow, AisConsentTO aisConsent, List<AccountDetailsTO> listOfAccounts);

    ConsentWorkflow identifyConsent(String encryptedConsentId, String authorizationId, boolean strict, String consentCookieString, BearerTokenTO bearerToken);

    void updateScaStatusConsentStatusConsentData(String psuId, ConsentWorkflow workflow);
}

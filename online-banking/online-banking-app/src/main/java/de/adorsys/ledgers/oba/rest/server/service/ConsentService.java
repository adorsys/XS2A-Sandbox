package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.oba.rest.api.exception.AisException;
import de.adorsys.psd2.consent.api.ais.AisAccountConsent;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.adorsys.ledgers.oba.rest.api.domain.AisErrorCode.AIS_BAD_REQUEST;
import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentService {
    private static final String RESPONSE_ERROR = "Error in response from CMS, please contact admin.";
    private static final String GET_CONSENTS_ERROR_MSG = "Failed to retrieve consents for user: %s, code: %s, message: %s";

    private final CmsPsuAisClient cmsPsuAisClient;

    public List<AisAccountConsent> getListOfConsents(String userLogin) {
        try {
            return Optional.ofNullable(cmsPsuAisClient.getConsentsForPsu(userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID).getBody())
                       .orElse(Collections.emptyList());
        } catch (FeignException e) {
            String msg = String.format(GET_CONSENTS_ERROR_MSG, userLogin, e.status(), e.getMessage());
            log.error(msg);
            throw AisException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .aisErrorCode(AIS_BAD_REQUEST).build();
        }
    }

    public boolean revokeConsent(String consentId) {
        return Optional.ofNullable(cmsPsuAisClient.revokeConsent(consentId, null, null, null, null, DEFAULT_SERVICE_INSTANCE_ID).getBody())
                   .orElse(false);
    }
}

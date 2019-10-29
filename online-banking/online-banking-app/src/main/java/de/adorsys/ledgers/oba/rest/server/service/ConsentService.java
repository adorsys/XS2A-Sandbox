package de.adorsys.ledgers.oba.rest.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.oba.rest.api.domain.AisErrorCode;
import de.adorsys.ledgers.oba.rest.api.domain.ObaAisConsent;
import de.adorsys.ledgers.oba.rest.api.exception.AisException;
import de.adorsys.psd2.consent.api.AspspDataService;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.service.security.SecurityDataService;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.adorsys.ledgers.oba.rest.api.domain.AisErrorCode.*;
import static java.lang.String.format;
import static java.util.Base64.getEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentService {
    private static final String RESPONSE_ERROR = "Error in response from CMS, please contact admin.";
    private static final String GET_CONSENTS_ERROR_MSG = "Failed to retrieve consents for user: %s, code: %s, message: %s";
    private static final String CONSENT_COULD_NOT_BE_FOUND = "Consent %s could not be found";
    private static final String FAILED_TO_CONFIRM_THE_CONSENT_MSG = "Failed to confirm the consent %s msg: %s";
    private static final String UPDATE_FAILED_MSG = "Update %s failed msg: %s";
    private static final String COULD_NOT_RETRIEVE_ASPSP_CONSENT_DATA = "Could not retrieve ASPSP consent data.";
    private static final String DEFAULT_SERVICE_INSTANCE_ID = "UNDEFINED";

    private final CmsPsuAisClient cmsPsuAisClient;
    private final SecurityDataService securityDataService;
    private final AspspConsentDataClient consentDataClient;
    private final ConsentRestClient consentRestClient;
    private final AuthRequestInterceptor authInterceptor;
    private final ObjectMapper objectMapper;
    private final AspspDataService aspspDataService;

    public List<ObaAisConsent> getListOfConsents(String userLogin) {
        try {
            List<CmsAisAccountConsent> aisAccountConsents = Optional.ofNullable(cmsPsuAisClient.getConsentsForPsu(userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID).getBody())
                                                                .orElse(Collections.emptyList());
            return toObaAisConsent(aisAccountConsents);
        } catch (FeignException e) {
            String msg = format(GET_CONSENTS_ERROR_MSG, userLogin, e.status(), e.getMessage());
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

    public void confirmAisConsentDecoupled(String userLogin, String encryptedConsentId, String authorizationId, String tan) {
        String consentId = getDecryptedConsentId(encryptedConsentId);
        setActiveAccessTokenFromConsentData(encryptedConsentId);

        SCAConsentResponseTO ledgerValidateTanConsentResponse = authorizeConsentAtLedgers(consentId, authorizationId, tan);
        confirmConsentAtCms(userLogin, consentId);
        updateCmsAuthorization(userLogin, authorizationId, consentId);
        updateAspspConsentDataForConsent(encryptedConsentId, ledgerValidateTanConsentResponse);
        authInterceptor.setAccessToken(null);
    }

    private void updateCmsAuthorization(String userLogin, String authorizationId, String consentId) {
        try {
            cmsPsuAisClient.updateAuthorisationStatus(consentId, "FINALISED", authorizationId, userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, null));
        } catch (FeignException e) {
            String msg = format(UPDATE_FAILED_MSG, "authorization", e.getMessage());
            log.error(msg);
            throw AisException.builder()
                      .devMessage(msg)
                      .aisErrorCode(AIS_BAD_REQUEST)
                      .build();
        }
    }

    private void updateAspspConsentDataForConsent(String encryptedConsentId, SCAConsentResponseTO ledgerValidateTanConsentResponse) {
        CmsAspspConsentDataBase64 updatedConsentData = new CmsAspspConsentDataBase64(encryptedConsentId, writeScaResponseAsString(ledgerValidateTanConsentResponse));
        try {
            consentDataClient.updateAspspConsentData(encryptedConsentId, updatedConsentData);
        } catch (FeignException e) {
            String msg = format(UPDATE_FAILED_MSG, "aspsp consent data", e.getMessage());
            log.error(msg);
            throw AisException.builder()
                      .devMessage(msg)
                      .aisErrorCode(AIS_BAD_REQUEST)
                      .build();
        }
    }

    private void confirmConsentAtCms(String userLogin, String consentId) {
        try {
            cmsPsuAisClient.confirmConsent(consentId, userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID).getBody();
        } catch (FeignException e) {
            String msg = e.status() == 404
                             ? format(CONSENT_COULD_NOT_BE_FOUND, consentId)
                             : format(FAILED_TO_CONFIRM_THE_CONSENT_MSG, consentId, e.getMessage());
            log.error(msg);
            AisErrorCode errorCode = e.status() == 404
                                         ? NOT_FOUND
                                         : CONNECTION_ERROR;
            throw AisException.builder()
                      .devMessage(msg)
                      .aisErrorCode(errorCode)
                      .build();
        }
    }

    private String getDecryptedConsentId(String encryptedConsentId) {
        return securityDataService.decryptId(encryptedConsentId)
                   .orElseThrow(() -> AisException.builder()
                                          .devMessage("Error decrypting consent id")
                                          .aisErrorCode(AIS_BAD_REQUEST)
                                          .build());
    }

    private String writeScaResponseAsString(SCAResponseTO ledgerValidateTanConsentResponse) {
        try {
            return getEncoder().encodeToString(objectMapper.writeValueAsBytes(ledgerValidateTanConsentResponse));
        } catch (JsonProcessingException e) {
            throw AisException.builder()
                      .devMessage("Could not encode ledgers consent confirmation response.")
                      .aisErrorCode(AIS_BAD_REQUEST)
                      .build();
        }
    }

    private SCAConsentResponseTO authorizeConsentAtLedgers(String consentId, String authorizationId, String tan) {
        try {
            return consentRestClient.authorizeConsent(consentId, authorizationId, tan).getBody();
        } catch (FeignException e) {
            throw AisException.builder()
                      .devMessage(getDevMessageFromFeignException(e))
                      .aisErrorCode(AIS_BAD_REQUEST)
                      .build();
        }
    }

    private String getDevMessageFromFeignException(FeignException e) {
        try {
            return objectMapper.readTree(e.content()).get("devMessage").asText();
        } catch (IOException i) {
            return "Could not extract exception message from ASPSP response";
        }
    }

    private void setActiveAccessTokenFromConsentData(String encryptedConsentId) {
        String token;
        try {
            byte[] decodedData = aspspDataService.readAspspConsentData(encryptedConsentId)
                                     .map(AspspConsentData::getAspspConsentData)
                                     .orElseThrow(() -> AisException.builder()
                                                            .devMessage(COULD_NOT_RETRIEVE_ASPSP_CONSENT_DATA)
                                                            .aisErrorCode(AIS_BAD_REQUEST)
                                                            .build());
            token = Optional.ofNullable(objectMapper.readTree(decodedData).get("bearerToken"))
                        .map(t -> t.get("access_token"))
                        .map(JsonNode::asText)
                        .orElseThrow(() -> AisException.builder()
                                               .devMessage("No AccessToken present in ASPSP consent data")
                                               .aisErrorCode(AIS_BAD_REQUEST)
                                               .build());
        } catch (IOException e) {
            throw AisException.builder()
                      .devMessage("Could not parse ASPSP consent data")
                      .aisErrorCode(AIS_BAD_REQUEST)
                      .build();
        }
        authInterceptor.setAccessToken(token);
    }

    private List<ObaAisConsent> toObaAisConsent(List<CmsAisAccountConsent> aisAccountConsents) {
        return aisAccountConsents.stream()
                   .map(a -> new ObaAisConsent(securityDataService.encryptId(a.getId()).orElse(""), a))
                   .collect(Collectors.toList());
    }
}

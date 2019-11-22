package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentWorkflow;
import de.adorsys.ledgers.oba.rest.api.domain.ValidationCode;
import de.adorsys.ledgers.oba.rest.api.exception.AuthorizationException;
import de.adorsys.ledgers.oba.rest.api.exception.ConsentAuthorizeException;
import de.adorsys.ledgers.oba.rest.server.mapper.ObaAisConsentMapper;
import de.adorsys.ledgers.oba.rest.server.resource.ResponseUtils;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisConsentAccessRequest;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.adorsys.ledgers.oba.rest.api.exception.AuthErrorCode.LOGIN_FAILED;
import static de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType.*;
import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID;

@Service
@RequiredArgsConstructor
public class RedirectConsentService {
    private final CmsPsuAisClient cmsPsuAisClient;
    private final ConsentRestClient consentRestClient;
    private final AuthRequestInterceptor authInterceptor;
    private final ObaAisConsentMapper consentMapper;
    private final ConsentReferencePolicy referencePolicy;
    private final ResponseUtils responseUtils;
    private final TokenStorageService tokenStorageService;
    private final AspspConsentDataClient aspspConsentDataClient;

    public void selectScaMethod(String scaMethodId, final ConsentWorkflow workflow) {
        try {
            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
            // INFO. Server does not set the bearer token.
            BearerTokenTO bearerToken = workflow.bearerToken();
            SCAConsentResponseTO sca = consentRestClient.selectMethod(workflow.consentId(), workflow.authId(), scaMethodId).getBody();
            // INFO. Server does not set the bearer token.
            sca.setBearerToken(bearerToken);
            workflow.storeSCAResponse(sca);
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    public void updateAccessByConsentType(ConsentWorkflow workflow, List<AccountDetailsTO> listOfAccounts) {
        AisConsentRequestType consentType = workflow.getConsentResponse().getAccountConsent().getAisConsentRequestType();
        if (!EnumSet.of(BANK_OFFERED, DEDICATED_ACCOUNTS).contains(consentType)) {
            AisAccountAccessInfoTO authAccountAccess = workflow.getAuthResponse().getConsent().getAccess();
            List<String> ibans = extractUserIbans(listOfAccounts);
            authAccountAccess.setAccounts(ibans);
            if (ALL_AVAILABLE_ACCOUNTS == consentType && requestedConsentWithBalance(workflow)) {
                authAccountAccess.setBalances(ibans);
            }
            if (GLOBAL == consentType) {
                authAccountAccess.setBalances(ibans);
                authAccountAccess.setTransactions(ibans);
            }
        }
        if (DEDICATED_ACCOUNTS == consentType) {
            Set<String> ibans = new HashSet<>(extractUserIbans(listOfAccounts));
            Set<String> ibansFromAccess = extractIbansFromAccess(workflow.getConsentResponse().getAccountConsent().getAccess());
            checkAccess(ibansFromAccess, ibans);
        }
    }

    private void checkAccess(Set<String> ibansFromAccess, Set<String> ibans) {
        if (!ibans.containsAll(ibansFromAccess)) {
            throw AuthorizationException.builder()
                      .errorCode(LOGIN_FAILED)
                      .devMessage("Operation you're logging in is not meant for current user")
                      .build();
        }
    }

    private Set<String> extractIbansFromAccess(AisAccountAccess access) {
        return Stream.of(access.getAccounts(), access.getBalances(), access.getTransactions())
                   .flatMap(a -> a.stream().map(AccountReference::getIban))
                   .collect(Collectors.toSet());
    }

    /**
     * Returns list of accounts' IBANs to which user has an access to.
     * Necessary for Global Consent and All Accounts Consent.
     *
     * @param accounts user account accesses
     */
    private List<String> extractUserIbans(List<AccountDetailsTO> accounts) {
        return accounts
                   .stream()
                   .map(AccountDetailsTO::getIban)
                   .collect(Collectors.toList());
    }

    public void updateScaStatusConsentStatusConsentData(String psuId, ConsentWorkflow workflow, HttpServletResponse response)
        throws ConsentAuthorizeException {
        // UPDATE CMS
        updateCmsAuthorizationScaStatus(workflow, psuId, response);
        updateAspspConsentData(workflow, response);
    }

    private void updateCmsAuthorizationScaStatus(ConsentWorkflow workflow, String psuId, HttpServletResponse response) throws ConsentAuthorizeException {
        String status = workflow.getAuthResponse().getScaStatus().name();
        ResponseEntity resp = cmsPsuAisClient.updateAuthorisationStatus(workflow.consentId(), status,
            workflow.authId(), psuId, null, null, null, DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, null));
        if (!HttpStatus.OK.equals(resp.getStatusCode())) {
            throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(),
                "Error updating authorisation status. See error code.", resp.getStatusCode(), response));
        }
    }

    private void updateAspspConsentData(ConsentWorkflow workflow, HttpServletResponse httpResp) throws ConsentAuthorizeException {
        CmsAspspConsentDataBase64 consentData;
        try {
            consentData = new CmsAspspConsentDataBase64(workflow.consentId(), tokenStorageService.toBase64String(workflow.getScaResponse()));
        } catch (IOException e) {
            throw new ConsentAuthorizeException(
                responseUtils.backToSender(authResp(), workflow.getConsentResponse().getTppNokRedirectUri(),
                    workflow.getConsentResponse().getTppOkRedirectUri(),
                    httpResp, HttpStatus.INTERNAL_SERVER_ERROR, ValidationCode.CONSENT_DATA_UPDATE_FAILED));
        }
        ResponseEntity<?> updateAspspConsentData = aspspConsentDataClient.updateAspspConsentData(
            workflow.getConsentReference().getEncryptedConsentId(), consentData);
        if (!HttpStatus.OK.equals(updateAspspConsentData.getStatusCode())) {
            throw new ConsentAuthorizeException(
                responseUtils.backToSender(authResp(), workflow.getConsentResponse().getTppNokRedirectUri(),
                    workflow.getConsentResponse().getTppOkRedirectUri(),
                    httpResp, updateAspspConsentData.getStatusCode(), ValidationCode.CONSENT_DATA_UPDATE_FAILED));
        }
    }

    public void startConsent(final ConsentWorkflow workflow, AisConsentTO aisConsent, List<AccountDetailsTO> listOfAccounts) {
        try {
            // Map the requested access and push it to the consent management system.
            AisAccountAccess accountAccess = consentMapper.accountAccess(aisConsent.getAccess(), listOfAccounts);
            CmsAisConsentAccessRequest accountAccessRequest = new CmsAisConsentAccessRequest(accountAccess, aisConsent.getValidUntil(), aisConsent.getFrequencyPerDay(), false, aisConsent.isRecurringIndicator());
            cmsPsuAisClient.putAccountAccessInConsent(workflow.consentId(), accountAccessRequest, DEFAULT_SERVICE_INSTANCE_ID);

            // Prepare consent object for ledger
            AisConsentTO consent = consentMapper.toTo(workflow.getConsentResponse().getAccountConsent());
            consent.setAccess(aisConsent.getAccess());
            workflow.getAuthResponse().setConsent(consent);

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
            SCAConsentResponseTO sca = consentRestClient.startSCA(workflow.consentId(), consent).getBody();

            // Store sca response in workflow.
            // TODO: CHeck why. INFO. Server does not set the bearer token.
            sca.setBearerToken(workflow.bearerToken()); // copy bearer from old sca object.
            workflow.storeSCAResponse(sca);
        } catch (FeignException f) {
            workflow.setErrorCode(HttpStatus.valueOf(f.status()));
            throw f;
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    /*
     * Identifying the consent associated with a request. Each request sent to consent endpoint is
     * associated with to parameter:
     * - An encryptedConsentId: containing the consentId and the key used to protect the consent id.
     * - An authorizationId: generally matching the redirectId sent by the XS2A-Endpoint.
     *
     * These two information are both available in the XMLHTTPRequest sent to this endpoint and in the
     * consent cookie stored with this request.
     *
     * Request will only be processed if both url and cookie data match.
     *
     * The consent is then load and stored in the response object. If consent is not found, redirect
     * PSU to TPP.
     */
    public ConsentWorkflow identifyConsent(String encryptedConsentId, String authorizationId, boolean strict, String consentCookieString, HttpServletResponse response, BearerTokenTO bearerToken) throws ConsentAuthorizeException {
        // Parse and verify the consent cookie.
        ConsentReference consentReference;
        try {
            String consentCookie = responseUtils.consentCookie(consentCookieString);
            consentReference = referencePolicy.fromRequest(encryptedConsentId, authorizationId, consentCookie, strict);
        } catch (InvalidConsentException e) {
            throw new ConsentAuthorizeException(responseUtils.forbidden(authResp(), e.getMessage(), response));
        }

        CmsAisConsentResponse cmsConsentResponse = loadConsentByRedirectId(consentReference, response);

        ConsentWorkflow workflow = new ConsentWorkflow(cmsConsentResponse, consentReference);
        AisConsentTO aisConsentTO = consentMapper.toTo(cmsConsentResponse.getAccountConsent());

        workflow.setAuthResponse(new ConsentAuthorizeResponse(aisConsentTO));
        workflow.getAuthResponse().setAuthorisationId(cmsConsentResponse.getAuthorisationId());
        workflow.getAuthResponse().setEncryptedConsentId(encryptedConsentId);
        if (bearerToken != null) {
            SCAConsentResponseTO scaConsentResponseTO = new SCAConsentResponseTO();
            scaConsentResponseTO.setBearerToken(bearerToken);
            workflow.setScaResponse(scaConsentResponseTO);
        }
        return workflow;
    }

    private CmsAisConsentResponse loadConsentByRedirectId(ConsentReference consentReference, HttpServletResponse response) throws ConsentAuthorizeException {
        String redirectId = consentReference.getRedirectId();
        // 4. After user login:
        ResponseEntity<CmsAisConsentResponse> responseEntity = cmsPsuAisClient.getConsentIdByRedirectId(redirectId, DEFAULT_SERVICE_INSTANCE_ID);
        HttpStatus statusCode = responseEntity.getStatusCode();

        if (HttpStatus.OK.equals(statusCode)) {
            return responseEntity.getBody();
        }

        if (HttpStatus.NOT_FOUND.equals(statusCode)) {
            // ---> if(NotFound)
            throw new ConsentAuthorizeException(responseUtils.requestWithRedNotFound(authResp(), response));
        }

        if (HttpStatus.REQUEST_TIMEOUT.equals(statusCode)) {
            // ---> if(Expired, TPP-Redirect-URL)
            // 3.a0) LogOut User
            // 3.a1) Send back to TPP
            CmsAisConsentResponse consent = responseEntity.getBody();
            String location = StringUtils.isNotBlank(consent.getTppNokRedirectUri())
                                  ? consent.getTppNokRedirectUri()
                                  : consent.getTppOkRedirectUri();
            throw new ConsentAuthorizeException(responseUtils.redirect(location, response));
        }
        throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), statusCode, response));
    }

    private ConsentAuthorizeResponse authResp() {
        return new ConsentAuthorizeResponse();
    }

    private boolean requestedConsentWithBalance(ConsentWorkflow workflow) {
        return StringUtils.isNotBlank(workflow.getConsentResponse().getAccountConsent().getAccess().getAvailableAccountsWithBalance());
    }
}

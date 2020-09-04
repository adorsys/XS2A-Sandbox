package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.StartScaOprTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.ledgers.oba.service.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.service.api.service.RedirectConsentService;
import de.adorsys.ledgers.oba.service.impl.mapper.ObaAisConsentMapper;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisConsentAccessRequest;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import lombok.RequiredArgsConstructor;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode.CONSENT_DATA_UPDATE_FAILED;
import static de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode.LOGIN_FAILED;
import static de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType.*;
import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID;

@Service
@RequiredArgsConstructor
public class RedirectConsentServiceImpl implements RedirectConsentService {
    private final CmsPsuAisClient cmsPsuAisClient;
    private final ConsentRestClient consentRestClient;
    private final AuthRequestInterceptor authInterceptor;
    private final ObaAisConsentMapper consentMapper;
    private final ConsentReferencePolicy referencePolicy;
    private final CmsAspspConsentDataService dataService;
    private final AspspConsentDataClient aspspConsentDataClient;
    private final RedirectScaRestClient redirectScaClient;

    @Override
    public void selectScaMethod(String scaMethodId, final ConsentWorkflow workflow) {
        try {
            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
            StartScaOprTO opr = new StartScaOprTO();
            opr.setAuthorisationId(workflow.authId());
            opr.setOpType(OpTypeTO.PAYMENT);
            opr.setOprId(workflow.consentId());
            GlobalScaResponseTO response = redirectScaClient.startSca(opr).getBody();
            response = redirectScaClient.selectMethod(response.getAuthorisationId(), scaMethodId).getBody();
            workflow.storeSCAResponse(response);
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ConsentWorkflow authorizeConsent(ConsentWorkflow workflow, String authCode) {
        authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
        GlobalScaResponseTO response = redirectScaClient.validateScaCode(workflow.authId(), authCode).getBody();

        workflow.storeSCAResponse(response);
        workflow.setConsentStatus(response.isPartiallyAuthorised()
                                      ? ConsentStatus.PARTIALLY_AUTHORISED.name()
                                      : ConsentStatus.VALID.name());
        return workflow;
    }

    @Override
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

    @Override
    public void updateScaStatusConsentStatusConsentData(String psuId, ConsentWorkflow workflow) {
        // UPDATE CMS
        updateCmsAuthorizationScaStatus(workflow, psuId);
        updateAspspConsentData(workflow);
    }

    private void updateCmsAuthorizationScaStatus(ConsentWorkflow workflow, String psuId) {
        String status = workflow.getAuthResponse().getScaStatus().name();
        cmsPsuAisClient.updateAuthorisationStatus(workflow.consentId(), status,
                                                  workflow.authId(), psuId, null, null, null, DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, workflow.getScaResponse().getAuthConfirmationCode()));
    }

    private void updateAspspConsentData(ConsentWorkflow workflow) {
        CmsAspspConsentDataBase64 consentData;
        try {
            consentData = new CmsAspspConsentDataBase64(workflow.consentId(), dataService.toBase64String(workflow.getScaResponse()));
        } catch (IOException e) {
            throw AuthorizationException.builder()
                      .errorCode(CONSENT_DATA_UPDATE_FAILED)
                      .devMessage("Consent data update failed")
                      .build();
        }
        aspspConsentDataClient.updateAspspConsentData(workflow.getConsentReference().getEncryptedConsentId(), consentData);
    }

    @Override
    public void startConsent(final ConsentWorkflow workflow, AisConsentTO aisConsent, List<AccountDetailsTO> listOfAccounts) {
        // Map the requested access and push it to the consent management system.
        AisAccountAccess accountAccess = consentMapper.accountAccess(aisConsent.getAccess(), listOfAccounts);
        CmsAisConsentAccessRequest accountAccessRequest = new CmsAisConsentAccessRequest(accountAccess, aisConsent.getValidUntil(), aisConsent.getFrequencyPerDay(), false, aisConsent.isRecurringIndicator());
        cmsPsuAisClient.putAccountAccessInConsent(workflow.consentId(), accountAccessRequest, DEFAULT_SERVICE_INSTANCE_ID);

        // Prepare consent object for ledger
        AisConsentTO consent = consentMapper.toTo(workflow.getConsentResponse().getAccountConsent());
        consent.setAccess(aisConsent.getAccess());
        workflow.getAuthResponse().setConsent(consent);

        authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
        SCAConsentResponseTO initResponse = consentRestClient.initiateAisConsent(workflow.consentId(), consent).getBody();
        GlobalScaResponseTO map = dataService.mapToGlobalResponse(initResponse, OpTypeTO.CONSENT);
        workflow.storeSCAResponse(map);
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
    @Override
    public ConsentWorkflow identifyConsent(String encryptedConsentId, String authorizationId, boolean strict, String consentCookieString, BearerTokenTO bearerToken) {
        // Parse and verify the consent cookie.
        ConsentReference consentReference = referencePolicy.fromRequest(encryptedConsentId, authorizationId, consentCookieString, strict);

        CmsAisConsentResponse cmsConsentResponse = loadConsentByRedirectId(consentReference);

        ConsentWorkflow workflow = new ConsentWorkflow(cmsConsentResponse, consentReference);
        AisConsentTO aisConsentTO = consentMapper.toTo(cmsConsentResponse.getAccountConsent());

        workflow.setAuthResponse(new ConsentAuthorizeResponse(aisConsentTO));
        workflow.getAuthResponse().setAuthorisationId(cmsConsentResponse.getAuthorisationId());
        workflow.getAuthResponse().setEncryptedConsentId(encryptedConsentId);
        if (bearerToken != null) {
            SCAConsentResponseTO scaConsentResponseTO = new SCAConsentResponseTO();
            scaConsentResponseTO.setBearerToken(bearerToken);
            workflow.setScaResponse(dataService.mapToGlobalResponse(scaConsentResponseTO, OpTypeTO.CONSENT));
        }
        return workflow;
    }

    private CmsAisConsentResponse loadConsentByRedirectId(ConsentReference consentReference) {
        String redirectId = consentReference.getRedirectId();
        // 4. After user login:
        ResponseEntity<CmsAisConsentResponse> responseEntity = cmsPsuAisClient.getConsentIdByRedirectId(redirectId, DEFAULT_SERVICE_INSTANCE_ID);
        return responseEntity.getBody();
    }

    private boolean requestedConsentWithBalance(ConsentWorkflow workflow) {
        return StringUtils.isNotBlank(workflow.getConsentResponse().getAccountConsent().getAccess().getAvailableAccountsWithBalance());
    }
}

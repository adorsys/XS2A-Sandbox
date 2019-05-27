package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.rest.exception.ForbiddenRestException;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.*;
import de.adorsys.ledgers.oba.rest.api.exception.ConsentAuthorizeException;
import de.adorsys.ledgers.oba.rest.api.resource.AISApi;
import de.adorsys.ledgers.oba.rest.server.mapper.AisConsentMapper;
import de.adorsys.ledgers.oba.rest.server.mapper.CreatePiisConsentRequestMapper;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisConsentAccessRequest;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient;
import org.adorsys.ledgers.consent.aspsp.rest.client.CreatePiisConsentRequest;
import org.adorsys.ledgers.consent.aspsp.rest.client.CreatePiisConsentResponse;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController(AISController.BASE_PATH)
@RequestMapping(AISController.BASE_PATH)
@Api(value = AISController.BASE_PATH, tags = "PSU AIS", description = "Provides access to online banking account functionality")
@SuppressWarnings("PMD.TooManyMethods")
public class AISController extends AbstractXISController implements AISApi {

    private static final Logger logger = LoggerFactory.getLogger(AISController.class);

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private UserMgmtRestClient userMgmtRestClient;
    @Autowired
    private AuthRequestInterceptor authInterceptor;
    @Autowired
    private CmsPsuAisClient cmsPsuAisClient;
    @Autowired
    private AisConsentMapper consentMapper;
    @Autowired
    private ConsentRestClient consentRestClient;
    @Autowired
    private CmsAspspPiisClient cmsAspspPiisClient;
    @Autowired
    private CreatePiisConsentRequestMapper createPiisConsentRequestMapper;
    @Autowired
    private AccountRestClient accountRestClient;

    //TODO remove and refactor https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/43
    private ScaStatusTO scaStatus;
    private String tppNokRedirectUri;
    private String tppOkRedirectUri;

    @Override
    @ApiOperation(value = "Entry point for authenticating ais consent requests.")
    public ResponseEntity<AuthorizeResponse> aisAuth(String redirectId,
                                                     String encryptedConsentId) {

        logger.debug("encryptedConsentId: {}", encryptedConsentId);

        return auth(redirectId, ConsentType.AIS, encryptedConsentId, request, response);
    }

    @Override
    public String getBasePath() {
        return BASE_PATH;
    }

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
    public ResponseEntity<ConsentAuthorizeResponse> login(String encryptedConsentId, String authorisationId,
                                                          String login, String pin, String consentCookieString) {

        // Verify request parameter against cookie. encryptedConsentId and authorisationId must
        // match value stored in the cookie.
        // The load initiated consent from consent database, and store it in the response.
        // Also hold Bearer Token in the consent workflow if any.

        ConsentWorkflow workflow;
        try {
            workflow = identifyConsent(encryptedConsentId, authorisationId, false, consentCookieString, login, response,
                                       null);

            logger.debug("Login: {}", login);


            CmsAisConsentResponse consent = workflow.getConsentResponse();

            logger.debug("authorisationId: {}", consent.getAuthorisationId());

            tppNokRedirectUri = consent.getTppNokRedirectUri();
            tppOkRedirectUri = consent.getTppOkRedirectUri();
            scaStatus = ScaStatusTO.RECEIVED;

        } catch (ConsentAuthorizeException e) {
            return e.getError();
        }

        ResponseEntity<SCALoginResponseTO> authoriseForConsent;
        try {
            // Start the authorization process for the consentId/authorizationId.
            authoriseForConsent = userMgmtRestClient.authoriseForConsent(login, pin,
                                                                         workflow.consentId(), workflow.authId(), OpTypeTO.CONSENT);
        } catch (FeignException e) {
            if (e.status() == 404 || e.status() == 401) {
                // TODO QUESTION: What do we do when we receive a wrong user?
                // Suggestion, set the content reference cookie so use can proceed with
                // a second login.
                responseUtils.setCookies(response, workflow.getConsentReference(),
                                         null, null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(workflow.getAuthResponse());
            } else if (e.status() == 403) {
                // TODO QUESTION: What do we do when the user password is false?
                // My current response: I think we have to set PSU SCA status to PSUIDENTIFIED.
                try {
                    workflow.getAuthResponse().setScaStatus(ScaStatusTO.PSUIDENTIFIED);

                    logger.debug("403 Error: {}", ScaStatusTO.PSUIDENTIFIED);

                    // Store the id of the psu
                    updatePSUIdentification(workflow, login);
                    // Store the SCA Status
                    scaStatus(workflow, login, response);
                    responseUtils.setCookies(response, workflow.getConsentReference(),
                                             null, null);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(workflow.getAuthResponse());
                } catch (ConsentAuthorizeException e1) {
                    return e1.getError();
                }
            }
            throw e;
        }

        storeSCAResponseIntoWorkflow(workflow, authoriseForConsent.getBody());
        boolean success = AuthUtils.success(authoriseForConsent);

        if (success) {

            String psuId = AuthUtils.psuId(workflow.bearerToken());
            try {
                updatePSUIdentification(workflow, psuId);
                updateScaStatusConsentStatusConsentData(psuId, workflow);
            } catch (ConsentAuthorizeException e) {
                return e.getError();
            }

            switch (workflow.scaStatus()) {
                case EXEMPTED:
                    // Bad request
                    // failed Message. No repeat. Delete cookies.
                    responseUtils.removeCookies(response);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                case PSUIDENTIFIED:
                case FINALISED:
                case PSUAUTHENTICATED:
                case SCAMETHODSELECTED:
                    List<AccountDetailsTO> listOfAccounts = listOfAccounts(workflow);
                    workflow.getAuthResponse().setAccounts(listOfAccounts);
                    responseUtils.setCookies(response, workflow.getConsentReference(),
                                             workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
                    return ResponseEntity.ok(workflow.getAuthResponse());
                case STARTED:
                case FAILED:
                default:
                    // failed Message. No repeat. Delete cookies.
                    responseUtils.removeCookies(response);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            // failed Message. No repeat. Keep Cookies so we can repeat login.
            // responseUtils.removeCookies(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> startConsentAuth(
        String encryptedConsentId,
        String authorisationId,
        String consentAndaccessTokenCookieString,
        AisConsentTO aisConsent) {
        String psuId = AuthUtils.psuId(auth);

        ConsentWorkflow workflow;
        List<AccountDetailsTO> listOfAccounts;
        try {
            workflow = identifyConsent(encryptedConsentId, authorisationId, false, consentAndaccessTokenCookieString, psuId, response,
                                       auth.getBearerToken());
            listOfAccounts = listOfAccounts(workflow);
            startConsent(workflow, aisConsent, listOfAccounts);
            updateScaStatusConsentStatusConsentData(psuId, workflow);
        } catch (ConsentAuthorizeException e) {
            return e.getError();
        }

        scaStatus = workflow.getAuthResponse().getScaStatus();

        switch (workflow.scaStatus()) {
            case EXEMPTED:
                // Bad request
                // failed Message. No repeat. Delete cookies.
                responseUtils.removeCookies(response);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            case PSUIDENTIFIED:
            case FINALISED:
            case PSUAUTHENTICATED:
            case SCAMETHODSELECTED:
                workflow.getAuthResponse().setAccounts(listOfAccounts);
                responseUtils.setCookies(response, workflow.getConsentReference(),
                                         workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
                return ResponseEntity.ok(workflow.getAuthResponse());
            case STARTED:
            case FAILED:
            default:
                // failed Message. No repeat. Delete cookies.
                responseUtils.removeCookies(response);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> authrizedConsent(
        String encryptedConsentId,
        String authorisationId,
        String consentAndaccessTokenCookieString, String authCode) {

        String psuId = AuthUtils.psuId(auth);
        try {
            ConsentWorkflow workflow = identifyConsent(encryptedConsentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            SCAConsentResponseTO scaConsentResponse = consentRestClient.authorizeConsent(workflow.consentId(), authorisationId, authCode).getBody();

            storeSCAResponseIntoWorkflow(workflow, scaConsentResponse);
            cmsPsuAisClient.confirmConsent(workflow.consentId(), psuId, null, null, null, CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID);
            updateScaStatusConsentStatusConsentData(psuId, workflow);

            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());

            scaStatus = workflow.getAuthResponse().getScaStatus();
            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (ConsentAuthorizeException e) {
            return e.getError();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> selectMethod(
        String encryptedConsentId, String authorisationId,
        String scaMethodId, String consentAndaccessTokenCookieString) {

        String psuId = AuthUtils.psuId(auth);
        try {
            ConsentWorkflow workflow = identifyConsent(encryptedConsentId, authorisationId, true,
                                                       consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());
            selectMethod(scaMethodId, workflow);

            updateScaStatusConsentStatusConsentData(psuId, workflow);

            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(),
                                     workflow.bearerToken().getAccessTokenObject());
            scaStatus = workflow.getAuthResponse().getScaStatus();

            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (ConsentAuthorizeException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PIISConsentCreateResponse> grantPiisConsent(String consentAndaccessTokenCookieString, CreatePiisConsentRequestTO piisConsentRequestTO) {

        String psuId = AuthUtils.psuId(auth);
        try {

            authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());

            CreatePiisConsentRequest piisConsentRequest = createPiisConsentRequestMapper.fromCreatePiisConsentRequest(piisConsentRequestTO);
            CreatePiisConsentResponse cmsCcnsent = cmsAspspPiisClient.createConsent(piisConsentRequest, psuId, null, null, null).getBody();

            // Attention intentional manual mapping. We fill up only the balances.
            AisConsentTO pisConsent = new AisConsentTO();
            AisAccountAccessInfoTO access = new AisAccountAccessInfoTO();
            // Only consent we take.
            access.setBalances(piisConsentRequest.getAccounts().stream().map(a -> a.getIban()).collect(Collectors.toList()));
            pisConsent.setAccess(access);
            pisConsent.setFrequencyPerDay(piisConsentRequest.getAllowedFrequencyPerDay());
            pisConsent.setId(cmsCcnsent.getConsentId());
            // Intentionally set to true
            pisConsent.setRecurringIndicator(true);
            pisConsent.setTppId(piisConsentRequest.getTppInfo().getAuthorisationNumber());
            pisConsent.setUserId(psuId);
            pisConsent.setValidUntil(piisConsentRequest.getValidUntil());

            SCAConsentResponseTO scaConsentResponse = consentRestClient.grantPIISConsent(pisConsent).getBody();
            ResponseEntity<?> updateAspspPiisConsentDataResponse = updateAspspPiisConsentData(cmsCcnsent.getConsentId(), scaConsentResponse);
            if (!HttpStatus.OK.equals(updateAspspPiisConsentDataResponse.getStatusCode())) {
                return responseUtils.error(new PIISConsentCreateResponse(), updateAspspPiisConsentDataResponse.getStatusCode(),
                                           "Could not update aspsp consent data", response);
            }
            // Send back same cookie. Delete any consent reference.
            responseUtils.setCookies(response, null, auth.getBearerToken().getAccess_token(), auth.getBearerToken().getAccessTokenObject());

            AisConsentTO consent = scaConsentResponse.getBearerToken().getAccessTokenObject().getConsent();
            return ResponseEntity.ok(new PIISConsentCreateResponse(consent));
        } catch (IOException e) {
            return responseUtils.error(new PIISConsentCreateResponse(), HttpStatus.INTERNAL_SERVER_ERROR,
                                       e.getMessage(), response);
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<List<AccountDetailsTO>> getListOfAccounts(String accessTokenCookieString) throws ForbiddenRestException {
        try {
            // Set access token
            authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());
            ResponseEntity<List<AccountDetailsTO>> listOfAccounts = accountRestClient.getListOfAccounts();
            return ResponseEntity.ok(listOfAccounts.getBody());
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> aisDone(String encryptedConsentId, String authorisationId,
                                                            String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp) {
        String redirectURL = ScaStatusTO.FINALISED.equals(scaStatus)
                                 ? tppOkRedirectUri
                                 : tppNokRedirectUri;

        return responseUtils.redirect(redirectURL, response);
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> revokeConsent(@NotNull String encryptedConsentId, @NotNull String authorisationId, String cookieString) {
        String psuId = AuthUtils.psuId(auth);
        ConsentWorkflow workflow;

        try {
            workflow = getConsentWorkflow(encryptedConsentId, authorisationId, cookieString, psuId);
        } catch (ConsentAuthorizeException e) {
            return ResponseEntity.badRequest().build();
        }

        if (failAuthorisation(workflow.encryptedConsentId(), psuId, authorisationId)) {
            return ResponseEntity.ok(buildResponseForSuccessfulConsentRevoke());
        }

        return ResponseEntity.badRequest().build();
    }

    private ConsentWorkflow getConsentWorkflow(String encryptedConsentId, String authorisationId, String cookieString, String psuId) throws ConsentAuthorizeException {
        ConsentWorkflow workflow = identifyConsent(encryptedConsentId, authorisationId, true, cookieString, psuId, response,
                                                   auth.getBearerToken());
        authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());

        return workflow;
    }

    private boolean failAuthorisation(String encryptedConsentId, String psuId, String authorisationId) {
        ResponseEntity<Boolean> updateAuthorisationStatusResponse = cmsPsuAisClient.updateAuthorisationStatus(encryptedConsentId,
                                                                                                              "FAILED", authorisationId, psuId, null, null, null,
                                                                                                              CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID);

        return updateAuthorisationStatusResponse.getStatusCode() == HttpStatus.OK;
    }

    private ConsentAuthorizeResponse buildResponseForSuccessfulConsentRevoke() {
        ConsentAuthorizeResponse consentAuthorisationResponse = new ConsentAuthorizeResponse();
        consentAuthorisationResponse.setScaStatus(ScaStatusTO.EXEMPTED);
        consentAuthorisationResponse.setAccounts(Collections.emptyList());

        AisConsentTO consent = new AisConsentTO();
        AisAccountAccessInfoTO access = new AisAccountAccessInfoTO();

        access.setBalances(Collections.emptyList());
        access.setAccounts(Collections.emptyList());
        access.setTransactions(Collections.emptyList());
        consent.setAccess(access);

        consentAuthorisationResponse.setConsent(consent);

        return consentAuthorisationResponse;
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
     *
     * ToDo: Check if we have to hash consent and authorizationId. CSRF.
     */
    private ConsentWorkflow identifyConsent(String encryptedConsentId, String authorizationId, boolean strict,
                                            String consentCookieString, String psuId, HttpServletResponse response, BearerTokenTO bearerToken)
        throws ConsentAuthorizeException {

        // Parse and verify the consent cookie.
        ConsentReference consentReference = null;
        try {
            String consentCookie = responseUtils.consentCookie(consentCookieString);
            consentReference = referencePolicy.fromRequest(encryptedConsentId, authorizationId, consentCookie, strict);
        } catch (InvalidConsentException e) {
            throw new ConsentAuthorizeException(responseUtils.forbidden(authResp(), e.getMessage(), response));
        }

        CmsAisConsentResponse cmsConsentResponse = loadConsentByRedirectId(psuId, consentReference, response);

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

    private void storeSCAResponseIntoWorkflow(ConsentWorkflow workflow, SCAResponseTO consentResponse) {
        workflow.setScaResponse(consentResponse);
        workflow.getAuthResponse().setAuthorisationId(consentResponse.getAuthorisationId());
        workflow.getAuthResponse().setScaStatus(consentResponse.getScaStatus());
        workflow.getAuthResponse().setScaMethods(consentResponse.getScaMethods());
        workflow.setAuthCodeMessage(consentResponse.getPsuMessage());
    }

    private void scaStatus(ConsentWorkflow workflow, String psuId, HttpServletResponse response)
        throws ConsentAuthorizeException {
        String status = workflow.getAuthResponse().getScaStatus().name();
        ResponseEntity<Boolean> resp = cmsPsuAisClient.updateAuthorisationStatus(workflow.consentId(), status,
                                                                                 workflow.authId(), psuId, null, null, null, CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID);
        if (!HttpStatus.OK.equals(resp.getStatusCode())) {
            throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(),
                                                                                     "Error updating authorisation status. See error code.", resp.getStatusCode(), response));
        }
    }

    private void updatePSUIdentification(ConsentWorkflow workflow, String psuId) throws ConsentAuthorizeException {
        PsuIdData psuIdData = new PsuIdData(psuId, null, null, null);
        ResponseEntity<Void> resp = cmsPsuAisClient.updatePsuDataInConsent(workflow.consentId(), workflow.authId(),
                                                                           CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID, psuIdData);
        if (!HttpStatus.OK.equals(resp.getStatusCode())) {
            throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(),
                                                                                     "Error updating psu identification. See error code.", resp.getStatusCode(), response));
        }
    }

    private ConsentAuthorizeResponse authResp() {
        return new ConsentAuthorizeResponse();
    }

    private void startConsent(final ConsentWorkflow workflow, AisConsentTO aisConsent, List<AccountDetailsTO> listOfAccounts)
        throws ConsentAuthorizeException {
        try {
            // Map the requested access and push it to the consent management system.
            AisAccountAccess accountAccess = consentMapper.accountAccess(aisConsent.getAccess(), listOfAccounts);
            CmsAisConsentAccessRequest accountAccessRequest = new CmsAisConsentAccessRequest(accountAccess, aisConsent.getValidUntil(), aisConsent.getFrequencyPerDay());
            cmsPsuAisClient.putAccountAccessInConsent(workflow.consentId(), accountAccessRequest);

            // Prepare consent object for ledger
            AisConsentTO consent = consentMapper.toTo(workflow.getConsentResponse().getAccountConsent());
            consent.setAccess(aisConsent.getAccess());
            workflow.getAuthResponse().setConsent(consent);

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
            SCAConsentResponseTO sca = consentRestClient.startSCA(workflow.consentId(), consent).getBody();

            // Store sca response in workflow.
            // TODO: CHeck why. INFO. Server does not set the bearer token.
            sca.setBearerToken(workflow.bearerToken()); // copy bearer from old sca object.
            storeSCAResponseIntoWorkflow(workflow, sca);
        } catch (FeignException f) {
            workflow.setErrorCode(HttpStatus.valueOf(f.status()));
            throw f;
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    private SCAConsentResponseTO selectMethod(String scaMethodId, final ConsentWorkflow workflow) {
        try {
            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
            // INFO. Server does not set the bearer token.
            BearerTokenTO bearerToken = workflow.bearerToken();
            SCAConsentResponseTO sca = consentRestClient.selectMethod(workflow.consentId(), workflow.authId(), scaMethodId).getBody();
            // INFO. Server does not set the bearer token.
            sca.setBearerToken(bearerToken);
            storeSCAResponseIntoWorkflow(workflow, sca);
            return sca;

        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    private void updateScaStatusConsentStatusConsentData(String psuId, ConsentWorkflow workflow)
        throws ConsentAuthorizeException {
        // UPDATE CMS
        scaStatus(workflow, psuId, response);
        updateAspspConsentData(workflow, response);
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

    private ResponseEntity<?> updateAspspPiisConsentData(String consentId, SCAConsentResponseTO consentResponse) throws IOException {
        CmsAspspConsentDataBase64 consentData = new CmsAspspConsentDataBase64(consentId, tokenStorageService.toBase64String(consentResponse));
        // Encrypted consentId???
        return aspspConsentDataClient.updateAspspConsentData(consentId, consentData);
    }


    @SuppressWarnings("PMD.CyclomaticComplexity")
    private CmsAisConsentResponse loadConsentByRedirectId(String psuId,
                                                          ConsentReference consentReference, HttpServletResponse response) throws ConsentAuthorizeException {
        String psuIdType = null;
        String psuCorporateId = null;
        String psuCorporateIdType = null;
        String redirectId = consentReference.getRedirectId();
        // 4. After user login:
        ResponseEntity<CmsAisConsentResponse> responseEntity = cmsPsuAisClient.getConsentByRedirectId(
            psuId, psuIdType, psuCorporateId, psuCorporateIdType, redirectId, CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID);

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
        } else if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), responseEntity.getStatusCode(), response));
        }

        throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), statusCode, response));
    }

    /*
     * Loads the list of accounts from the ledgers.
     *
     * We assume the access token needed to authenticate with the server is contained in the workflow object.
     * It is the responsibility of the caller to make sure the workflow ist propertly filled with a bearer token.
     */
    private List<AccountDetailsTO> listOfAccounts(ConsentWorkflow workflow) {
        try {
            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
            return accountRestClient.getListOfAccounts().getBody();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

}

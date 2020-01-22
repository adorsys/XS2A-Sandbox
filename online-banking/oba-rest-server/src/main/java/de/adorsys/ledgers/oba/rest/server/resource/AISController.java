package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.AISApi;
import de.adorsys.ledgers.oba.rest.api.resource.exception.ConsentAuthorizeException;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.*;
import de.adorsys.ledgers.oba.service.api.service.ConsentService;
import de.adorsys.ledgers.oba.service.api.service.RedirectConsentService;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.*;
import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.PARTIALLY_AUTHORISED;
import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.VALID;
import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID;

@Slf4j
@RestController
@RequestMapping(AISApi.BASE_PATH)
@Api(value = AISApi.BASE_PATH, tags = "PSU AIS. Provides access to online banking account functionality")
@SuppressWarnings("PMD.TooManyMethods")
@RequiredArgsConstructor
public class AISController implements AISApi {
    private final CmsPsuAisClient cmsPsuAisClient;
    private final ConsentRestClient consentRestClient;
    private final AccountRestClient accountRestClient;
    private final OauthRestClient oauthRestClient;
    private final RedirectConsentService redirectConsentService;
    private final ConsentService consentService;
    private final XISControllerService xisService;
    private final HttpServletResponse response;
    private final ResponseUtils responseUtils;
    private final ObaMiddlewareAuthentication middlewareAuth;
    private final AuthRequestInterceptor authInterceptor;
    private final AspspConsentDataClient aspspConsentDataClient;
    private final TokenStorageService tokenStorageService;

    @Override
    @ApiOperation(value = "Entry point for authenticating ais consent requests.")
    public ResponseEntity<AuthorizeResponse> aisAuth(String redirectId, String encryptedConsentId, String token) {
        return xisService.auth(redirectId, ConsentType.AIS, encryptedConsentId, response);
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> login(String encryptedConsentId, String authorisationId, String login, String pin, String consentCookieString) {
        // Verify request parameter against cookie. encryptedConsentId and authorisationId must
        // match value stored in the cookie.
        // The load initiated consent from consent database, and store it in the response.
        // Also hold Bearer Token in the consent workflow if any.

        ConsentWorkflow workflow;
        try {
            String consentCookie = responseUtils.consentCookie(consentCookieString);
            workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, false, consentCookie, null);
        } catch (ConsentAuthorizeException e) {
            return e.getError();
        }

        ResponseEntity<SCALoginResponseTO> loginResult = xisService.performLoginForConsent(login, pin, workflow.consentId(), workflow.authId(), OpTypeTO.CONSENT);
        AuthUtils.checkIfUserInitiatedOperation(loginResult, workflow.getConsentResponse().getAccountConsent().getPsuIdDataList());
        workflow.storeSCAResponse(loginResult.getBody());

        if (AuthUtils.success(loginResult)) {
            String psuId = AuthUtils.psuId(workflow.bearerToken());
            try {
                updatePSUIdentification(workflow, psuId);
                redirectConsentService.updateScaStatusConsentStatusConsentData(psuId, workflow);
            } catch (ConsentAuthorizeException e) {
                return e.getError();
            }
            return resolveResponseByScaStatus(workflow, true);
        } else {
            // failed Message. No repeat. Keep Cookies so we can repeat login.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> startConsentAuth(String encryptedConsentId, String authorisationId, String consentAndAccessTokenCookieString, AisConsentTO aisConsent) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        ConsentWorkflow workflow;
        List<AccountDetailsTO> listOfAccounts;
        try {
            String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);
            workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, false, consentCookie, middlewareAuth.getBearerToken());
            listOfAccounts = listOfAccounts(workflow);
            redirectConsentService.startConsent(workflow, aisConsent, listOfAccounts);
            redirectConsentService.updateScaStatusConsentStatusConsentData(psuId, workflow);
        } catch (ConsentAuthorizeException e) {
            return e.getError();
        }

        return resolveResponseByScaStatus(workflow, false);
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> authrizedConsent(String encryptedConsentId, String authorisationId, String consentAndAccessTokenCookieString, String authCode) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);
            ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, true, consentCookie, middlewareAuth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
            SCAConsentResponseTO scaConsentResponse = consentRestClient.authorizeConsent(workflow.consentId(), authorisationId, authCode).getBody();
            workflow.storeSCAResponse(scaConsentResponse);

            cmsPsuAisClient.confirmConsent(workflow.consentId(), psuId, null, null, null, DEFAULT_SERVICE_INSTANCE_ID);
            redirectConsentService.updateScaStatusConsentStatusConsentData(psuId, workflow);

            // if consent is partially authorized the access token is null
            Optional<BearerTokenTO> token = Optional.ofNullable(workflow.bearerToken());
            String tokenString = token.map(BearerTokenTO::getAccess_token).orElseGet(() -> "");
            AccessTokenTO tokenTO = token.map(BearerTokenTO::getAccessTokenObject).orElse(null);
            responseUtils.setCookies(response, workflow.getConsentReference(), tokenString, tokenTO);

            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (ConsentAuthorizeException e) {
            return e.getError();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> selectMethod(String encryptedConsentId, String authorisationId, String scaMethodId, String consentAndAccessTokenCookieString) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);
            ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, true, consentCookie, middlewareAuth.getBearerToken());
            redirectConsentService.selectScaMethod(scaMethodId, workflow);

            redirectConsentService.updateScaStatusConsentStatusConsentData(psuId, workflow);

            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());

            return ResponseEntity.ok(workflow.getAuthResponse());
        } catch (ConsentAuthorizeException e) {
            return e.getError();
        }
    }

    @Override
    public ResponseEntity<PIISConsentCreateResponse> grantPiisConsent(String consentAndaccessTokenCookieString, CreatePiisConsentRequestTO piisConsentRequestTO) {

        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            authInterceptor.setAccessToken(middlewareAuth.getBearerToken().getAccess_token());
            SCAConsentResponseTO scaConsentResponse = consentService.createConsent(piisConsentRequestTO, psuId);

            ResponseEntity<?> updateAspspPiisConsentDataResponse = updateAspspPiisConsentData(scaConsentResponse.getConsentId(), scaConsentResponse);
            if (!HttpStatus.OK.equals(updateAspspPiisConsentDataResponse.getStatusCode())) {
                return responseUtils.error(new PIISConsentCreateResponse(), updateAspspPiisConsentDataResponse.getStatusCode(),
                    "Could not update aspsp consent data", response);
            }
            // Send back same cookie. Delete any consent reference.
            responseUtils.setCookies(response, null, middlewareAuth.getBearerToken().getAccess_token(), middlewareAuth.getBearerToken().getAccessTokenObject());

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
    public ResponseEntity<List<AccountDetailsTO>> getListOfAccounts(String accessTokenCookieString) {
        try {
            // Set access token
            authInterceptor.setAccessToken(middlewareAuth.getBearerToken().getAccess_token());
            ResponseEntity<List<AccountDetailsTO>> listOfAccounts = accountRestClient.getListOfAccounts();
            return ResponseEntity.ok(listOfAccounts.getBody());
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> aisDone(String encryptedConsentId, String authorisationId, String consentAndAccessTokenCookieString, Boolean forgetConsent, Boolean backToTpp, boolean isOauth2Integrated) {
        String consentCookie = responseUtils.consentCookie(consentAndAccessTokenCookieString);
        ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, true, consentCookie, middlewareAuth.getBearerToken());

        ConsentStatus consentStatus = workflow.getConsentResponse().getAccountConsent().getConsentStatus();
        CmsAisConsentResponse consentResponse = workflow.getConsentResponse();
        authInterceptor.setAccessToken(workflow.getScaResponse().getBearerToken().getAccess_token());
        String tppOkRedirectUri = isOauth2Integrated
                                      ? oauthRestClient.oauthCode(consentResponse.getTppOkRedirectUri()).getBody().getRedirectUri()
                                      : consentResponse.getTppOkRedirectUri();
        String tppNokRedirectUri = consentResponse.getTppNokRedirectUri();

        String redirectURL = EnumSet.of(VALID, PARTIALLY_AUTHORISED).contains(consentStatus)
                                 ? tppOkRedirectUri
                                 : tppNokRedirectUri;

        return responseUtils.redirect(redirectURL, response);
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> revokeConsent(@NotNull String encryptedConsentId, @NotNull String authorisationId, String cookieString) {
        ConsentWorkflow workflow;
        try {
            String consentCookie = responseUtils.consentCookie(cookieString);
            workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, true, consentCookie, middlewareAuth.getBearerToken());
            authInterceptor.setAccessToken(middlewareAuth.getBearerToken().getAccess_token());
        } catch (ConsentAuthorizeException e) {
            return ResponseEntity.badRequest().build();
        }

        String psuId = AuthUtils.psuId(middlewareAuth);
        if (failAuthorisation(workflow.consentId(), psuId, authorisationId)) {
            return ResponseEntity.ok(buildResponseForSuccessfulConsentRevoke());
        }

        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<ConsentAuthorizeResponse> resolveResponseByScaStatus(ConsentWorkflow workflow, boolean isLoginOperation) {
        ScaStatusTO scaStatusTO = workflow.scaStatus();
        if (scaStatusTO == EXEMPTED) {// Bad request
            // failed Message. No repeat. Delete cookies.
            responseUtils.removeCookies(response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (EnumSet.of(PSUIDENTIFIED, FINALISED, PSUAUTHENTICATED, SCAMETHODSELECTED).contains(scaStatusTO)) {
            List<AccountDetailsTO> listOfAccounts = listOfAccounts(workflow);
            workflow.getAuthResponse().setAccounts(listOfAccounts);
            if (isLoginOperation) {
                // update consent accounts, transactions and balances if global consent flag is set
                redirectConsentService.updateAccessByConsentType(workflow, listOfAccounts);
            }
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        }// failed Message. No repeat. Delete cookies.
        responseUtils.removeCookies(response);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean failAuthorisation(String consentId, String psuId, String authorisationId) {
        ResponseEntity updateAuthorisationStatusResponse = cmsPsuAisClient.updateAuthorisationStatus(consentId,
            "FAILED", authorisationId, psuId, null, null, null,
            DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, null));

        return updateAuthorisationStatusResponse.getStatusCode() == HttpStatus.OK;
    }

    private ConsentAuthorizeResponse buildResponseForSuccessfulConsentRevoke() {
        ConsentAuthorizeResponse consentAuthorisationResponse = new ConsentAuthorizeResponse();
        consentAuthorisationResponse.setScaStatus(EXEMPTED);
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


    private void updatePSUIdentification(ConsentWorkflow workflow, String psuId) {
        PsuIdData psuIdData = new PsuIdData(psuId, null, null, null, null);
        ResponseEntity resp = cmsPsuAisClient.updatePsuDataInConsent(workflow.consentId(), workflow.authId(),
            DEFAULT_SERVICE_INSTANCE_ID, psuIdData);
        if (!HttpStatus.OK.equals(resp.getStatusCode())) {
            throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(),
                "Error updating psu identification. See error code.", resp.getStatusCode(), response));
        }
    }

    private ConsentAuthorizeResponse authResp() {
        return new ConsentAuthorizeResponse();
    }

    private ResponseEntity<?> updateAspspPiisConsentData(String consentId, SCAConsentResponseTO consentResponse) throws IOException {
        CmsAspspConsentDataBase64 consentData = new CmsAspspConsentDataBase64(consentId, tokenStorageService.toBase64String(consentResponse));
        // Encrypted consentId???
        return aspspConsentDataClient.updateAspspConsentData(consentId, consentData);
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

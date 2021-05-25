package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.AISApi;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.ConsentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.AuthorizationService;
import de.adorsys.ledgers.oba.service.api.service.RedirectConsentService;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import de.adorsys.psd2.consent.api.ais.AisAccountConsentAuthorisation;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import feign.FeignException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPiisV2Client;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.EXEMPTED;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.FINALISED;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.PSUAUTHENTICATED;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.PSUIDENTIFIED;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.SCAMETHODSELECTED;
import static de.adorsys.psd2.consent.aspsp.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;
import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.PARTIALLY_AUTHORISED;
import static de.adorsys.psd2.xs2a.core.consent.ConsentStatus.VALID;
import static java.util.Objects.requireNonNull;

@Slf4j
@RestController
@RequestMapping(AISApi.BASE_PATH)
@Api(value = AISApi.BASE_PATH, tags = "PSU AIS. Provides access to online banking account functionality")
@SuppressWarnings({"PMD.TooManyMethods", "PMD.TooManyStaticImports"})
@RequiredArgsConstructor
public class AISController implements AISApi {
    private final CmsPsuAisClient cmsPsuAisClient;
    private final CmsPsuPiisV2Client cmsPsuPiisV2Client;
    private final AccountRestClient accountRestClient;
    private final OauthRestClient oauthRestClient;
    private final RedirectConsentService redirectConsentService;
    private final XISControllerService xisService;
    private final HttpServletResponse response;
    private final ResponseUtils responseUtils;
    private final MiddlewareAuthentication middlewareAuth;
    private final AuthRequestInterceptor authInterceptor;
    private final AuthorizationService authService;
    private final TokenAuthenticationService authenticationService;

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> login(String encryptedConsentId, String authorisationId, String login, String pin) {
        ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, null);
        xisService.checkFailedCount(encryptedConsentId);

        // Authorize
        try {
            GlobalScaResponseTO ledgersResponse = authenticationService.login(login, pin, authorisationId);
            workflow.storeSCAResponse(ledgersResponse);
            AuthUtils.checkIfUserInitiatedOperation(ledgersResponse, workflow.getConsentResponse().getAccountConsent().getPsuIdDataList());
        } catch (FeignException | ObaException e) {
            xisService.resolveFailedLoginAttempt(encryptedConsentId, workflow.consentId(), login, workflow.authId(), OpTypeTO.CONSENT);
        }

        String psuId = AuthUtils.psuId(workflow.bearerToken());
        updatePSUIdentification(workflow, psuId);
        redirectConsentService.updateScaStatusAndConsentData(psuId, workflow);
        return resolveResponseByScaStatus(workflow, true);

    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> startConsentAuth(String encryptedConsentId, String authorisationId, AisConsentTO aisConsent) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, middlewareAuth.getBearerToken());
        List<AccountDetailsTO> listOfAccounts = listOfAccounts(workflow);
        redirectConsentService.startConsent(workflow, aisConsent, listOfAccounts);
        redirectConsentService.updateScaStatusAndConsentData(psuId, workflow);
        return resolveResponseByScaStatus(workflow, false);
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> authrizedConsent(String encryptedConsentId, String authorisationId, String authCode) {
        String psuId = AuthUtils.psuId(middlewareAuth);

        ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, middlewareAuth.getBearerToken());

        workflow = redirectConsentService.authorizeConsent(workflow, authCode);

        if (workflow.getConsentStatus().equals(VALID.name())) {
            boolean isConfirmedConsent = Optional.ofNullable(cmsPsuAisClient.confirmConsent(workflow.consentId(), DEFAULT_SERVICE_INSTANCE_ID).getBody()).orElse(false);
            if (!isConfirmedConsent) { //TODO This is a workaround! Should be fixed with separate OBA controller set!
                log.info("Consent not found so assume we have a PIIS consent");
                cmsPsuPiisV2Client.updateConsentStatus(workflow.consentId(), VALID.name(), DEFAULT_SERVICE_INSTANCE_ID);
            }
        }
        redirectConsentService.updateScaStatusAndConsentData(psuId, workflow);

        log.info("Confirmation code: {}", workflow.getAuthResponse().getAuthConfirmationCode());
        return ResponseEntity.ok(workflow.getAuthResponse());
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> selectMethod(String encryptedConsentId, String authorisationId, String scaMethodId) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, middlewareAuth.getBearerToken());
        redirectConsentService.selectScaMethod(scaMethodId, encryptedConsentId, workflow);
        redirectConsentService.updateScaStatusAndConsentData(psuId, workflow);
        responseUtils.addAccessTokenHeader(response, workflow.bearerToken().getAccess_token());
        return ResponseEntity.ok(workflow.getAuthResponse());
    }


    @Override
    public ResponseEntity<ConsentAuthorizeResponse> aisDone(String encryptedConsentId, String authorisationId, boolean isOauth2Integrated, String authConfirmationCode) {
        ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, middlewareAuth.getBearerToken());

        ConsentStatus consentStatus = workflow.getConsentResponse().getAccountConsent().getConsentStatus();
        CmsAisConsentResponse consentResponse = workflow.getConsentResponse();
        authInterceptor.setAccessToken(workflow.getScaResponse().getBearerToken().getAccess_token());
        String tppOkRedirectUri = isOauth2Integrated
            ? requireNonNull(oauthRestClient.oauthCode(consentResponse.getTppOkRedirectUri()).getBody()).getRedirectUri()
            : authService.resolveAuthConfirmationCodeRedirectUri(consentResponse.getTppOkRedirectUri(), authConfirmationCode);
        String tppNokRedirectUri = Optional.ofNullable(consentResponse.getTppNokRedirectUri())
            .filter(StringUtils::isNotBlank)
            .orElse(consentResponse.getTppOkRedirectUri());

        String redirectURL = EnumSet.of(VALID, ConsentStatus.RECEIVED, PARTIALLY_AUTHORISED).contains(consentStatus) && isNotFailedAuthorizationList(consentResponse)
                                 ? tppOkRedirectUri
                                 : tppNokRedirectUri;

        return responseUtils.redirect(redirectURL, response);
    }

    private boolean isNotFailedAuthorizationList(CmsAisConsentResponse consentResponse) {
        return consentResponse.getAccountConsent().getAccountConsentAuthorizations().stream()
                   .map(AisAccountConsentAuthorisation::getScaStatus)
                   .anyMatch(s -> s != ScaStatus.FAILED);
    }

    @Override
    public ResponseEntity<ConsentAuthorizeResponse> revokeConsent(@NotNull String encryptedConsentId, @NotNull String authorisationId) {
        ConsentWorkflow workflow = redirectConsentService.identifyConsent(encryptedConsentId, authorisationId, middlewareAuth.getBearerToken());
        authInterceptor.setAccessToken(middlewareAuth.getBearerToken().getAccess_token());

        String psuId = AuthUtils.psuId(middlewareAuth);
        if (failAuthorisation(workflow.consentId(), psuId, authorisationId)) {
            return ResponseEntity.ok(buildResponseForSuccessfulConsentRevoke());
        }

        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<ConsentAuthorizeResponse> resolveResponseByScaStatus(ConsentWorkflow workflow, boolean isLoginOperation) {
        ScaStatusTO scaStatusTO = workflow.scaStatus();
        if (scaStatusTO == EXEMPTED) {// Bad request
            // failed Message. No repeat.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (EnumSet.of(PSUIDENTIFIED, FINALISED, PSUAUTHENTICATED, SCAMETHODSELECTED).contains(scaStatusTO)) {
            List<AccountDetailsTO> listOfAccounts = listOfAccounts(workflow);
            workflow.getAuthResponse().setAccounts(listOfAccounts);
            if (isLoginOperation) {
                // update consent accounts, transactions and balances if global consent flag is set
                redirectConsentService.updateAccessByConsentType(workflow, listOfAccounts);
            }
            responseUtils.addAccessTokenHeader(response, workflow.bearerToken().getAccess_token());
            return ResponseEntity.ok(workflow.getAuthResponse());
        }// failed Message. No repeat.

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean failAuthorisation(String consentId, String psuId, String authorisationId) {
        ResponseEntity<?> updateAuthorisationStatusResponse = cmsPsuAisClient.updateAuthorisationStatus(consentId,
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
      try {
        cmsPsuAisClient.updatePsuDataInConsent(workflow.consentId(), workflow.authId(), DEFAULT_SERVICE_INSTANCE_ID, psuIdData);
      }catch (FeignException e){
          if (e.status() == HttpStatus.REQUEST_TIMEOUT.value()) {
              throw ObaException.builder()
                  .obaErrorCode(ObaErrorCode.RESOURCE_EXPIRED)
                  .devMessage("Authorisation is expired")
                  .build();

          } else {
              throw e;
          }
      }
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

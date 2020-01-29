package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
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
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.*;
import de.adorsys.ledgers.oba.service.api.service.ConsentService;
import de.adorsys.ledgers.oba.service.api.service.RedirectConsentService;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AISControllerTest {
    private static final String PIN = "12345";
    private static final String LOGIN = "anton.brueckner";
    private static final String ENCRYPTED_ID = "ENC_123";
    private static final String AUTH_ID = "AUTH_1";
    private static final String METHOD_ID = "SCA_1";
    private static final String COOKIE = "COOKIE";
    private static final String TOKEN = "TOKEN";
    private static final String OK_URI = "OK_URI";
    private static final String NOK_URI = "NOK_URI";
    private static final String CONSENT_ID = "12345";
    private static final String CODE = "xyz132";
    private static final String ASPSP_ACC_ID = "ASPSP_ACC_ID";
    private static final String RESOURCE_ID = "RESOURCE_ID";
    private static final String IBAN = "DE123456789";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final String TPP_AUTH_ID = "TPP_123";
    private static final LocalDate DATE = LocalDate.of(2020, 1, 24);

    @InjectMocks
    private AISController controller;
    @Mock
    private CmsPsuAisClient cmsPsuAisClient;
    @Mock
    private ConsentRestClient consentRestClient;
    @Mock
    private AccountRestClient accountRestClient;
    @Mock
    private OauthRestClient oauthRestClient;
    @Mock
    private RedirectConsentService redirectConsentService;
    @Mock
    private ConsentService consentService;
    @Mock
    private XISControllerService xisService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ResponseUtils responseUtils;
    @Mock
    private ObaMiddlewareAuthentication middlewareAuth;

    @Mock
    private AuthRequestInterceptor authInterceptor;

    @Mock
    private AspspConsentDataClient aspspConsentDataClient;
    @Mock
    private TokenStorageService tokenStorageService;

    @Test
    public void aisAuth() {
        when(xisService.auth(any(), any(), any(), any())).thenReturn(getAuthResponse());

        ResponseEntity<AuthorizeResponse> result = controller.aisAuth(AUTH_ID, ENCRYPTED_ID, TOKEN);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getAuthResponse());
    }

    @Test
    public void login() {
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(redirectConsentService.identifyConsent(anyString(), anyString(), anyBoolean(), anyString(), any())).thenReturn(getConsentWorkflow(PSUIDENTIFIED));
        when(xisService.performLoginForConsent(anyString(), anyString(), anyString(), anyString(), any(OpTypeTO.class))).thenReturn(getScaLoginResponse());
        when(cmsPsuAisClient.updatePsuDataInConsent(anyString(), anyString(), anyString(), any())).thenReturn(ResponseEntity.ok(null));
        when(accountRestClient.getListOfAccounts()).thenReturn(ResponseEntity.ok(new ArrayList<>()));

        ResponseEntity<ConsentAuthorizeResponse> result = controller.login(ENCRYPTED_ID, AUTH_ID, LOGIN, PIN, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getConsentAuthorizeResponse(true, true, false, PSUIDENTIFIED)));
    }

    @Test
    public void startConsentAuth() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(redirectConsentService.identifyConsent(anyString(), anyString(), anyBoolean(), anyString(), any())).thenReturn(getConsentWorkflow(PSUIDENTIFIED));
        when(accountRestClient.getListOfAccounts()).thenReturn(ResponseEntity.ok(new ArrayList<>()));

        ResponseEntity<ConsentAuthorizeResponse> result = controller.startConsentAuth(ENCRYPTED_ID, AUTH_ID, COOKIE, getAisConsentTO(false));
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getConsentAuthorizeResponse(true, true, false, PSUIDENTIFIED)));
    }

    @Test
    public void authrizedConsent() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(redirectConsentService.identifyConsent(anyString(), anyString(), anyBoolean(), anyString(), any())).thenReturn(getConsentWorkflow(FINALISED));
        when(consentRestClient.authorizeConsent(anyString(), anyString(), anyString())).thenReturn(ResponseEntity.ok(getScaConsentResponse(FINALISED)));

        ResponseEntity<ConsentAuthorizeResponse> result = controller.authrizedConsent(ENCRYPTED_ID, AUTH_ID, COOKIE, CODE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getConsentAuthorizeResponse(true, true, false, FINALISED)));
    }

    @Test
    public void selectMethod() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(redirectConsentService.identifyConsent(anyString(), anyString(), anyBoolean(), anyString(), any())).thenReturn(getConsentWorkflow(SCAMETHODSELECTED));

        ResponseEntity<ConsentAuthorizeResponse> result = controller.selectMethod(ENCRYPTED_ID, AUTH_ID, METHOD_ID, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getConsentAuthorizeResponse(true, true, false, SCAMETHODSELECTED)));
    }

    @Test
    public void grantPiisConsent() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(consentService.createConsent(any(), anyString())).thenReturn(getScaConsentResponse(FINALISED));
        when(aspspConsentDataClient.updateAspspConsentData(anyString(), any())).thenReturn(ResponseEntity.ok(null));
        ResponseEntity<PIISConsentCreateResponse> result = controller.grantPiisConsent(COOKIE, getPiisRequest());
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPiisResp()));
    }

    @Test
    public void getListOfAccounts() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(accountRestClient.getListOfAccounts()).thenReturn(ResponseEntity.ok(getAccounts()));

        ResponseEntity<List<AccountDetailsTO>> result = controller.getListOfAccounts(COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getAccounts()));
    }

    @Test
    public void aisDone() {
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(redirectConsentService.identifyConsent(anyString(), anyString(), anyBoolean(), anyString(), any())).thenReturn(getConsentWorkflow(FINALISED));
        when(responseUtils.redirect(anyString(), any())).thenReturn(ResponseEntity.ok(getConsentAuthorizeResponse(true, true, false, FINALISED)));

        ResponseEntity<ConsentAuthorizeResponse> result = controller.aisDone(ENCRYPTED_ID, AUTH_ID, COOKIE, false, true, false);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getConsentAuthorizeResponse(true, true, false, FINALISED)));
    }

    @Test
    public void revokeConsent() {
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(redirectConsentService.identifyConsent(anyString(), anyString(), anyBoolean(), anyString(), any())).thenReturn(getConsentWorkflow(FINALISED));
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(cmsPsuAisClient.updateAuthorisationStatus(anyString(), anyString(), anyString(), anyString(), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), anyString(), any())).thenReturn(ResponseEntity.ok(null));

        ResponseEntity<ConsentAuthorizeResponse> result = controller.revokeConsent(ENCRYPTED_ID, AUTH_ID, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getConsentAuthorizeResponse(false, false, true, ScaStatusTO.EXEMPTED)));
    }

    private List<AccountDetailsTO> getAccounts() {
        return Collections.singletonList(new AccountDetailsTO(ASPSP_ACC_ID, IBAN, null, null, null, null, EUR, LOGIN, null, AccountTypeTO.CASH, AccountStatusTO.ENABLED, null, null, UsageTypeTO.PRIV, null, Collections.emptyList()));
    }

    private PIISConsentCreateResponse getPiisResp() {
        PIISConsentCreateResponse resp = new PIISConsentCreateResponse();
        resp.setConsent(getAisConsentTO(false));
        return resp;
    }

    private CreatePiisConsentRequestTO getPiisRequest() {
        CreatePiisConsentRequestTO request = new CreatePiisConsentRequestTO();
        request.setAccount(new AccountReference(ASPSP_ACC_ID, RESOURCE_ID, IBAN, null, null, null, null, EUR));
        request.setTppAuthorisationNumber(TPP_AUTH_ID);
        request.setValidUntil(DATE);
        return request;
    }

    private SCAConsentResponseTO getScaConsentResponse(ScaStatusTO status) {
        SCAConsentResponseTO to = new SCAConsentResponseTO();
        to.setBearerToken(getBearerToken());
        to.setConsentId(CONSENT_ID);
        to.setScaStatus(status);
        to.setAuthorisationId(AUTH_ID);
        return to;
    }

    private BearerTokenTO getBearerToken() {
        return new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO());
    }

    private AisConsentTO getAisConsentTO(boolean isEmpty) {
        return isEmpty
                   ? new AisConsentTO(null, null, null, 0, new AisAccountAccessInfoTO(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null, null), null, false)
                   : new AisConsentTO(CONSENT_ID, LOGIN, "TPP_ID", 5, null, DATE, false);
    }

    private ConsentAuthorizeResponse getConsentAuthorizeResponse(boolean hasAuth, boolean hasEncrConsId, boolean isEmptyConsent, ScaStatusTO scaStatus) {
        ConsentAuthorizeResponse cons = new ConsentAuthorizeResponse();
        cons.setScaStatus(scaStatus);
        cons.setAuthorisationId(hasAuth ? AUTH_ID : null);
        cons.setAccounts(new ArrayList<>());
        cons.setConsent(getAisConsentTO(isEmptyConsent));
        cons.setEncryptedConsentId(hasEncrConsId ? ENCRYPTED_ID : null);
        return cons;
    }

    private ResponseEntity<SCALoginResponseTO> getScaLoginResponse() {
        SCALoginResponseTO to = new SCALoginResponseTO();
        to.setAuthorisationId(AUTH_ID);
        to.setScaStatus(PSUIDENTIFIED);

        to.setBearerToken(new BearerTokenTO(null, null, 999, null, getAccessTokenTO()));
        return ResponseEntity.ok(to);
    }

    private ResponseEntity<AuthorizeResponse> getAuthResponse() {
        AuthorizeResponse resp = new AuthorizeResponse();
        resp.setAuthorisationId(AUTH_ID);
        resp.setEncryptedConsentId(ENCRYPTED_ID);
        resp.setScaStatus(PSUIDENTIFIED);
        resp.setScaMethods(Collections.emptyList());
        return ResponseEntity.ok(resp);
    }

    private ConsentWorkflow getConsentWorkflow(ScaStatusTO status) {
        ConsentWorkflow workflow = new ConsentWorkflow(getCmsAisConsentResponse(), getConsentReference());
        workflow.setAuthResponse(getConsentAuthorizeResponse(true, true, false, status));
        workflow.setScaResponse(getScaConsentResponse(status));
        return workflow;
    }

    private ConsentReference getConsentReference() {
        ConsentReference ref = new ConsentReference();
        ref.setAuthorizationId(AUTH_ID);
        ref.setConsentType(ConsentType.AIS);
        ref.setCookieString(COOKIE);
        ref.setEncryptedConsentId(ENCRYPTED_ID);
        ref.setRedirectId(AUTH_ID);
        return ref;
    }

    private CmsAisConsentResponse getCmsAisConsentResponse() {
        return new CmsAisConsentResponse(getCmsAisAccountConsent(), AUTH_ID, OK_URI, NOK_URI);
    }

    private CmsAisAccountConsent getCmsAisAccountConsent() {
        return new CmsAisAccountConsent("123", new AisAccountAccess(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null, null, null, null),
            false, DATE, 5, null, ConsentStatus.RECEIVED, false, true, AisConsentRequestType.BANK_OFFERED, null,
            null, null, false, null, null, null, null);
    }

    private AccessTokenTO getAccessTokenTO() {
        AccessTokenTO tokenTO = new AccessTokenTO();
        tokenTO.setLogin(LOGIN);
        tokenTO.setConsent(getAisConsentTO(false));
        return tokenTO;
    }
}

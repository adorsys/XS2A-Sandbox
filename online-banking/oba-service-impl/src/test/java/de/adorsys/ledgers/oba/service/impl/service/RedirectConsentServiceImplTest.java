package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ChallengeDataTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.*;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.oba.service.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.ConsentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.service.impl.mapper.ObaAisConsentMapper;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.xs2a.core.authorisation.AuthorisationTemplate;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RedirectConsentServiceImplTest {
    private static final String SCA_METHOD_ID = "scaMethodId";
    private static final String AUTHORIZATION_ID = "authorizationID";
    private static final String ENCRYPTED_CONSENT_ID = "encryptedConsentId";
    private static final String REDIRECT_ID = "redirectID";
    private static final String CONSENT_ID = "234234kjlkjklj2lk34j";
    private static final String IBAN_DE = "DE1234567890";
    private static final String IBAN_FR = "FR1234567890";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final String USER_LOGIN = "login";
    private static final String USER_ID = "userId";

    @InjectMocks
    private RedirectConsentServiceImpl redirectConsentService;
    @Mock
    private CmsPsuAisClient cmsPsuAisClient;
    @Mock
    private ConsentRestClient consentRestClient;
    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private ObaAisConsentMapper consentMapper;
    @Mock
    private ConsentReferencePolicy referencePolicy;
    @Mock
    private TokenStorageService tokenStorageService;
    @Mock
    private AspspConsentDataClient aspspConsentDataClient;

    @Test
    public void selectScaMethod() {
        //given
        when(consentRestClient.selectMethod(any(), any(), any())).thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));

        //when
        redirectConsentService.selectScaMethod(SCA_METHOD_ID, getConsentWorkflow(AisConsentRequestType.GLOBAL, IBAN_DE));

        //then
        verify(consentRestClient, times(1)).selectMethod(CONSENT_ID, AUTHORIZATION_ID, SCA_METHOD_ID);
    }

    @Test
    public void updateAccessByConsentType_globalConsent() {
        assertThatCode(() -> redirectConsentService.updateAccessByConsentType(getConsentWorkflow(AisConsentRequestType.GLOBAL, IBAN_DE), Collections.singletonList(getAccountDetails()))).doesNotThrowAnyException();
    }

    @Test
    public void updateAccessByConsentType_allAvailableAccountsConsent() {
        assertThatCode(() -> redirectConsentService.updateAccessByConsentType(getConsentWorkflow(AisConsentRequestType.ALL_AVAILABLE_ACCOUNTS, IBAN_DE), Collections.singletonList(getAccountDetails()))).doesNotThrowAnyException();
    }

    @Test
    public void updateAccessByConsentType_dedicatedAccountsConsent() {
        assertThatCode(() -> redirectConsentService.updateAccessByConsentType(getConsentWorkflow(AisConsentRequestType.DEDICATED_ACCOUNTS, IBAN_DE), Collections.singletonList(getAccountDetails()))).doesNotThrowAnyException();
    }

    @Test(expected = AuthorizationException.class)
    public void updateAccessByConsentType_loginFailed() {
        //when
        redirectConsentService.updateAccessByConsentType(getConsentWorkflow(AisConsentRequestType.DEDICATED_ACCOUNTS, IBAN_FR), Collections.singletonList(getAccountDetails()));
    }

    @Test
    public void updateScaStatusConsentStatusConsentData() throws IOException {
        //given
        when(cmsPsuAisClient.updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok().build());
        when(tokenStorageService.toBase64String(any())).thenReturn(CONSENT_ID);
        when(aspspConsentDataClient.updateAspspConsentData(any(), any())).thenReturn(ResponseEntity.ok().build());

        //when
        redirectConsentService.updateScaStatusConsentStatusConsentData(USER_ID, getConsentWorkflow(AisConsentRequestType.DEDICATED_ACCOUNTS, IBAN_DE));

        //then
        verify(aspspConsentDataClient, times(1)).updateAspspConsentData(ENCRYPTED_CONSENT_ID, new CmsAspspConsentDataBase64(CONSENT_ID, CONSENT_ID));
    }

    @Test(expected = AuthorizationException.class)
    public void updateScaStatusConsentStatusConsentData_failure() throws IOException {
        //given
        when(cmsPsuAisClient.updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok().build());
        when(tokenStorageService.toBase64String(any())).thenThrow(IOException.class);

        //when
        redirectConsentService.updateScaStatusConsentStatusConsentData(USER_ID, getConsentWorkflow(AisConsentRequestType.DEDICATED_ACCOUNTS, IBAN_DE));
    }

    @Test
    public void startConsent() {
        //given
        when(consentMapper.accountAccess(any(), any())).thenReturn(getAisAccountAccess(IBAN_DE));
        when(cmsPsuAisClient.putAccountAccessInConsent(any(), any(), any())).thenReturn(ResponseEntity.ok().build());
        when(consentMapper.toTo(any())).thenReturn(getAisConsentTO());
        when(consentRestClient.startSCA(any(), any())).thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));

        //when
        redirectConsentService.startConsent(getConsentWorkflow(AisConsentRequestType.DEDICATED_ACCOUNTS, IBAN_DE), getAisConsentTO(), Collections.singletonList(getAccountDetails()));

        //then
        verify(consentMapper, times(1)).toTo(getCmsAisAccountConsent(AisConsentRequestType.DEDICATED_ACCOUNTS, IBAN_DE));

    }

    @Test
    public void identifyConsent() {
        //given
        when(referencePolicy.fromRequest(any(), any(), any(), anyBoolean())).thenReturn(getConsentReference());
        when(cmsPsuAisClient.getConsentIdByRedirectId(any(), any())).thenReturn(ResponseEntity.ok(getCmsAisConsentResponse(AisConsentRequestType.DEDICATED_ACCOUNTS, IBAN_DE)));
        when(consentMapper.toTo(any())).thenReturn(getAisConsentTO());

        //when
        ConsentWorkflow workflow = redirectConsentService.identifyConsent(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID, false, "consentCookieString", getBearerTokenTO());

        //then
        assertThat(workflow).isNotNull();
        assertEquals(workflow.getConsentReference(), getConsentReference());
        assertNotNull(workflow.getScaResponse().getBearerToken());
    }

    @Test
    public void identifyConsent_bearerTokenNull() {
        //given
        when(referencePolicy.fromRequest(any(), any(), any(), anyBoolean())).thenReturn(getConsentReference());
        when(cmsPsuAisClient.getConsentIdByRedirectId(any(), any())).thenReturn(ResponseEntity.ok(getCmsAisConsentResponse(AisConsentRequestType.DEDICATED_ACCOUNTS, IBAN_DE)));
        when(consentMapper.toTo(any())).thenReturn(getAisConsentTO());

        //when
        ConsentWorkflow workflow = redirectConsentService.identifyConsent(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID, false, "consentCookieString", null);

        //then
        assertThat(workflow).isNotNull();
        assertEquals(workflow.getConsentReference(), getConsentReference());
    }

    private static AccountDetailsTO getAccountDetails() {
        AccountDetailsTO details = new AccountDetailsTO();
        details.setId(USER_ID);
        details.setIban(IBAN_DE);
        details.setCurrency(Currency.getInstance("EUR"));
        details.setName(USER_LOGIN);
        details.setAccountType(AccountTypeTO.CASH);
        details.setAccountStatus(AccountStatusTO.ENABLED);
        details.setUsageType(UsageTypeTO.PRIV);
        return details;
    }

    private ConsentWorkflow getConsentWorkflow(AisConsentRequestType type, String iban) {
        ConsentWorkflow workflow = new ConsentWorkflow(getCmsAisConsentResponse(type, iban), getConsentReference());
        workflow.setScaResponse(getSCAResponseTO());
        workflow.setAuthResponse(getConsentAuthorizeResponse());
        return workflow;
    }

    private ConsentAuthorizeResponse getConsentAuthorizeResponse() {
        ConsentAuthorizeResponse response = new ConsentAuthorizeResponse();
        response.setAccounts(Collections.singletonList(getAccountDetails()));
        response.setAuthMessageTemplate("authMessageTemplate");
        response.setConsent(getAisConsentTO());
        response.setScaStatus(ScaStatusTO.FINALISED);
        return response;
    }

    private AisConsentTO getAisConsentTO() {
        return new AisConsentTO("id", "userId", "tppId", 6, new AisAccountAccessInfoTO(), LocalDate.now(), false);
    }

    private SCAResponseTO getSCAResponseTO() {
        return new SCAResponseTO(ScaStatusTO.FINALISED, AUTHORIZATION_ID, Collections.EMPTY_LIST, new ScaUserDataTO(), new ChallengeDataTO(), "psuMessage", LocalDateTime.now(), 15, false, "authConfirmationCode", getBearerTokenTO(), "objectType") {
        };
    }

    private BearerTokenTO getBearerTokenTO() {
        return new BearerTokenTO("access_token", "Bearer", 60, "refresh_token", new AccessTokenTO());
    }

    private CmsAisConsentResponse getCmsAisConsentResponse(AisConsentRequestType type, String iban) {
        return new CmsAisConsentResponse(getCmsAisAccountConsent(type, iban), AUTHORIZATION_ID, "tppOkRedirectUri", "tppNokRedirectUri");
    }

    private CmsAisAccountConsent getCmsAisAccountConsent(AisConsentRequestType type, String iban) {
        return new CmsAisAccountConsent(CONSENT_ID, getAisAccountAccess(iban), false, LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1), 3, LocalDate.now(), ConsentStatus.VALID, false, false,
                                        type, Collections.EMPTY_LIST, new TppInfo(), new AuthorisationTemplate(), false, Collections.EMPTY_LIST,
                                        Collections.EMPTY_MAP, OffsetDateTime.MIN, OffsetDateTime.MIN);
    }

    private AisAccountAccess getAisAccountAccess(String iban) {
        return new AisAccountAccess(Collections.singletonList(getReference(iban)), Collections.EMPTY_LIST, Collections.EMPTY_LIST, "availableAccounts", "allPsd2", "availableAccountsWithBalance", null);
    }

    private AccountReference getReference(String iban) {
        AccountReference reference = new AccountReference();
        reference.setIban(iban);
        reference.setCurrency(EUR);
        return reference;
    }

    ConsentReference getConsentReference() {
        ConsentReference reference = new ConsentReference();
        reference.setAuthorizationId(AUTHORIZATION_ID);
        reference.setConsentType(ConsentType.AIS);
        reference.setEncryptedConsentId(ENCRYPTED_CONSENT_ID);
        reference.setRedirectId(REDIRECT_ID);
        return reference;
    }

    private SCAConsentResponseTO getSCAConsentResponseTO() {
        SCAConsentResponseTO response = new SCAConsentResponseTO();
        response.setConsentId(CONSENT_ID);
        response.setAuthorisationId(AUTHORIZATION_ID);
        response.setScaStatus(ScaStatusTO.FINALISED);
        response.setScaMethods(Collections.EMPTY_LIST);
        response.setAuthConfirmationCode("code");
        response.setPsuMessage("psuMessage");
        return response;
    }
}

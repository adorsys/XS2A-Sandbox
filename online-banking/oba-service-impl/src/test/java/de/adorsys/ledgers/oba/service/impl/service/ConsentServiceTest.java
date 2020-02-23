package de.adorsys.ledgers.oba.service.impl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.oba.service.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.service.api.domain.ObaAisConsent;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.impl.mapper.CreatePiisConsentRequestMapper;
import de.adorsys.psd2.consent.api.AspspDataService;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.service.security.SecurityDataService;
import de.adorsys.psd2.xs2a.core.authorisation.AuthorisationTemplate;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import feign.FeignException;
import org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient;
import org.adorsys.ledgers.consent.aspsp.rest.client.CreatePiisConsentRequest;
import org.adorsys.ledgers.consent.aspsp.rest.client.CreatePiisConsentResponse;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsentServiceTest {
    private static final String AUTHORIZATION_ID = "authorizationID";
    private static final String TAN = "123456";
    private static final String CONSENT_ID = "234234kjlkjklj2lk34j";
    private static final String IBAN = "DE1234567890";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final String USER_LOGIN = "login";

    @InjectMocks
    private ConsentServiceImpl consentService;
    @Mock
    private CmsPsuAisClient cmsPsuAisClient;
    @Mock
    private SecurityDataService securityDataService;
    @Mock
    private AspspConsentDataClient consentDataClient;
    @Mock
    private ConsentRestClient consentRestClient;
    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AspspDataService aspspDataService;
    @Mock
    private CmsAspspPiisClient cmsAspspPiisClient;
    @Mock
    private CreatePiisConsentRequestMapper createPiisConsentRequestMapper;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getListOfConsents() {
        //given
        when(cmsPsuAisClient.getConsentsForPsu(any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(Collections.singletonList(getCmsAisAccountConsent())));
        when(securityDataService.encryptId(any())).thenReturn(Optional.of("consent"));

        //when
        List<ObaAisConsent> listOfConsents = consentService.getListOfConsents(USER_LOGIN);

        //then
        assertThat(listOfConsents).isNotNull();
        assertEquals("consent", listOfConsents.get(0).getEncryptedConsent());
        assertThat(listOfConsents.get(0).getAisAccountConsent()).isEqualTo(getCmsAisAccountConsent());
    }

    @Test(expected = ObaException.class)
    public void getListOfConsents_failedGetConsent() {
        //given
        when(cmsPsuAisClient.getConsentsForPsu(any(), any(), any(), any(), any())).thenThrow(FeignException.class);

        //when
        consentService.getListOfConsents(USER_LOGIN);
    }

    @Test
    public void revokeConsentSuccess() {
        //given
        when(cmsPsuAisClient.revokeConsent(CONSENT_ID, null, null, null, null, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(ResponseEntity.ok(Boolean.TRUE));

        //then
        assertTrue(consentService.revokeConsent(CONSENT_ID));
    }

    @Test
    public void confirmAisConsentDecoupled() throws IOException {
        //given
        when(securityDataService.decryptId(any())).thenReturn(Optional.of(CONSENT_ID));
        when(aspspDataService.readAspspConsentData(any())).thenReturn(Optional.of(getAspspConsentData()));
        when(objectMapper.readTree(getByteArray())).thenReturn(getJsonNode());
        when(consentRestClient.authorizeConsent(any(), any(), any())).thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));
        when(cmsPsuAisClient.confirmConsent(any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(true));
        when(cmsPsuAisClient.updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok().build());
        when(consentDataClient.updateAspspConsentData(any(), any())).thenReturn(ResponseEntity.ok().build());
        when(objectMapper.writeValueAsBytes(any())).thenReturn(getByteArray());

        //when
        consentService.confirmAisConsentDecoupled(USER_LOGIN, "encryptedConsentId", AUTHORIZATION_ID, TAN);

        //then
        verify(objectMapper, times(1)).readTree(getByteArray());
    }

    @Test(expected = ObaException.class)
    public void confirmAisConsentDecoupled_failedUpdateAspspData() throws IOException {
        //given
        when(securityDataService.decryptId(any())).thenReturn(Optional.of(CONSENT_ID));
        when(aspspDataService.readAspspConsentData(any())).thenReturn(Optional.of(getAspspConsentData()));
        when(objectMapper.readTree(getByteArray())).thenReturn(getJsonNode());
        when(consentRestClient.authorizeConsent(any(), any(), any())).thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));
        when(cmsPsuAisClient.confirmConsent(any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(true));
        when(cmsPsuAisClient.updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok().build());
        when(consentDataClient.updateAspspConsentData(any(), any())).thenThrow(FeignException.class);
        when(objectMapper.writeValueAsBytes(any())).thenReturn(getByteArray());

        //when
        consentService.confirmAisConsentDecoupled(USER_LOGIN, "encryptedConsentId", AUTHORIZATION_ID, TAN);
    }

    @Test(expected = ObaException.class)
    public void confirmAisConsentDecoupled_failedEncode() throws IOException {
        //given
        when(securityDataService.decryptId(any())).thenReturn(Optional.of(CONSENT_ID));
        when(aspspDataService.readAspspConsentData(any())).thenReturn(Optional.of(getAspspConsentData()));
        when(objectMapper.readTree(getByteArray())).thenReturn(getJsonNode());
        when(consentRestClient.authorizeConsent(any(), any(), any())).thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));
        when(cmsPsuAisClient.confirmConsent(any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(true));
        when(cmsPsuAisClient.updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok().build());
        when(objectMapper.writeValueAsBytes(any())).thenThrow(JsonProcessingException.class);

        //when
        consentService.confirmAisConsentDecoupled(USER_LOGIN, "encryptedConsentId", AUTHORIZATION_ID, TAN);
    }

    @Test(expected = ObaException.class)
    public void confirmAisConsentDecoupled_failedUpdateAuthId() throws IOException {
        //given
        when(securityDataService.decryptId(any())).thenReturn(Optional.of(CONSENT_ID));
        when(aspspDataService.readAspspConsentData(any())).thenReturn(Optional.of(getAspspConsentData()));
        when(objectMapper.readTree(getByteArray())).thenReturn(getJsonNode());
        when(consentRestClient.authorizeConsent(any(), any(), any())).thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));
        when(cmsPsuAisClient.confirmConsent(any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(true));
        when(cmsPsuAisClient.updateAuthorisationStatus(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenThrow(FeignException.class);

        //when
        consentService.confirmAisConsentDecoupled(USER_LOGIN, "encryptedConsentId", AUTHORIZATION_ID, TAN);
    }

    @Test(expected = ObaException.class)
    public void confirmAisConsentDecoupled_failedConfirmConsent() throws IOException {
        //given
        when(securityDataService.decryptId(any())).thenReturn(Optional.of(CONSENT_ID));
        when(aspspDataService.readAspspConsentData(any())).thenReturn(Optional.of(getAspspConsentData()));
        when(objectMapper.readTree(getByteArray())).thenReturn(getJsonNode());
        when(consentRestClient.authorizeConsent(any(), any(), any())).thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));
        when(cmsPsuAisClient.confirmConsent(any(), any(), any(), any(), any(), any())).thenThrow(FeignException.class);

        //when
        consentService.confirmAisConsentDecoupled(USER_LOGIN, "encryptedConsentId", AUTHORIZATION_ID, TAN);
    }

    @Test(expected = ObaException.class)
    public void confirmAisConsentDecoupled_failedEncodeConsentId() {
        //when
        consentService.confirmAisConsentDecoupled(USER_LOGIN, "encryptedConsentId", AUTHORIZATION_ID, TAN);
    }

    @Test(expected = ObaException.class)
    public void confirmAisConsentDecoupled_couldNotRetrieveAspspData() throws IOException {
        //given
        when(securityDataService.decryptId(any())).thenReturn(Optional.of(CONSENT_ID));
        when(aspspDataService.readAspspConsentData(any())).thenReturn(Optional.of(new AspspConsentData(null, CONSENT_ID)));

        //when
        consentService.confirmAisConsentDecoupled(USER_LOGIN, "encryptedConsentId", AUTHORIZATION_ID, TAN);
    }

    @Test(expected = ObaException.class)
    public void confirmAisConsentDecoupled_couldNotParseAspspData() throws IOException {
        //given
        when(securityDataService.decryptId(any())).thenReturn(Optional.of(CONSENT_ID));
        when(aspspDataService.readAspspConsentData(any())).thenReturn(Optional.of(getAspspConsentData()));
        when(objectMapper.readTree(getByteArray())).thenThrow(IOException.class);

        //when
        consentService.confirmAisConsentDecoupled(USER_LOGIN, "encryptedConsentId", AUTHORIZATION_ID, TAN);
    }

    @Test
    public void createConsent() {
        //given
        when(createPiisConsentRequestMapper.fromCreatePiisConsentRequest(any())).thenReturn(getCreatePiisConsentRequest());
        when(cmsAspspPiisClient.createConsent(any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(getCreatePiisConsentResponse()));
        when(consentRestClient.grantPIISConsent(any())).thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));

        //when
        SCAConsentResponseTO result = consentService.createConsent(getCreatePiisConsentRequestTO(), USER_LOGIN);

        //then
        assertThat(result).isNotNull();
        assertEquals(CONSENT_ID, result.getConsentId());
        verify(createPiisConsentRequestMapper, times(1)).fromCreatePiisConsentRequest(getCreatePiisConsentRequestTO());
    }

    private CreatePiisConsentResponse getCreatePiisConsentResponse() {
        CreatePiisConsentResponse response = new CreatePiisConsentResponse();
        response.setConsentId(CONSENT_ID);
        return response;
    }

    private CreatePiisConsentRequest getCreatePiisConsentRequest() {
        CreatePiisConsentRequest request = new CreatePiisConsentRequest();
        request.setAccount(getReference());
        request.setCardExpiryDate(LocalDate.now().plusMonths(1));
        request.setCardInformation("cardInformation");
        request.setCardNumber("cardNumber");
        request.setRegistrationInformation("registrationInformation");
        request.setValidUntil(LocalDate.now().plusMonths(3));
        request.setTppAuthorisationNumber("tppAuthorisationNumber");
        return request;
    }

    private CreatePiisConsentRequestTO getCreatePiisConsentRequestTO() {
        CreatePiisConsentRequestTO request = new CreatePiisConsentRequestTO();
        request.setAccount(getReference());
        request.setCardExpiryDate(LocalDate.now().plusMonths(1));
        request.setCardInformation("cardInformation");
        request.setCardNumber("cardNumber");
        request.setRegistrationInformation("registrationInformation");
        request.setValidUntil(LocalDate.now().plusMonths(3));
        request.setTppAuthorisationNumber("tppAuthorisationNumber");
        return request;
    }

    private SCAConsentResponseTO getSCAConsentResponseTO() {
        SCAConsentResponseTO response = new SCAConsentResponseTO();
        response.setConsentId(CONSENT_ID);
        return response;
    }

    private CmsAisAccountConsent getCmsAisAccountConsent() {
        return new CmsAisAccountConsent(CONSENT_ID, getAisAccountAccess(), false, LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1), 3, LocalDate.now(), ConsentStatus.VALID, false, false,
                                        AisConsentRequestType.BANK_OFFERED, Collections.EMPTY_LIST, new TppInfo(), new AuthorisationTemplate(), false, Collections.EMPTY_LIST,
                                        Collections.EMPTY_MAP, OffsetDateTime.MIN, OffsetDateTime.MIN);
    }

    private AisAccountAccess getAisAccountAccess() {
        return new AisAccountAccess(Collections.singletonList(getReference()), Collections.EMPTY_LIST, Collections.EMPTY_LIST, "availableAccounts", "allPsd2", "availableAccountsWithBalance", null);
    }

    private AccountReference getReference() {
        AccountReference reference = new AccountReference();
        reference.setIban(IBAN);
        reference.setCurrency(EUR);
        return reference;
    }

    private AspspConsentData getAspspConsentData() throws JsonProcessingException {
        return new AspspConsentData(getByteArray(), CONSENT_ID);
    }

    private byte[] getByteArray() throws JsonProcessingException {
        String json = "{ \"bearerToken\":{ \"access_token\":\"eyJraWQiOiJBV3MtRk1o1V4M\"," +
                          " \"token_type\":\"Bearer\" }}";
        return mapper.writeValueAsBytes(json);
    }

    private JsonNode getJsonNode() throws JsonProcessingException {
        String json = "{ \"bearerToken\":{ \"access_token\":\"eyJraWQiOiJBV3MtRk1o1V4M\"," +
                          " \"token_type\":\"Bearer\" }}";
        return mapper.readTree(json);
    }
}


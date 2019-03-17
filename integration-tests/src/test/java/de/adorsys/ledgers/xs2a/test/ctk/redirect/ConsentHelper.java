package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.client.ObaAisApiClient;
import de.adorsys.ledgers.xs2a.api.client.AccountApiClient;
import de.adorsys.ledgers.xs2a.api.client.ConsentApiClient;
import de.adorsys.psd2.model.AccountAccess;
import de.adorsys.psd2.model.AccountAccess.AllPsd2Enum;
import de.adorsys.psd2.model.AccountDetails;
import de.adorsys.psd2.model.AccountList;
import de.adorsys.psd2.model.AccountReference;
import de.adorsys.psd2.model.AccountReport;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentStatusResponse200;
import de.adorsys.psd2.model.Consents;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.TransactionDetails;
import de.adorsys.psd2.model.TransactionList;
import de.adorsys.psd2.model.TransactionsResponse200Json;

public class ConsentHelper {

	private final String digest = null;
	private final String signature = null;
	private final byte[] tpPSignatureCertificate = null;
	private final String psUIDType = null;
	private final String psUCorporateID = null;
	private final String psUCorporateIDType = null;
	private final String psUIPAddress = "127.0.0.1";
	private final String psUIPPort = null;
	private final String psUAccept = null;
	private final String psUAcceptCharset = null;
	private final String psUAcceptEncoding = null;
	private final String psUAcceptLanguage = null;
	private final String psUUserAgent = null;
	private final String psUHttpMethod = null;
	private final UUID psUDeviceID = UUID.randomUUID();
	private final String psUGeoLocation = null;

	private final CookiesUtils cu = new CookiesUtils();

	private final String PSU_ID;
	private final String iban;
	private final ConsentApiClient consentApi;
	private final ObaAisApiClient obaAisApiClient;
	private final AccountApiClient accountApi;

	public ConsentHelper(String pSU_ID, String iban, ConsentApiClient consentApi, ObaAisApiClient obaAisApiClient,
			AccountApiClient accountApi) {
		super();
		PSU_ID = pSU_ID;
		this.iban = iban;
		this.consentApi = consentApi;
		this.obaAisApiClient = obaAisApiClient;
		this.accountApi = accountApi;
	}

	public ResponseEntity<ConsentsResponse201> createDedicatedConsent() {
		return createConsent(dedicatedConsent());
	}

	public ResponseEntity<ConsentsResponse201> createAllPsd2Consent() {
		return createConsent(allPSD2Consent());
	}
	
	private ResponseEntity<ConsentsResponse201> createConsent(Consents consents) {
		UUID xRequestID = UUID.randomUUID();
		String tpPRedirectPreferred = "true";
		String tpPRedirectURI = "http://localhost:8080/tpp/ok";
		String tpPNokRedirectURI =  "http://localhost:8080/tpp/nok";
		Boolean tpPExplicitAuthorisationPreferred = false;

		ResponseEntity<ConsentsResponse201> consentsResponse201 = consentApi._createConsent(xRequestID, consents,
				digest, signature, tpPSignatureCertificate, PSU_ID, psUIDType, psUCorporateID, psUCorporateIDType,
				tpPRedirectPreferred, tpPRedirectURI, tpPNokRedirectURI, tpPExplicitAuthorisationPreferred,
				psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage,
				psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation);

		Assert.assertNotNull(consentsResponse201);
		Assert.assertEquals(HttpStatus.CREATED, consentsResponse201.getStatusCode());
		ConsentsResponse201 consent201 = consentsResponse201.getBody();
		Assert.assertNotNull(getScaRedirect(consent201.getLinks()));

		Assert.assertNotNull(consent201.getConsentId());
		Assert.assertNotNull(consent201.getConsentStatus());
		Assert.assertEquals(ConsentStatus.RECEIVED, consent201.getConsentStatus());

		return consentsResponse201;
	}

	public ResponseEntity<ConsentAuthorizeResponse> login(
			ResponseEntity<ConsentsResponse201> createConsentResp) {
		ConsentsResponse201 consentsResponse201 = createConsentResp.getBody();
		String scaRedirectLink = getScaRedirect(consentsResponse201.getLinks());
		String encryptedConsentId = consentsResponse201.getConsentId();
		String redirectId = QuerryParser.param(scaRedirectLink, "redirectId");
		String encryptedConsentIdFromOnlineBanking = QuerryParser.param(scaRedirectLink, "encryptedConsentId");

		Assert.assertEquals(encryptedConsentId, encryptedConsentIdFromOnlineBanking);

		ResponseEntity<AuthorizeResponse> aisAuth = obaAisApiClient.aisAuth(redirectId, encryptedConsentId);
		URI location = aisAuth.getHeaders().getLocation();
		String authorisationId = QuerryParser.param(location.toString(), "authorisationId");
		List<String> cookieStrings = aisAuth.getHeaders().get("Set-Cookie");
		String consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		ResponseEntity<ConsentAuthorizeResponse> loginResponse = obaAisApiClient.login(encryptedConsentId,
				authorisationId, PSU_ID, "12345", cu.resetCookies(cookieStrings));

		Assert.assertNotNull(loginResponse);
		Assert.assertTrue(loginResponse.getStatusCode().is2xxSuccessful());
		cookieStrings = loginResponse.getHeaders().get("Set-Cookie");
		consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		return loginResponse;
	}

	public ResponseEntity<ConsentStatusResponse200> loadConsentStatus(String encryptedConsentId) {
		UUID xRequestID = UUID.randomUUID();
		ResponseEntity<ConsentStatusResponse200> consentStatus = consentApi._getConsentStatus(encryptedConsentId,
				xRequestID, digest, signature, tpPSignatureCertificate, psUIPAddress, psUIPPort, psUAccept,
				psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID,
				psUGeoLocation);

		Assert.assertNotNull(consentStatus);
		Assert.assertEquals(HttpStatus.OK, consentStatus.getStatusCode());
		return consentStatus;
	}

	String getScaRedirect(@NotNull @Valid Map map) {
		return (String) map.get("scaRedirect");
	}

	public ResponseEntity<ConsentAuthorizeResponse> authCode(ResponseEntity<ConsentAuthorizeResponse> authResponseWrapper) {
		Assert.assertNotNull(authResponseWrapper);
		Assert.assertTrue(authResponseWrapper.getStatusCode().is2xxSuccessful());
		List<String> cookieStrings = authResponseWrapper.getHeaders().get("Set-Cookie");
		String consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		ConsentAuthorizeResponse authResponse = authResponseWrapper.getBody();
		
		ResponseEntity<ConsentAuthorizeResponse> authrizedConsentResponseWrapper = obaAisApiClient.authrizedConsent(authResponse.getEncryptedConsentId(), authResponse.getAuthorisationId(), 
				cu.resetCookies(cookieStrings), "123456");

		Assert.assertNotNull(authrizedConsentResponseWrapper);
		Assert.assertTrue(authrizedConsentResponseWrapper.getStatusCode().is2xxSuccessful());
		cookieStrings = authrizedConsentResponseWrapper.getHeaders().get("Set-Cookie");
		consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		return authrizedConsentResponseWrapper;
	}

	public ResponseEntity<ConsentAuthorizeResponse> choseScaMethod(
			ResponseEntity<ConsentAuthorizeResponse> authResponseWrapper) {
		Assert.assertNotNull(authResponseWrapper);
		Assert.assertTrue(authResponseWrapper.getStatusCode().is2xxSuccessful());
		List<String> cookieStrings = authResponseWrapper.getHeaders().get("Set-Cookie");
		String consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		ConsentAuthorizeResponse consentAuthorizeResponse = authResponseWrapper.getBody();
		ScaUserDataTO scaUserDataTO = consentAuthorizeResponse.getScaMethods().iterator().next();
		ResponseEntity<ConsentAuthorizeResponse> selectMethodResponseWrapper = obaAisApiClient.selectMethod(consentAuthorizeResponse.getEncryptedConsentId(), 
				consentAuthorizeResponse.getAuthorisationId(), scaUserDataTO.getId(), cu.resetCookies(cookieStrings));
		
		Assert.assertNotNull(selectMethodResponseWrapper);
		Assert.assertTrue(selectMethodResponseWrapper.getStatusCode().is2xxSuccessful());
		cookieStrings = selectMethodResponseWrapper.getHeaders().get("Set-Cookie");
		consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		return selectMethodResponseWrapper;
	}

	
	public void checkConsentStatus(String encryptedConsentId, ConsentStatus expectedStatus) {
		ResponseEntity<ConsentStatusResponse200> loadedConsentStatusWrapper = loadConsentStatus(encryptedConsentId);
		ConsentStatusResponse200 loadedConsentStatus = loadedConsentStatusWrapper.getBody();
		Assert.assertNotNull(loadedConsentStatus);
		ConsentStatus currentStatus = loadedConsentStatus.getConsentStatus();
		Assert.assertNotNull(currentStatus);
		Assert.assertEquals(expectedStatus, currentStatus);
	}

	public void validateResponseStatus(ResponseEntity<ConsentAuthorizeResponse> authResponseWrapper, ScaStatusTO expectedScaStatus) {
		Assert.assertNotNull(authResponseWrapper);
		Assert.assertEquals(HttpStatus.OK, authResponseWrapper.getStatusCode());
		ConsentAuthorizeResponse authResponse = authResponseWrapper.getBody();
		ScaStatusTO scaStatus = authResponse.getScaStatus();
		Assert.assertNotNull(scaStatus);
		Assert.assertEquals(expectedScaStatus, scaStatus);
	}
	
	public void checkConsentStatus(ResponseEntity<ConsentsResponse201> createConsentResp, ConsentStatus status) {
		ConsentsResponse201 consents = createConsentResp.getBody();
		// Login User
		Assert.assertNotNull(consents);
		ConsentStatus consentStatus = consents.getConsentStatus();
		Assert.assertNotNull(consentStatus);
		Assert.assertEquals(ConsentStatus.RECEIVED, consentStatus);
	}
	
	private Consents dedicatedConsent() {
		Consents consents = new Consents();
		AccountAccess access = new AccountAccess();
		AccountReference accountRef = new AccountReference();
		accountRef.setIban(iban);
		accountRef.setCurrency("EUR");
		List<AccountReference> accounts = Arrays.asList(accountRef);
		access.setAccounts(accounts);
		access.setBalances(accounts);
		access.setTransactions(accounts);
		consents.setAccess(access);
		consents.setFrequencyPerDay(4);
		consents.setRecurringIndicator(true);
		consents.setValidUntil(LocalDate.of(2021, 11, 30));
		return consents;
	}

	private Consents allPSD2Consent() {
		Consents consents = new Consents()
				.access(new AccountAccess()
				.allPsd2(AllPsd2Enum.ALLACCOUNTS))
				.frequencyPerDay(4)
				.recurringIndicator(true)
				.validUntil(LocalDate.of(2021, 11, 30));
		return consents;
	}
	
	public Map<String, Map<String, List<TransactionDetails>>> loadTransactions(ConsentAuthorizeResponse consentAuthorizeResponse, Boolean withBalance) {
		String encryptedConsentId = consentAuthorizeResponse.getEncryptedConsentId();
		AccountList accountList = lisftOfAccounts(withBalance, encryptedConsentId);
		List<AccountDetails> accounts = accountList.getAccounts();
		Map<String, Map<String, List<TransactionDetails>>> result = new HashMap<>();
		accounts.stream().forEach(a -> {
			Map<String, List<TransactionDetails>> loadTransactions = loadTransactions(a, encryptedConsentId, withBalance);
			result.put(a.getResourceId(), loadTransactions);
		});
		return result;
	}

	private AccountList lisftOfAccounts(Boolean withBalance, String encryptedConsentId) {
		UUID xRequestID = UUID.randomUUID();
		AccountList accountList = accountApi
				._getAccountList(xRequestID, encryptedConsentId, withBalance, digest, signature,
						tpPSignatureCertificate, psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset,
						psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation)
				.getBody();
		return accountList;
	}

	private Map<String, List<TransactionDetails>> loadTransactions(AccountDetails a, String encryptedConsentId, Boolean withBalance) {
		UUID xRequestID = UUID.randomUUID();

		LocalDate dateFrom = LocalDate.of(2017, 01, 01);
		LocalDate dateTo = LocalDate.of(2020, 01, 01);
		// WARNING case sensitive
		String bookingStatus = "both";
		String entryReferenceFrom = null;
		Boolean deltaList = false;
		TransactionsResponse200Json transactionsResponse200Json = accountApi
				._getTransactionList(a.getResourceId(), bookingStatus, xRequestID, encryptedConsentId,
						dateFrom, dateTo, entryReferenceFrom, deltaList, withBalance, digest, signature,
						tpPSignatureCertificate, psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset,
						psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod, xRequestID, psUGeoLocation)
				.getBody();
		AccountReport transactions = transactionsResponse200Json.getTransactions();
		TransactionList booked = transactions.getBooked();
		Map<String, List<TransactionDetails>> result = new HashMap<>();
		result.put("BOOKED", booked);
		TransactionList pending = transactions.getPending();
		result.put("PENDING", pending);
		return result;
	}
	
	
}

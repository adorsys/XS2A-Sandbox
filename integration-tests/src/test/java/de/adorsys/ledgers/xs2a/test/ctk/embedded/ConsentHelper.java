package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
import de.adorsys.psd2.model.PsuData;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethod;
import de.adorsys.psd2.model.TransactionDetails;
import de.adorsys.psd2.model.TransactionList;
import de.adorsys.psd2.model.TransactionsResponse200Json;
import de.adorsys.psd2.model.UpdatePsuAuthentication;
import de.adorsys.psd2.model.UpdatePsuAuthenticationResponse;

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

	private final String PSU_ID;
	private final String iban;
	private final ConsentApiClient consentApi;
	private final AccountApiClient accountApi;

	public ConsentHelper(AccountApiClient accountApi, ConsentApiClient consentApi, String PSU_ID, String iban) {
		this.accountApi = accountApi;
		this.consentApi = consentApi;
		this.PSU_ID = PSU_ID;
		this.iban = iban;
	}

	public ResponseEntity<ConsentsResponse201> createDedicatedConsent() {
		return createConsent(dedicatedConsent());
	}

	public ResponseEntity<ConsentsResponse201> createAllPSD2Consent() {
		return createConsent(allPSD2Consent());
	}
	
	private ResponseEntity<ConsentsResponse201> createConsent(Consents consents) {
		UUID xRequestID = UUID.randomUUID();
		String tpPRedirectPreferred = "false";
		String tpPRedirectURI = null;
		String tpPNokRedirectURI = null;
		Boolean tpPExplicitAuthorisationPreferred = false;
		ResponseEntity<ConsentsResponse201> consentsResponse201 = consentApi._createConsent(xRequestID, consents,
				digest, signature, tpPSignatureCertificate, PSU_ID, psUIDType, psUCorporateID, psUCorporateIDType,
				tpPRedirectPreferred, tpPRedirectURI, tpPNokRedirectURI, tpPExplicitAuthorisationPreferred,
				psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage,
				psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation);

		Assert.assertNotNull(consentsResponse201);
		Assert.assertEquals(HttpStatus.CREATED, consentsResponse201.getStatusCode());
		ConsentsResponse201 consent201 = consentsResponse201.getBody();
		Assert.assertNotNull(getStartAuthorisationWithPsuAuthentication(consent201));

		Assert.assertNotNull(consent201.getConsentId());
		Assert.assertNotNull(consent201.getConsentStatus());
		Assert.assertEquals(ConsentStatus.RECEIVED, consent201.getConsentStatus());

		return consentsResponse201;
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
//		access.setAllPsd2(AllPsd2Enum.ALLACCOUNTS);
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
	
	public ResponseEntity<UpdatePsuAuthenticationResponse> login(
			ResponseEntity<ConsentsResponse201> createConsentResp) {
		ConsentsResponse201 consentsResponse201 = createConsentResp.getBody();
		String startAuthorisationWithPsuAuthentication = getStartAuthorisationWithPsuAuthentication(
				consentsResponse201);
		String consentId = consentsResponse201.getConsentId();
		String authorisationId = StringUtils
				.substringBefore(StringUtils.substringAfterLast(startAuthorisationWithPsuAuthentication, "/"), "?");
		UpdatePsuAuthentication updatePsuAuthentication = new UpdatePsuAuthentication();
		updatePsuAuthentication.setPsuData(new PsuData().password("12345"));
		UUID xRequestID = UUID.randomUUID();

		ResponseEntity<UpdatePsuAuthenticationResponse> updateConsentsPsuDataResponse = consentApi
				._updateConsentsPsuData(xRequestID, consentId, authorisationId, updatePsuAuthentication, digest,
						signature, tpPSignatureCertificate, PSU_ID, psUIDType, psUCorporateID, psUCorporateIDType,
						psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage,
						psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation);

		return updateConsentsPsuDataResponse;
	}

	public ResponseEntity<ConsentStatusResponse200> loadConsentStatus(String authorisationUrl) {
		AuthUrl authUrl = AuthUrl.parse(authorisationUrl);
		String encryptedConsentId = authUrl.getEncryptedConsentId();

		UUID xRequestID = UUID.randomUUID();
		ResponseEntity<ConsentStatusResponse200> consentStatus = consentApi._getConsentStatus(encryptedConsentId,
				xRequestID, digest, signature, tpPSignatureCertificate, psUIPAddress, psUIPPort, psUAccept,
				psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID,
				psUGeoLocation);

		Assert.assertNotNull(consentStatus);
		Assert.assertEquals(HttpStatus.OK, consentStatus.getStatusCode());
		return consentStatus;
	}

	private String getStartAuthorisationWithPsuAuthentication(ConsentsResponse201 consent201) {
		return (String) consent201.getLinks().get("startAuthorisationWithPsuAuthentication");
	}

	public ResponseEntity<UpdatePsuAuthenticationResponse> authCode(UpdatePsuAuthenticationResponse authResponse) {
		AuthUrl authUrl = AuthUrl.parse((String) authResponse.getLinks().get("authoriseTransaction"));
		String authorisationId = authUrl.getAuthorizationId();
		String encryptedConsentId = authUrl.getEncryptedConsentId();

		UUID xRequestID = UUID.randomUUID();
		Map<String, String> scaAuthenticationData = new HashMap<>();
		scaAuthenticationData.put("scaAuthenticationData", "123456");

		ResponseEntity<UpdatePsuAuthenticationResponse> authCodeResponse = consentApi._updateConsentsPsuData(xRequestID,
				encryptedConsentId, authorisationId, scaAuthenticationData, digest, signature, tpPSignatureCertificate,
				PSU_ID, psUIDType, psUCorporateID, psUCorporateIDType, psUIPAddress, psUIPPort, psUAccept,
				psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID,
				psUGeoLocation);

		Assert.assertNotNull(authCodeResponse);
		Assert.assertEquals(HttpStatus.OK, authCodeResponse.getStatusCode());
		return authCodeResponse;
	}

	public ResponseEntity<UpdatePsuAuthenticationResponse> choseScaMethod(
			UpdatePsuAuthenticationResponse authResponse) {
		AuthUrl authUrl = AuthUrl.parse((String) authResponse.getLinks().get("selectAuthenticationMethod"));
		String authorisationId = authUrl.getAuthorizationId();
		String encryptedConsentId = authUrl.getEncryptedConsentId();

		UUID xRequestID = UUID.randomUUID();
		SelectPsuAuthenticationMethod selectPsuAuthenticationMethod = new SelectPsuAuthenticationMethod();
		Assert.assertNotNull(authResponse.getScaMethods());
		Assert.assertFalse(authResponse.getScaMethods().isEmpty());
		selectPsuAuthenticationMethod
				.setAuthenticationMethodId(authResponse.getScaMethods().iterator().next().getAuthenticationMethodId());
		ResponseEntity<UpdatePsuAuthenticationResponse> authCodeResponse = consentApi._updateConsentsPsuData(xRequestID,
				encryptedConsentId, authorisationId, selectPsuAuthenticationMethod, digest, signature,
				tpPSignatureCertificate, PSU_ID, psUIDType, psUCorporateID, psUCorporateIDType, psUIPAddress, psUIPPort,
				psUAccept, psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod,
				psUDeviceID, psUGeoLocation);

		Assert.assertNotNull(authCodeResponse);

		return authCodeResponse;
	}

	public Map<String, Map<String, List<TransactionDetails>>> loadTransactions(UpdatePsuAuthenticationResponse authCodeResponse, Boolean withBalance) {
		UUID xRequestID = UUID.randomUUID();

		String scaStatusUrl = (String) authCodeResponse.getLinks().get("scaStatus");
		AuthUrl authUrl = AuthUrl.parse(scaStatusUrl);
		AccountList accountList = lisftOfAccounts(withBalance, authUrl);
		List<AccountDetails> accounts = accountList.getAccounts();
		Map<String, Map<String, List<TransactionDetails>>> result = new HashMap<>();
		accounts.stream().forEach(a -> {
			Map<String, List<TransactionDetails>> loadTransactions = loadTransactions(a, authUrl, withBalance);
			result.put(a.getResourceId(), loadTransactions);
		});
		return result;
	}

	private AccountList lisftOfAccounts(Boolean withBalance, AuthUrl authUrl) {
		UUID xRequestID = UUID.randomUUID();
		AccountList accountList = accountApi
				._getAccountList(xRequestID, authUrl.getEncryptedConsentId(), withBalance, digest, signature,
						tpPSignatureCertificate, psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset,
						psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation)
				.getBody();
		return accountList;
	}

	private Map<String, List<TransactionDetails>> loadTransactions(AccountDetails a, AuthUrl authUrl, Boolean withBalance) {
		UUID xRequestID = UUID.randomUUID();

		LocalDate dateFrom = LocalDate.of(2017, 01, 01);
		LocalDate dateTo = LocalDate.of(2020, 01, 01);
		// WARNING case sensitive
		String bookingStatus = "both";
		String entryReferenceFrom = null;
		Boolean deltaList = false;
		TransactionsResponse200Json transactionsResponse200Json = accountApi
				._getTransactionList(a.getResourceId(), bookingStatus, xRequestID, authUrl.getEncryptedConsentId(),
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

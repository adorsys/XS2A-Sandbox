package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.rest.api.domain.PIISConsentCreateResponse;
import de.adorsys.ledgers.oba.rest.client.ObaAisApiClient;
import de.adorsys.ledgers.oba.rest.client.ObaScaApiClient;
import de.adorsys.ledgers.xs2a.api.client.FundsConfirmationApiClient;
import de.adorsys.ledgers.xs2a.api.client.FundsConfirmationResponse;
import de.adorsys.psd2.model.AccountReference;
import de.adorsys.psd2.model.Amount;
import de.adorsys.psd2.model.ConfirmationOfFunds;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.core.tpp.TppRole;

public class PiisHelper {

	private final String digest = null;
	private final String signature = null;
	private final byte[] tpPSignatureCertificate = null;

	private final CookiesUtils cu = new CookiesUtils();

	private final String PSU_ID;
	private final String iban;
	private final FundsConfirmationApiClient fundsConfirmationApiClient;
	private final ObaAisApiClient obaAisApiClient;
	private final ObaScaApiClient obaScaApiClient;

	public PiisHelper(String pSU_ID, String iban, ObaAisApiClient obaAisApiClient, 
			FundsConfirmationApiClient fundsConfirmationApiClient,
			ObaScaApiClient obaScaApiClient) {
		PSU_ID = pSU_ID;
		this.iban = iban;
		this.obaAisApiClient = obaAisApiClient;
		this.fundsConfirmationApiClient = fundsConfirmationApiClient;
		this.obaScaApiClient = obaScaApiClient;
	}

	public ResponseEntity<PIISConsentCreateResponse> createPiisConsent(ResponseEntity<AuthorizeResponse> authResponse) {
		
		CreatePiisConsentRequestTO aisConsentTO = dedicatedConsent();

		Assert.assertNotNull(authResponse);
		Assert.assertTrue(authResponse.getStatusCode().is2xxSuccessful());
		List<String> cookieStrings = authResponse.getHeaders().get("Set-Cookie");
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);
		
		ResponseEntity<PIISConsentCreateResponse> grantPiisConsentResponseWrapper = obaAisApiClient.grantPiisConsent(cu.resetCookies(cookieStrings), aisConsentTO);
		Assert.assertNotNull(grantPiisConsentResponseWrapper);
		Assert.assertEquals(HttpStatus.OK, grantPiisConsentResponseWrapper.getStatusCode());
		
		return grantPiisConsentResponseWrapper;
	}

	public ResponseEntity<AuthorizeResponse> login() {
		return obaScaApiClient.login(PSU_ID, "12345");
	}

	public ResponseEntity<AuthorizeResponse> authCode(ResponseEntity<AuthorizeResponse> authResponseWrapper) {
		Assert.assertNotNull(authResponseWrapper);
		Assert.assertTrue(authResponseWrapper.getStatusCode().is2xxSuccessful());
		List<String> cookieStrings = authResponseWrapper.getHeaders().get("Set-Cookie");
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		AuthorizeResponse authResponse = authResponseWrapper.getBody();
		
		
		ResponseEntity<AuthorizeResponse> authCoderesponseWrapper = obaScaApiClient.validateAuthCode(
				authResponse.getEncryptedConsentId(), authResponse.getAuthorisationId(),
				"123456", cu.resetCookies(cookieStrings));

		Assert.assertNotNull(authCoderesponseWrapper);
		Assert.assertTrue(authCoderesponseWrapper.getStatusCode().is2xxSuccessful());
		cookieStrings = authCoderesponseWrapper.getHeaders().get("Set-Cookie");
		accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		return authCoderesponseWrapper;
	}

	public ResponseEntity<AuthorizeResponse> choseScaMethod(
			ResponseEntity<AuthorizeResponse> authResponseWrapper) {
		Assert.assertNotNull(authResponseWrapper);
		Assert.assertTrue(authResponseWrapper.getStatusCode().is2xxSuccessful());
		List<String> cookieStrings = authResponseWrapper.getHeaders().get("Set-Cookie");
		String consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		AuthorizeResponse consentAuthorizeResponse = authResponseWrapper.getBody();
		ScaUserDataTO scaUserDataTO = consentAuthorizeResponse.getScaMethods().iterator().next();
		ResponseEntity<AuthorizeResponse> selectMethodResponseWrapper = obaScaApiClient.selectMethod(consentAuthorizeResponse.getEncryptedConsentId(), 
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

	private CreatePiisConsentRequestTO dedicatedConsent() {
		CreatePiisConsentRequestTO consents = new CreatePiisConsentRequestTO();
		List<AccountReferenceTO> accounts = new ArrayList<>();
		consents.setAccounts(accounts);
		AccountReferenceTO ref = new AccountReferenceTO();
		accounts.add(ref);
		ref.setIban(iban);
		ref.setCurrency(Currency.getInstance("EUR"));
		consents.setAllowedFrequencyPerDay(4);
		consents.setValidUntil(LocalDate.of(2021, 11, 30));
		TppInfo tppInfo = new TppInfo();
		tppInfo.setAuthorisationNumber("12345987");// Took this from debugger
		tppInfo.setAuthorityId(RandomStringUtils.randomNumeric(4));
		
		// Ticket. What if this is null
		tppInfo.setTppRoles(Collections.singletonList(TppRole.PIISP));
		// ??
		consents.setTppInfo(tppInfo);
		return consents;
	}

	public ResponseEntity<FundsConfirmationResponse> confOfFund(ResponseEntity<AuthorizeResponse> authResponseWrapper) {
		UUID xRequestID = UUID.randomUUID();
		@Valid
		ConfirmationOfFunds cof = new ConfirmationOfFunds();
		AccountReference account = new AccountReference();
		account.setIban(iban);
		account.setCurrency("EUR");
		cof.setAccount(account);
		Amount instructedAmount = new Amount().amount("5").currency("EUR");
		cof.setInstructedAmount(instructedAmount);
		
		// hmmm
		cof.setCardNumber(iban);
		cof.setPayee("Francis");
		
		ResponseEntity<FundsConfirmationResponse> availabilityOfFunds = fundsConfirmationApiClient._checkAvailabilityOfFunds(cof, xRequestID, digest, signature, tpPSignatureCertificate);
		return availabilityOfFunds;
	}

}

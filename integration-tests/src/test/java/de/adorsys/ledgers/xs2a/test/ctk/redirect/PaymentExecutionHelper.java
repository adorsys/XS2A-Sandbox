package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.client.ObaPisApiClient;
import de.adorsys.ledgers.xs2a.api.client.PaymentApiClient;
import de.adorsys.psd2.model.PaymentInitationRequestResponse201;
import de.adorsys.psd2.model.PaymentInitiationStatusResponse200Json;
import de.adorsys.psd2.model.TransactionStatus;

public class PaymentExecutionHelper {

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

	private final PaymentApiClient paymentApi;
	private final ObaPisApiClient pisApiClient;
	private final PaymentCase paymentCase;
	private final String paymentService;
	private final String paymentProduct;
	
	private final CookiesUtils cu = new CookiesUtils();

	public PaymentExecutionHelper(PaymentApiClient paymentApi, ObaPisApiClient pisApiClient, PaymentCase paymentCase,
			String paymentService, String paymentProduct) {
		super();
		this.paymentApi = paymentApi;
		this.pisApiClient = pisApiClient;
		this.paymentCase = paymentCase;
		this.paymentService = paymentService;
		this.paymentProduct = paymentProduct;
	}

	public PaymentInitationRequestResponse201 initiatePayment() {
		Object payment = paymentCase.getPayment();
		UUID xRequestID = UUID.randomUUID();
		String PSU_ID = paymentCase.getPsuId();
		String consentID = null;
		String tpPRedirectPreferred = "true";
		String tpPRedirectURI = "http://localhost:8080/tpp/ok";
		String tpPNokRedirectURI =  "http://localhost:8080/tpp/nok";
		Boolean tpPExplicitAuthorisationPreferred = true;
		PaymentInitationRequestResponse201 initiatedPayment = paymentApi._initiatePayment(payment, xRequestID, psUIPAddress,
				paymentService, paymentProduct, digest, signature, tpPSignatureCertificate, PSU_ID, psUIDType,
				psUCorporateID, psUCorporateIDType, consentID, tpPRedirectPreferred, tpPRedirectURI, tpPNokRedirectURI,
				tpPExplicitAuthorisationPreferred, psUIPPort, psUAccept, psUAcceptCharset, psUAcceptEncoding,
				psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation).getBody();

		Assert.assertNotNull(initiatedPayment);
		Assert.assertNotNull(getScaRedirect(initiatedPayment));

		Assert.assertNotNull(initiatedPayment.getPaymentId());
		Assert.assertNotNull(initiatedPayment.getTransactionStatus());
		Assert.assertEquals("RCVD", initiatedPayment.getTransactionStatus().name());
		Assert.assertNotNull(initiatedPayment.getPaymentId());

		return initiatedPayment;
	}

	static class RedirectedParams {
		private final String redirectId;
		private final String encryptedPaymentId;
		public RedirectedParams(String scaRedirect) {
			encryptedPaymentId = StringUtils.substringBetween(scaRedirect, "paymentId=", "&redirectId=");
			redirectId = StringUtils.substringAfter(scaRedirect, "&redirectId=");
		}
		public String getRedirectId() {
			return redirectId;
		}
		public String getEncryptedPaymentId() {
			return encryptedPaymentId;
		}
	}
	public ResponseEntity<PaymentAuthorizeResponse> login(PaymentInitationRequestResponse201 initiatedPayment) throws MalformedURLException {
		String scaRedirectLink = getScaRedirect(initiatedPayment);
		String encryptedPaymentId = initiatedPayment.getPaymentId();
		String redirectId = QuerryParser.param(scaRedirectLink, "redirectId");
		String encryptedPaymentIdFromOnlineBanking = QuerryParser.param(scaRedirectLink, "paymentId");
		
		Assert.assertEquals(encryptedPaymentId, encryptedPaymentIdFromOnlineBanking);
		
		ResponseEntity<AuthorizeResponse> pisAuth = pisApiClient.pisAuth(redirectId, encryptedPaymentId);
		URI location = pisAuth.getHeaders().getLocation();
		String authorisationId = QuerryParser.param(location.toString(), "authorisationId");
		List<String> cookieStrings = pisAuth.getHeaders().get("Set-Cookie");
		String consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		ResponseEntity<PaymentAuthorizeResponse> loginResponse = pisApiClient.login(encryptedPaymentId, authorisationId, paymentCase.getPsuId(), "12345", cu.resetCookies(cookieStrings));
		
		Assert.assertNotNull(loginResponse);
		Assert.assertTrue(loginResponse.getStatusCode().is2xxSuccessful());
		cookieStrings = loginResponse.getHeaders().get("Set-Cookie");
		consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		return loginResponse;
	}

	public ResponseEntity<PaymentInitiationStatusResponse200Json> loadPaymentStatus(String encryptedPaymentId) {
		UUID xRequestID = UUID.randomUUID();
		ResponseEntity<PaymentInitiationStatusResponse200Json> _getPaymentInitiationStatus = paymentApi
				._getPaymentInitiationStatus(paymentService, paymentProduct, encryptedPaymentId, xRequestID, digest, signature,
						tpPSignatureCertificate, psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset,
						psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation);

		PaymentInitiationStatusResponse200Json paymentInitiationStatus = _getPaymentInitiationStatus.getBody();
		
		Assert.assertNotNull(paymentInitiationStatus);

		return _getPaymentInitiationStatus;
	}

	private String getScaRedirect(PaymentInitationRequestResponse201 resp) {
		return (String) resp.getLinks().get("scaRedirect");
	}

	public ResponseEntity<PaymentAuthorizeResponse> authCode(ResponseEntity<PaymentAuthorizeResponse> paymentResponse) {
		Assert.assertNotNull(paymentResponse);
		Assert.assertTrue(paymentResponse.getStatusCode().is2xxSuccessful());
		List<String> cookieStrings = paymentResponse.getHeaders().get("Set-Cookie");
		String consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		PaymentAuthorizeResponse paymentAuthorizeResponse = paymentResponse.getBody();
		ResponseEntity<PaymentAuthorizeResponse> authrizedPaymentResponse = 
				pisApiClient.authrizedPayment(paymentAuthorizeResponse.getEncryptedConsentId(), paymentAuthorizeResponse.getAuthorisationId(), 
						cu.resetCookies(cookieStrings), "123456");
		Assert.assertNotNull(authrizedPaymentResponse);
		Assert.assertTrue(authrizedPaymentResponse.getStatusCode().is2xxSuccessful());
		cookieStrings = authrizedPaymentResponse.getHeaders().get("Set-Cookie");
		consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		return authrizedPaymentResponse;
	}

	public ResponseEntity<PaymentAuthorizeResponse> choseScaMethod(ResponseEntity<PaymentAuthorizeResponse> paymentResponse) {
		Assert.assertNotNull(paymentResponse);
		Assert.assertTrue(paymentResponse.getStatusCode().is2xxSuccessful());
		List<String> cookieStrings = paymentResponse.getHeaders().get("Set-Cookie");
		String consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		PaymentAuthorizeResponse paymentAuthorizeResponse = paymentResponse.getBody();
		ScaUserDataTO scaUserDataTO = paymentAuthorizeResponse.getScaMethods().iterator().next();
		ResponseEntity<PaymentAuthorizeResponse> authrizedPaymentResponse = pisApiClient.selectMethod(paymentAuthorizeResponse.getEncryptedConsentId(), paymentAuthorizeResponse.getAuthorisationId(), 
				scaUserDataTO.getId(), cu.resetCookies(cookieStrings));
		Assert.assertNotNull(authrizedPaymentResponse);
		Assert.assertTrue(authrizedPaymentResponse.getStatusCode().is2xxSuccessful());
		cookieStrings = authrizedPaymentResponse.getHeaders().get("Set-Cookie");
		consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
		Assert.assertNotNull(consentCookieString);
		accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
		Assert.assertNotNull(accessTokenCookieString);

		return authrizedPaymentResponse;
	}
	
	public void checkTxStatus(String paymentId, TransactionStatus expectedStatus) {
		ResponseEntity<PaymentInitiationStatusResponse200Json> loadPaymentStatusResponseWrapper;
		loadPaymentStatusResponseWrapper = loadPaymentStatus(paymentId);
		PaymentInitiationStatusResponse200Json loadPaymentStatusResponse = loadPaymentStatusResponseWrapper.getBody();
		Assert.assertNotNull(loadPaymentStatusResponse);
		TransactionStatus currentStatus = loadPaymentStatusResponse.getTransactionStatus();
		Assert.assertNotNull(currentStatus);
		Assert.assertEquals(expectedStatus, currentStatus);
	}

	public void validateResponseStatus(PaymentAuthorizeResponse authResponse, ScaStatusTO expectedScaStatus, TransactionStatusTO expectedPaymentStatus) {
		ScaStatusTO scaStatus = authResponse.getScaStatus();
		Assert.assertNotNull(scaStatus);
		Assert.assertEquals(expectedScaStatus, scaStatus);
		Assert.assertEquals(expectedPaymentStatus, authResponse.getSinglePayment().getPaymentStatus());
	}
	
}

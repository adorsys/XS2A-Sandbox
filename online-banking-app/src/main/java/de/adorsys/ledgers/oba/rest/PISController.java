package de.adorsys.ledgers.oba.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.ledgers.consent.psu.rest.client.AspspConsentDataRestClient;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisRestClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.consentref.ConsentReference;
import de.adorsys.ledgers.oba.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.consentref.ConsentType;
import de.adorsys.ledgers.oba.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.domain.OnlineBankingResponse;
import de.adorsys.ledgers.oba.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.domain.ValidationCode;
import de.adorsys.ledgers.oba.exception.PaymentWorkflowException;
import de.adorsys.ledgers.oba.mapper.BulkPaymentMapper;
import de.adorsys.ledgers.oba.mapper.PeriodicPaymentMapper;
import de.adorsys.ledgers.oba.mapper.SinglePaymentMapper;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController(PISController.BASE_PATH)
@RequestMapping(PISController.BASE_PATH)
@Api(value = PISController.BASE_PATH, tags = "PSU PIS", description = "Provides access to online banking payment functionality")
public class PISController extends AbstractXISController {

	static final String BASE_PATH = "/pis";
	@Autowired
	private ConsentReferencePolicy referencePolicy;
	@Autowired
	private ResponseUtils responseUtils;
	@Autowired
	private CmsPsuPisRestClient cmsPsuPisRestClient;
	@Autowired
	private PaymentRestClient ledgersRestClient;
	@Autowired
	private AspspConsentDataRestClient aspspConsentDataRestClient;
	
	@Autowired
	private SinglePaymentMapper singlePaymentMapper;
	@Autowired
	private BulkPaymentMapper bulkPaymentMapper;
	@Autowired
	private PeriodicPaymentMapper periodicPaymentMapper;
	
	@Autowired
	private TokenStorageService tokenStorageService;
	
	@Autowired
	private AuthRequestInterceptor authInterceptor;
	

	/**
	 * STEP-P0: payment Entry Point
	 * 
	 * Receptions a payment authorization link. Generate an eca-id associated with the login process.
	 * 
	 * @param redirectId
	 * @param encryptedPaymentId
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping(path="/start", params= {"redirectId","encryptedPaymentId"})
	@ApiOperation(value = "Entry point for authenticating payment requests.")
	public ResponseEntity<OnlineBankingResponse> pisStart(
			@RequestParam(name = "redirectId") String redirectId,
			@RequestParam(name = "encryptedPaymentId") String encryptedPaymentId,
			HttpServletRequest request,
			HttpServletResponse response) {
		return auth(redirectId, ConsentType.PIS, encryptedPaymentId, request, response);
	}

	@PostMapping(path="/{paymentId}/authorisation/{authorisationId}/initiate")
	@ApiOperation(value = "Calls the consent validation page.", 
		authorizations = @Authorization(value = "apiKey"))
	public ResponseEntity<OnlineBankingResponse> initiatePayment(
			@PathVariable("paymentId") String paymentId,
			@PathVariable("authorisationId") String authorisationId,
			MiddlewareAuthentication auth,
			@CookieValue(name=ResponseUtils.CONSENT_COOKIE_NAME) String consentCookieString,
			HttpServletResponse response) {
		
		try {
			// Identity the link and load the workflow.
			PaymentWorkflow paymentWorkflow = identifyPayment(paymentId, authorisationId, consentCookieString, auth, response);
			
			// cehck payment type
			PaymentType paymentType = checkAndGetPaymentType(paymentId, authorisationId, paymentWorkflow, auth, response);

			scaStatus(paymentId, authorisationId, ScaStatus.PSUAUTHENTICATED, auth, response);

			SCAPaymentResponseTO initiatePaymentResponse = initiatePayment(auth, response, paymentWorkflow, PaymentTypeTO.valueOf(paymentType.name()));
			processPaymentResponse(paymentWorkflow, initiatePaymentResponse);

			ScaStatusTO scaStatus = initiatePaymentResponse.getScaStatus();
			scaStatus(paymentId, authorisationId, ScaStatus.valueOf(scaStatus.name()), auth, response);
			updatePaymentStatus(response, paymentWorkflow);
			updateAspspConsentData(paymentWorkflow, initiatePaymentResponse, response);

			// Store result in token.
			responseUtils.setCookies(response, paymentWorkflow.getConsentReference(), auth.getBearerToken().getAccess_token(), auth.getBearerToken().getAccessTokenObject());
			return ResponseEntity.ok(paymentWorkflow.getAuthResponse());
		} catch (PaymentWorkflowException e) {
			return e.getError();
		}
	}

	private void processPaymentResponse(PaymentWorkflow paymentWorkflow, SCAPaymentResponseTO paymentResponse) {
		paymentWorkflow.getAuthResponse().setScaId(paymentResponse.getPaymentId());
		paymentWorkflow.getAuthResponse().setAuthorisationId(paymentResponse.getAuthorisationId());
		paymentWorkflow.getAuthResponse().setScaStatus(paymentResponse.getScaStatus());
		paymentWorkflow.getAuthResponse().setScaMethods(paymentResponse.getScaMethods());
	}

	@PostMapping("/{paymentId}/authorisation/{authorisationId}/methods/{scaMethodId}")
	@ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
	public ResponseEntity<OnlineBankingResponse> selectMethod(
			@PathVariable("paymentId") String paymentId,
			@PathVariable("authorisationId") String authorisationId,
			@PathVariable("scaMethodId") String scaMethodId,
			MiddlewareAuthentication auth,
			@CookieValue(name=ResponseUtils.CONSENT_COOKIE_NAME) String consentCookieString,
			HttpServletResponse response) {
		try {
			PaymentWorkflow paymentWorkflow = identifyPayment(paymentId, authorisationId, consentCookieString, auth, response);
			
			SCAPaymentResponseTO scaPaymentResponse = selectMethod(auth, paymentId, authorisationId, scaMethodId);
			processPaymentResponse(paymentWorkflow, scaPaymentResponse);

			ScaStatusTO scaStatus = scaPaymentResponse.getScaStatus();
			scaStatus(paymentId, authorisationId, ScaStatus.valueOf(scaStatus.name()), auth, response);
			updatePaymentStatus(response, paymentWorkflow);			
			updateAspspConsentData(paymentWorkflow, scaPaymentResponse, response);

			responseUtils.setCookies(response, paymentWorkflow.getConsentReference(), auth.getBearerToken().getAccess_token(), auth.getBearerToken().getAccessTokenObject());
			return ResponseEntity.ok(paymentWorkflow.getAuthResponse());
		} catch (PaymentWorkflowException e) {
			return e.getError();
		}			
	}

	private SCAPaymentResponseTO selectMethod(MiddlewareAuthentication auth, String paymentId, String authorisationId, String scaMethodId) {
		try {
			authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());
			
			return ledgersRestClient.selectMethod(paymentId, authorisationId, scaMethodId).getBody();
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}	
	
	@PostMapping(path="/{paymentId}/authorisation/{authorisationId}/authCode", params= {"authCode"})
	@ApiOperation(value = "Provides a TAN for the validation of an authorization", authorizations = @Authorization(value = "apiKey"))
	public ResponseEntity<OnlineBankingResponse> authrizedPayment(
			@PathVariable("paymentId") String paymentId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestParam("authCode") String authCode,
			MiddlewareAuthentication auth,
			@CookieValue(name=ResponseUtils.CONSENT_COOKIE_NAME) String consentCookieString,
			HttpServletResponse response) {
		
		try {
			PaymentWorkflow paymentWorkflow = identifyPayment(paymentId, authorisationId, consentCookieString, auth, response);
			SCAPaymentResponseTO scaPaymentResponse = ledgersRestClient.authorizePayment(paymentId, authorisationId, authCode).getBody();
			processPaymentResponse(paymentWorkflow, scaPaymentResponse);

			ScaStatusTO scaStatus = scaPaymentResponse.getScaStatus();
			scaStatus(paymentId, authorisationId, ScaStatus.valueOf(scaStatus.name()), auth, response);
			updatePaymentStatus(response, paymentWorkflow);			
			updateAspspConsentData(paymentWorkflow, scaPaymentResponse, response);
			responseUtils.setCookies(response, paymentWorkflow.getConsentReference(), auth.getBearerToken().getAccess_token(), auth.getBearerToken().getAccessTokenObject());
			return ResponseEntity.ok(paymentWorkflow.getAuthResponse());
		} catch (PaymentWorkflowException e) {
			return e.getError();
		}			
	}

	private void updateAspspConsentData(PaymentWorkflow paymentWorkflow, SCAPaymentResponseTO res, HttpServletResponse httpResp) throws PaymentWorkflowException{
		CmsAspspConsentDataBase64 consentData;
		try {
			consentData = new CmsAspspConsentDataBase64(res.getPaymentId(), tokenStorageService.toBase64String(res));
		} catch (IOException e) {
			throw new PaymentWorkflowException(
					responseUtils.backToSender(paymentWorkflow.getPaymentResponse(), 
							httpResp, HttpStatus.INTERNAL_SERVER_ERROR, ValidationCode.CONSENT_DATA_UPDATE_FAILED));
		}
		ResponseEntity<?> updateAspspConsentData = aspspConsentDataRestClient.updateAspspConsentData(paymentWorkflow.getConsentReference().getEncryptedConsentId(), consentData);
		if(!HttpStatus.OK.equals(updateAspspConsentData.getStatusCode())) {
			throw new PaymentWorkflowException(
					responseUtils.backToSender(paymentWorkflow.getPaymentResponse(), 
							httpResp, updateAspspConsentData.getStatusCode(), ValidationCode.CONSENT_DATA_UPDATE_FAILED));
		}
	}

	private SCAPaymentResponseTO initiatePayment(MiddlewareAuthentication auth, HttpServletResponse response,
			final PaymentWorkflow paymentWorkflow, PaymentTypeTO paymentType) throws PaymentWorkflowException {
			CmsPaymentResponse paymentResponse = paymentWorkflow.getPaymentResponse();
			Object payment = readPayment(response, paymentType, paymentResponse);
		try {
			authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());
			SCAPaymentResponseTO paymentResponseTO = ledgersRestClient.initiatePayment(paymentType, payment).getBody();
			paymentWorkflow.setPaymentStatus(paymentResponseTO.getTransactionStatus().name());
			paymentWorkflow.setAuthCodeMessage(paymentResponseTO.getPsuMessage());
			paymentWorkflow.setAuthResponse(new PaymentAuthorizeResponse(paymentType, payment));
			return paymentResponseTO;
		} catch(FeignException f) {
			paymentWorkflow.setErrorCode(HttpStatus.valueOf(f.status()));
			throw f;
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}

	private void updatePaymentStatus(HttpServletResponse response, PaymentWorkflow paymentWorkflow)
			throws PaymentWorkflowException {
		ResponseEntity<Void> updatePaymentStatus = cmsPsuPisRestClient.updatePaymentStatus(paymentWorkflow.getPaymentResponse().getPayment().getPaymentId(), paymentWorkflow.getPaymentStatus());
		if(!HttpStatus.OK.equals(updatePaymentStatus.getStatusCode())) {
			throw new PaymentWorkflowException(responseUtils.couldNotProcessRequest("Could not set payment status. See status code.", updatePaymentStatus.getStatusCode(), response));
		}
	}

	private Object readPayment(HttpServletResponse response, PaymentTypeTO paymentType,
			CmsPaymentResponse paymentResponse) throws PaymentWorkflowException {
		switch (paymentType) {
		case SINGLE:
			return singlePaymentMapper.toPayment((CmsSinglePayment)paymentResponse.getPayment());
		case BULK:
			return bulkPaymentMapper.toPayment((CmsBulkPayment)paymentResponse.getPayment());
		case PERIODIC:
			return periodicPaymentMapper.toPayment((CmsPeriodicPayment)paymentResponse.getPayment());
		default:
			throw new PaymentWorkflowException(responseUtils.badRequest(String.format("Payment type %s not supported.", paymentType.name()), response));
		}
	}
	
	private PaymentWorkflow identifyPayment(String paymentId, String authorizationId, String consentCookieString, MiddlewareAuthentication auth, HttpServletResponse response) throws PaymentWorkflowException{
		ConsentReference consentReference = null;
		try {
			consentReference = referencePolicy.fromRequest(paymentId, consentCookieString);
		} catch (InvalidConsentException e) {
			throw new PaymentWorkflowException(responseUtils.forbidden("Invalid credentials. ScaId not matching consent cookie", response));
		}

		ResponseEntity<CmsPaymentResponse> responseEntity = loadPaymentByRedirectId(auth, consentReference, response);

		if (responseEntity.getStatusCode()==HttpStatus.REQUEST_TIMEOUT) {
			// ---> if(Expired, TPP-Redirect-URL)
	         // 3.a0) LogOut User
	         // 3.a1) Send back to TPP
			CmsPaymentResponse payment = responseEntity.getBody();
			scaStatus(paymentId, authorizationId, ScaStatus.FAILED, auth, response);
			String location = StringUtils.isNotBlank(payment.getTppNokRedirectUri())
				?payment.getTppNokRedirectUri()
					:payment.getTppOkRedirectUri();
			throw new PaymentWorkflowException(responseUtils.redirect(location, response));
		} else if (responseEntity.getStatusCode()!=HttpStatus.OK) {
			throw new PaymentWorkflowException(responseUtils.couldNotProcessRequest(responseEntity.getStatusCode(), response));
		}
		
		PaymentWorkflow workflow = new PaymentWorkflow(responseEntity.getBody());
		workflow.setConsentReference(consentReference);
		return workflow;
	}
	
	private PaymentType checkAndGetPaymentType(String paymentId, String authorisationId, PaymentWorkflow paymentWorkflow, MiddlewareAuthentication auth, HttpServletResponse response) throws PaymentWorkflowException {
		CmsPaymentResponse paymentResponse = paymentWorkflow.getPaymentResponse();
		CmsPayment payment = paymentResponse.getPayment();
		// Validate Payment Type Supported
		PaymentType paymentType = payment.getPaymentType();
		if(paymentType==null) {
			scaStatus(paymentId, authorisationId, ScaStatus.FAILED, auth, response);
			throw new PaymentWorkflowException(responseUtils.backToSender(paymentResponse, response,HttpStatus.BAD_REQUEST, ValidationCode.MISSING_PAYMENT_TYPE));				
		}
		
		return paymentType;
	}
	
	private String psuId(MiddlewareAuthentication auth) {
		return auth.getBearerToken().getAccessTokenObject().getLogin();
	}

	private void scaStatus(String paymentId, String authorisationId, ScaStatus status, MiddlewareAuthentication auth, HttpServletResponse response) throws PaymentWorkflowException {
		ResponseEntity<Void> resp = cmsPsuPisRestClient.updateAuthorisationStatus(psuId(auth), null, null, null, paymentId, authorisationId, status.name());
		if(!HttpStatus.OK.equals(resp.getStatusCode())) {
			throw new PaymentWorkflowException(responseUtils.couldNotProcessRequest("Error updating authorisation status. See error code.", resp.getStatusCode(), response));
		}
	}

	private ResponseEntity<CmsPaymentResponse> loadPaymentByRedirectId(MiddlewareAuthentication auth,
			ConsentReference consentReference, HttpServletResponse response) throws PaymentWorkflowException {
		BearerTokenTO loginToken = auth.getBearerToken();

		String psuId = loginToken.getAccessTokenObject().getLogin();
		String psuIdType = null;
		String psuCorporateId = null;
		String psuCorporateIdType = null;
		String redirectId = consentReference.getRedirectId();
		// 4. After user login: 
		ResponseEntity<CmsPaymentResponse> responseEntity = cmsPsuPisRestClient.getPaymentByRedirectId(psuId, psuIdType, psuCorporateId, psuCorporateIdType, redirectId);
		HttpStatus statusCode = responseEntity.getStatusCode();
		if(HttpStatus.REQUEST_TIMEOUT.equals(statusCode) || HttpStatus.OK.equals(statusCode)) {
			return responseEntity;
		}
		
		if(HttpStatus.NOT_FOUND.equals(statusCode)) {
			// ---> if(NotFound)
			throw new PaymentWorkflowException(responseUtils.requestWithRedNotFound(response));
		}
				
		throw new PaymentWorkflowException(responseUtils.couldNotProcessRequest(statusCode, response));
	}

	@Override
	public String getBasePath() {
		return BASE_PATH;
	}
}

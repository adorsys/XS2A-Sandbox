package de.adorsys.ledgers.oba.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.ledgers.oba.consentref.ConsentType;
import de.adorsys.ledgers.oba.domain.OnlineBankingResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(AISController.BASE_PATH)
@RequestMapping(AISController.BASE_PATH)
@Api(value = AISController.BASE_PATH, tags = "PSU AIS", description = "Provides access to online banking payment functionality")
public class AISController extends AbstractXISController {

	static final String BASE_PATH = "/ais";

	@GetMapping(path="/auth", params= {"redirectId","encryptedConsentId"})
	@ApiOperation(value = "Entry point for authenticating ais consent requests.")
	public ResponseEntity<OnlineBankingResponse> pisAuth(
			@RequestParam(name = "redirectId") String redirectId,
			@RequestParam(name = "encryptedConsentId") String encryptedConsentId,
			HttpServletRequest request,
			HttpServletResponse response) {
		return auth(redirectId, ConsentType.AIS, encryptedConsentId, request, response);
	}

	@Override
	public String getBasePath() {
		return BASE_PATH;
	}

	
	// SCA Status is STARTED
	
	// 1. Store redirect link in a cookie
	
	// 2. Redirect User to login Page (Either simple login, or login with TAN)
	
	// 3. After user login: 
	// x.x.x.controller.CmsPsuAisConsentController.getConsentByRedirectId(String, String, String, String, String)
	// ---> if(Expired, TPP-Redirect-URL)
	         // 3.a0) LogOut User
	         // 3.a1) Send back to TPP
	// ---> if(NotFound)
    		// 3.b0) LogOut User
    		// 
	// ---> if(OK, TPP-Redirect-URL, CmsAisConsentResponse)
			// 3.c0) Update SCA Status to "PSU-AUTHENTICATED" -> x.controller.CmsPsuAisConsentController.updateAuthorisationStatus(String, String, String, String, String, String, String)
	
			// 3.c1) Check de.adorsys.psd2.consent.api.ais.AisAccountConsent.aisConsentRequestType
				// 3.c1.i - GLOBAL, ALL_AVAILABLE_ACCOUNTS,DEDICATED_ACCOUNTS
						// 3.c1.ia) Display corresponding page to the user for confirmation
						// 3.c1.ib) Call consent initiation with core banking
						// 3.c1.ic) Select SCA Methods
							// Update SCA Status to: "SCA-METHOD-SELECTED"
						// 3.c1.id) Proceed with SCA (Collect TAN), Confirm Consent
							// 3.c1.id-1 VALID TAN
								// a) Update SCA Status to: "FINALIZED"
								// b) Update Consent Status to: x.x.CmsPsuAisConsentController.confirmConsent(String, String, String, String, String)
								// c) Display Result Page 
								// e) Redirect to TPP OK Page
					
							// 3.c3ii INVALID TAN (n incorrect TAN)
								// 3.c3ii-a) Update SCA Status "FAILLED"
								// 3.c3ii-b) Update Payment Status "RJCT"
								// 3.c3ii-c) Display Result Page 
								// 3.c3ii-d) Redirect to TPP NOK URL if present. If not present, redirect to OK.

    			// BANK_OFFERED,
	
    		// 3.c1) Initiate the payment with the bank
			// 3.c2) Select SCA Method
				// Update SCA Status to: "SCA-METHOD-SELECTED" -> CmsPsuPisController.updateAuthorisationStatus
    		// 3.c3) Proceed with SCA (Collect TAN), Executes Payment
				// 3.c3i VALID TAN
					// 3.c3i-a) Update SCA Status to: "FINALIZED"
					// de.adorsys.psd2.consent.web.psu.controller.CmsPsuPisController.updateAuthorisationStatus(String, String, String, String, String, String, String)
					// 3.c3i-b) Update Payment Status to: "ACTC"
					// de.adorsys.psd2.consent.web.psu.controller.CmsPsuPisController.updatePaymentStatus(String, String)
					// 3.c3i-d) Display Result Page 
					// 3.c3i-e) Redirect to TPP OK Page
	
				// 3.c3ii INVALID TAN (n incorrect TAN)
					// 3.c3ii-a) Update SCA Status "FAILLED"
					// 3.c3ii-b) Update Payment Status "RJCT"
					// 3.c3ii-c) Display Result Page 
					// 3.c3ii-d) Redirect to TPP NOK URL if present. If not present, redirect to OK.
	
	
//	
//	AuthorizeResponse authResponse = new AuthorizeResponse();
//	// load payment by id
//	ConsentReference consentReference;
//	try {
//		consentReference = referencePolicy.fromPISURL(paymentId);
//	} catch (InvalidConsentException e) {
//		return responseUtils.unknownCredentials(authResponse);
//	}
//	authResponse.setScaId(consentReference.getScaId());
//	String basePath = StringUtils.substringBefore(request.getRequestURL().toString(), BASE_PATH);
//	Map<String, Object> map = new HashMap<>();
//	map.put(SCAController.SCA_ID_REQUEST_PARAM, consentReference.getScaId());
//	URI locationHeader = UriComponentsBuilder.fromUriString(basePath)
//			.path(SCAController.BASE_PATH)
//			.path(SCAController.LOGIN_SCA_ID)
//			.build(map);
//	response.addHeader(LOCATION_HEADER_NAME, locationHeader.toString());
//	responseUtils.setCookies(response, consentReference, null, null);
//	return ResponseEntity.status(302).build();
	
}

package de.adorsys.ledgers.oba.rest.server.resource;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient;
import org.adorsys.ledgers.consent.aspsp.rest.client.CreatePiisConsentRequest;
import org.adorsys.ledgers.consent.aspsp.rest.client.CreatePiisConsentResponse;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentWorkflow;
import de.adorsys.ledgers.oba.rest.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.rest.api.domain.PIISConsentCreateResponse;
import de.adorsys.ledgers.oba.rest.api.domain.ValidationCode;
import de.adorsys.ledgers.oba.rest.api.exception.ConsentAuthorizeException;
import de.adorsys.ledgers.oba.rest.api.resource.AISApi;
import de.adorsys.ledgers.oba.rest.server.mapper.AisConsentMapper;
import de.adorsys.ledgers.oba.rest.server.mapper.CreatePiisConsentRequestMapper;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisConsentResponse;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(AISController.BASE_PATH)
@RequestMapping(AISController.BASE_PATH)
@Api(value = AISController.BASE_PATH, tags = "PSU AIS", description = "Provides access to online banking account functionality")
public class AISController extends AbstractXISController implements AISApi {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;
	@Autowired
	private UserMgmtRestClient userMgmtRestClient;
	@Autowired
	private AuthRequestInterceptor authInterceptor;
	@Autowired
	private CmsPsuAisClient cmsPsuAisClient;
	@Autowired
	private AisConsentMapper consentMapper;
	@Autowired
	private ConsentRestClient consentRestClient;
	@Autowired
	private CmsAspspPiisClient cmsAspspPiisClient;
	@Autowired
	private CreatePiisConsentRequestMapper createPiisConsentRequestMapper;

	@Override
	@ApiOperation(value = "Entry point for authenticating ais consent requests.")
	public ResponseEntity<AuthorizeResponse> aisAuth(@RequestParam(name = "redirectId") String redirectId,
			@RequestParam(name = "encryptedConsentId") String encryptedConsentId) {
		return auth(redirectId, ConsentType.AIS, encryptedConsentId, request, response);
	}

	@Override
	public String getBasePath() {
		return BASE_PATH;
	}

	@Override
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public ResponseEntity<ConsentAuthorizeResponse> login(String encryptedConsentId, String authorisationId,
			String login, String pin, String consentCookieString) {

		ConsentWorkflow workflow;
		try {
			workflow = identifyConsent(encryptedConsentId, authorisationId, false, consentCookieString, login, response,
					null);
		} catch (ConsentAuthorizeException e) {
			return e.getError();
		}

		// Authorize
		ResponseEntity<SCALoginResponseTO> authoriseForConsent = userMgmtRestClient.authoriseForConsent(login, pin,
				workflow.consentId(), workflow.authId(), OpTypeTO.CONSENT);
		processSCAResponse(workflow, authoriseForConsent.getBody());
		boolean success = AuthUtils.success(authoriseForConsent);

		if (success) {
			String psuId = AuthUtils.psuId(workflow.bearerToken());
			try {
				scaStatus(workflow, psuId, response);
				startConsent(workflow);

				// Select sca if no alternative.
				if (workflow.singleScaMethod()) {
					ScaUserDataTO scaUserDataTO = workflow.scaMethods().iterator().next();
					selectMethod(scaUserDataTO.getId(), workflow);
				}

				updateScaStatusConsentStatusConsentData(psuId, workflow);
			} catch (ConsentAuthorizeException e) {
				return e.getError();
			}

			switch (workflow.scaStatus()) {
			case EXEMPTED:
				// Bad request
				// failed Message. No repeat. Delete cookies.
				responseUtils.removeCookies(response);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			case PSUIDENTIFIED:
			case FINALISED:
			case PSUAUTHENTICATED:
			case SCAMETHODSELECTED:
				responseUtils.setCookies(response, workflow.getConsentReference(),
						workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
				return ResponseEntity.ok(workflow.getAuthResponse());
			case STARTED:
			case FAILED:
			default:
				// failed Message. No repeat. Delete cookies.
				responseUtils.removeCookies(response);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} else {
			// failed Message. No repeat. Delete cookies.
			responseUtils.removeCookies(response);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@Override
	public ResponseEntity<ConsentAuthorizeResponse> authrizedConsent(
			String encryptedConsentId,
			String authorisationId,
			String consentAndaccessTokenCookieString, String authCode) {
		
		String psuId = AuthUtils.psuId(auth);
		try {
			ConsentWorkflow workflow = identifyConsent(encryptedConsentId, authorisationId, true, consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());

			authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
			
			SCAConsentResponseTO scaConsentResponse = consentRestClient.authorizeConsent(workflow.consentId(), authorisationId, authCode).getBody();

			processSCAResponse(workflow, scaConsentResponse);
			cmsPsuAisClient.confirmConsent(workflow.consentId(), psuId, null, null, null, CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID);
			updateScaStatusConsentStatusConsentData(psuId, workflow);

			responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
			return ResponseEntity.ok(workflow.getAuthResponse());
		} catch (ConsentAuthorizeException e) {
			return e.getError();
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}
	
	@Override
	public ResponseEntity<ConsentAuthorizeResponse> selectMethod(
			String encryptedConsentId, String authorisationId,
			String scaMethodId, String consentAndaccessTokenCookieString) {

		String psuId = AuthUtils.psuId(auth);
		try {
			ConsentWorkflow workflow = identifyConsent(encryptedConsentId, authorisationId, true, 
					consentAndaccessTokenCookieString, psuId, response, auth.getBearerToken());
			selectMethod(scaMethodId, workflow);
			
			updateScaStatusConsentStatusConsentData(psuId, workflow);

			responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), 
					workflow.bearerToken().getAccessTokenObject());
			return ResponseEntity.ok(workflow.getAuthResponse());
		} catch (ConsentAuthorizeException e) {
			return e.getError();
		}			
	}
	
	@Override
	public ResponseEntity<PIISConsentCreateResponse> grantPiisConsent(@RequestHeader("Cookie") String consentAndaccessTokenCookieString,  CreatePiisConsentRequestTO piisConsentRequestTO) {
		
		String psuId = AuthUtils.psuId(auth);
		try {

			authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());
			
			CreatePiisConsentRequest piisConsentRequest = createPiisConsentRequestMapper.fromCreatePiisConsentRequest(piisConsentRequestTO);
			CreatePiisConsentResponse cmsCcnsent = cmsAspspPiisClient.createConsent(piisConsentRequest, psuId, null, null, null).getBody();
			
			// Attention intentional manual mapping. We fill up only the balances.
			AisConsentTO pisConsent = new AisConsentTO();
			AisAccountAccessInfoTO access = new AisAccountAccessInfoTO();
			// Only consent we take.
			access.setBalances(piisConsentRequest.getAccounts().stream().map(a->a.getIban()).collect(Collectors.toList()));
			pisConsent.setAccess(access);
			pisConsent.setFrequencyPerDay(piisConsentRequest.getAllowedFrequencyPerDay());
			pisConsent.setId(cmsCcnsent.getConsentId());
			// Intentionally set to true
			pisConsent.setRecurringIndicator(true);
			pisConsent.setTppId(piisConsentRequest.getTppInfo().getAuthorisationNumber());
			pisConsent.setUserId(psuId);
			pisConsent.setValidUntil(piisConsentRequest.getValidUntil());
			
			SCAConsentResponseTO scaConsentResponse = consentRestClient.grantPIISConsent(pisConsent).getBody();
			ResponseEntity<?> updateAspspPiisConsentDataResponse = updateAspspPiisConsentData(cmsCcnsent.getConsentId(), scaConsentResponse);
			if(!HttpStatus.OK.equals(updateAspspPiisConsentDataResponse.getStatusCode())){
				return responseUtils.error(new PIISConsentCreateResponse(), updateAspspPiisConsentDataResponse.getStatusCode(),
						"Could not update aspsp consent data", response);
			}
			// Send back same cookie. Delete any consent reference.
			responseUtils.setCookies(response, null, auth.getBearerToken().getAccess_token(), auth.getBearerToken().getAccessTokenObject());
			
			AisConsentTO consent = scaConsentResponse.getBearerToken().getAccessTokenObject().getConsent();
			return ResponseEntity.ok(new PIISConsentCreateResponse(consent));
		} catch (IOException e) {
			return responseUtils.error(new PIISConsentCreateResponse(), HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage(), response);
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}

	private ConsentWorkflow identifyConsent(String encryptedConsentId, String authorizationId, boolean strict,
			String consentCookieString, String psuId, HttpServletResponse response, BearerTokenTO bearerToken)
			throws ConsentAuthorizeException {
		ConsentReference consentReference = null;
		try {
			String consentCookie = responseUtils.consentCookie(consentCookieString);
			consentReference = referencePolicy.fromRequest(encryptedConsentId, authorizationId, consentCookie, strict);
		} catch (InvalidConsentException e) {
			throw new ConsentAuthorizeException(responseUtils.forbidden(authResp(), e.getMessage(), response));
		}

		CmsAisConsentResponse cmsConsentResponse = loadConsentByRedirectId(psuId, consentReference, response);

		ConsentWorkflow workflow = new ConsentWorkflow(cmsConsentResponse, consentReference);
		AisConsentTO aisConsentTO = consentMapper.toTo(cmsConsentResponse.getAccountConsent());

		workflow.setAuthResponse(new ConsentAuthorizeResponse(aisConsentTO));
		workflow.getAuthResponse().setAuthorisationId(cmsConsentResponse.getAuthorisationId());
		workflow.getAuthResponse().setEncryptedConsentId(encryptedConsentId);
		if (bearerToken != null) {
			SCAConsentResponseTO scaConsentResponseTO = new SCAConsentResponseTO();
			scaConsentResponseTO.setBearerToken(bearerToken);
			workflow.setScaResponse(scaConsentResponseTO);
		}
		return workflow;
	}

	private void processSCAResponse(ConsentWorkflow workflow, SCAResponseTO consentResponse) {
		workflow.setScaResponse(consentResponse);
		workflow.getAuthResponse().setAuthorisationId(consentResponse.getAuthorisationId());
		workflow.getAuthResponse().setScaStatus(consentResponse.getScaStatus());
		workflow.getAuthResponse().setScaMethods(consentResponse.getScaMethods());
		workflow.setAuthCodeMessage(consentResponse.getPsuMessage());
	}

	private void scaStatus(ConsentWorkflow workflow, String psuId, HttpServletResponse response)
			throws ConsentAuthorizeException {
		String status = workflow.getAuthResponse().getScaStatus().name();
		ResponseEntity<Boolean> resp = cmsPsuAisClient.updateAuthorisationStatus(workflow.consentId(), status,
				workflow.authId(), psuId, null, null, null, CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID);
		if (!HttpStatus.OK.equals(resp.getStatusCode())) {
			throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(),
					"Error updating authorisation status. See error code.", resp.getStatusCode(), response));
		}
	}

	private ConsentAuthorizeResponse authResp() {
		return new ConsentAuthorizeResponse();
	}

	private void startConsent(final ConsentWorkflow workflow)
			throws ConsentAuthorizeException {
		try {
			authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
			// INFO. Server does not set the bearer token.
			BearerTokenTO bearerToken = workflow.bearerToken();
			AisConsentTO consent = consentMapper.toTo(workflow.getConsentResponse().getAccountConsent());
			SCAConsentResponseTO sca = consentRestClient.startSCA(workflow.consentId(), consent).getBody();
			// INFO. Server does not set the bearer token.
			sca.setBearerToken(bearerToken);
			processSCAResponse(workflow, sca);
			

		} catch (FeignException f) {
			workflow.setErrorCode(HttpStatus.valueOf(f.status()));
			throw f;
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}

	private SCAConsentResponseTO selectMethod(String scaMethodId, final ConsentWorkflow workflow) {
		try {
			authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());
			// INFO. Server does not set the bearer token.
			BearerTokenTO bearerToken = workflow.bearerToken();
			SCAConsentResponseTO sca = consentRestClient.selectMethod(workflow.consentId(), workflow.authId(), scaMethodId).getBody();
			// INFO. Server does not set the bearer token.
			sca.setBearerToken(bearerToken);
			processSCAResponse(workflow, sca);
			return sca;
			
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}	

	private void updateScaStatusConsentStatusConsentData(String psuId, ConsentWorkflow workflow)
			throws ConsentAuthorizeException {
		// UPDATE CMS
		scaStatus(workflow, psuId, response);
		updateAspspConsentData(workflow, response);
	}
	

	private void updateAspspConsentData(ConsentWorkflow workflow, HttpServletResponse httpResp) throws ConsentAuthorizeException{
		CmsAspspConsentDataBase64 consentData;
		try {
			consentData = new CmsAspspConsentDataBase64(workflow.consentId(), tokenStorageService.toBase64String(workflow.getScaResponse()));
		} catch (IOException e) {
			throw new ConsentAuthorizeException(
					responseUtils.backToSender(authResp(), workflow.getConsentResponse().getTppNokRedirectUri(),
							workflow.getConsentResponse().getTppOkRedirectUri(),
							httpResp, HttpStatus.INTERNAL_SERVER_ERROR, ValidationCode.CONSENT_DATA_UPDATE_FAILED));
		}
		ResponseEntity<?> updateAspspConsentData = aspspConsentDataClient.updateAspspConsentData(
				workflow.getConsentReference().getEncryptedConsentId(), consentData);
		if(!HttpStatus.OK.equals(updateAspspConsentData.getStatusCode())) {
			throw new ConsentAuthorizeException(
					responseUtils.backToSender(authResp(), workflow.getConsentResponse().getTppNokRedirectUri(), 
							workflow.getConsentResponse().getTppOkRedirectUri(),							
							httpResp, updateAspspConsentData.getStatusCode(), ValidationCode.CONSENT_DATA_UPDATE_FAILED));
		}
	}
	
	private ResponseEntity<?> updateAspspPiisConsentData(String consentId, SCAConsentResponseTO consentResponse) throws IOException{
		CmsAspspConsentDataBase64 consentData = new CmsAspspConsentDataBase64(consentId, tokenStorageService.toBase64String(consentResponse));
		// Encrypted consentId???
		return aspspConsentDataClient.updateAspspConsentData(consentId, consentData);
	}
	
	

	@SuppressWarnings("PMD.CyclomaticComplexity")
	private CmsAisConsentResponse loadConsentByRedirectId(String psuId,
			ConsentReference consentReference, HttpServletResponse response) throws ConsentAuthorizeException {
		String psuIdType = null;
		String psuCorporateId = null;
		String psuCorporateIdType = null;
		String redirectId = consentReference.getRedirectId();
		// 4. After user login: 
		ResponseEntity<CmsAisConsentResponse> responseEntity = cmsPsuAisClient.getConsentByRedirectId(
				psuId, psuIdType, psuCorporateId, psuCorporateIdType, redirectId, CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID);
		
		HttpStatus statusCode = responseEntity.getStatusCode();
		
		if(HttpStatus.OK.equals(statusCode)) {
			return responseEntity.getBody();
		}
		
		if(HttpStatus.NOT_FOUND.equals(statusCode)) {
			// ---> if(NotFound)
			throw new ConsentAuthorizeException(responseUtils.requestWithRedNotFound(authResp(), response));
		}
		
		if(HttpStatus.REQUEST_TIMEOUT.equals(statusCode)) {
			// ---> if(Expired, TPP-Redirect-URL)
	         // 3.a0) LogOut User
	         // 3.a1) Send back to TPP
			CmsAisConsentResponse consent = responseEntity.getBody();
			String location = StringUtils.isNotBlank(consent.getTppNokRedirectUri())
				?consent.getTppNokRedirectUri()
					:consent.getTppOkRedirectUri();
			throw new ConsentAuthorizeException(responseUtils.redirect(location, response));
		} else if (responseEntity.getStatusCode()!=HttpStatus.OK) {
			throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), responseEntity.getStatusCode(), response));
		}
		
		throw new ConsentAuthorizeException(responseUtils.couldNotProcessRequest(authResp(), statusCode, response));
	}
	
	
}

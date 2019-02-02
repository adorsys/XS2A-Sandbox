package de.adorsys.ledgers.oba.rest.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.rest.api.domain.PIISConsentCreateResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@Api(value = AISApi.BASE_PATH, tags = "PSU AIS", description = "Provides access to online banking payment functionality")
public interface AISApi {
	String BASE_PATH = "/ais";

	@GetMapping(path="/auth", params= {"redirectId","encryptedConsentId"})
	@ApiOperation(value = "Entry point for authenticating ais consent requests.")
	ResponseEntity<AuthorizeResponse> aisAuth(
			@RequestParam(name = "redirectId") String redirectId,
			@RequestParam(name = "encryptedConsentId") String encryptedConsentId);

	/**
	 * Identifies the user by login an pin. Return sca methods information
	 * 
	 * @param encryptedConsentId the encryptedConsentId
	 * @param authorisationId the auth id
	 * @param login the login 
	 * @param pin the password
	 * @param consentCookieString the cosent cookie
	 * @return ConsentAuthorizeResponse
	 */
	@PostMapping(path="/{encryptedConsentId}/authorisation/{authorisationId}/login")
	@ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
	ResponseEntity<ConsentAuthorizeResponse> login(
			@PathVariable("encryptedConsentId") String encryptedConsentId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestParam("login") String login,
			@RequestParam("pin") String pin, 
			@RequestHeader("Cookie") String consentCookieString);
	
	/**
	 * Selects the SCA Method for use.
	 * 
	 * @param encryptedConsentId the sca id
	 * @param authorisationId the auth id
	 * @param scaMethodId sca
	 * @param consentAndaccessTokenCookieString the cosent cookie
	 * @return ConsentAuthorizeResponse
	 */
	@PostMapping("/{encryptedConsentId}/authorisation/{authorisationId}/methods/{scaMethodId}")
	@ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<ConsentAuthorizeResponse> selectMethod(
			@PathVariable("encryptedConsentId") String encryptedConsentId,
			@PathVariable("authorisationId") String authorisationId,
			@PathVariable("scaMethodId") String scaMethodId,
			@RequestHeader("Cookie") String consentAndaccessTokenCookieString);

	/**
	 * Provides a TAN for the validation of an authorization
	 * 
	 * @param encryptedConsentId the sca id
	 * @param authorisationId the auth id
	 * @param consentAndaccessTokenCookieString the cosent cookie
	 * @param authCode the auth code
	 * @return ConsentAuthorizeResponse
	 */
	@PostMapping(path="/{encryptedConsentId}/authorisation/{authorisationId}/authCode", params= {"authCode"})
	@ApiOperation(value = "Provides a TAN for the validation of an authorization", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<ConsentAuthorizeResponse> authrizedConsent(
			@PathVariable("encryptedConsentId") String encryptedConsentId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestHeader("Cookie") String consentAndaccessTokenCookieString,
			@RequestParam("authCode") String authCode);

	@PostMapping(path="/piis")
	@ApiOperation(value = "Grant a piis consent", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<PIISConsentCreateResponse> grantPiisConsent(
			@RequestHeader("Cookie") String consentAndaccessTokenCookieString, @RequestBody CreatePiisConsentRequestTO aisConsentTO);
}

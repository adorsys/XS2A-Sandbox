package de.adorsys.ledgers.oba.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.rest.exception.ForbiddenRestException;
import io.swagger.annotations.*;
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

import java.util.List;

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
			@RequestHeader(name="Cookie", required=false) String consentCookieString);
	
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
			@RequestHeader(name="Cookie", required=false) String consentAndaccessTokenCookieString);

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
			@RequestHeader(name="Cookie", required=false) String consentAndaccessTokenCookieString,
			@RequestParam("authCode") String authCode);

	@PostMapping(path="/piis")
	@ApiOperation(value = "Grant a piis consent", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<PIISConsentCreateResponse> grantPiisConsent(
			@RequestHeader(name="Cookie", required=false) String consentAndaccessTokenCookieString, @RequestBody CreatePiisConsentRequestTO aisConsentTO);

	/**
	 * Return the list of accounts linked with the current customer.
	 *
	 * @return : the list of accounts linked with the current customer.
	 */
	@GetMapping(path = "/accounts")
	@ApiOperation(value="List fo Accessible Accounts", authorizations =@Authorization(value="apiKey"),
			notes="Returns the list of all accounts linked to the connected user. "
					+ "Call only available to role CUSTOMER.")
	@ApiResponses(value={
			@ApiResponse(code=200, response= AccountDetailsTO[].class, message="List of accounts accessible to the user.")
	})
	ResponseEntity<List<AccountDetailsTO>> getListOfAccounts(@RequestHeader(name="Cookie", required=false) String accessTokenCookieString)  throws ForbiddenRestException;
}


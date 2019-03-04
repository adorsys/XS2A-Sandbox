package de.adorsys.ledgers.oba.rest.server.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;

public abstract class AbstractXISController {
	private static final Logger logger = LoggerFactory.getLogger(AbstractXISController.class);

	
	@Autowired
	protected AspspConsentDataClient aspspConsentDataClient;

	@Autowired
	protected TokenStorageService tokenStorageService;
	
	@Autowired
	protected AuthRequestInterceptor authInterceptor;
	
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	protected MiddlewareAuthentication auth;
	
	@Value("${online-banking.sca.loginpage:web/login}")
	private String loginPage;
	
	@Autowired
	protected ConsentReferencePolicy referencePolicy;
	
	@Autowired
	protected ResponseUtils responseUtils;
	
	public abstract String getBasePath();
	
	protected ResponseEntity<AuthorizeResponse> auth(
			String redirectId,
			ConsentType consentType,
			String encryptedConsentId,
			HttpServletRequest request,
			HttpServletResponse response) {
		// SCA Status is set to STARTED
		AuthorizeResponse authResponse = new AuthorizeResponse();
		
		// 1. Store redirect link in a cookie
		ConsentReference consentReference;
		try {
			consentReference = referencePolicy.fromURL(redirectId, consentType, encryptedConsentId);
			authResponse.setEncryptedConsentId(encryptedConsentId);
			authResponse.setAuthorisationId(redirectId);
			// 2. Set cookies
			responseUtils.setCookies(response, consentReference, null, null);
		} catch (InvalidConsentException e) {
			logger.info(e.getMessage());
			responseUtils.removeCookies(response);
			return responseUtils.unknownCredentials(authResponse, response);
		}
		
		String uriString = UriComponentsBuilder.fromUriString(loginPage)
			.queryParam("encryptedConsentId", authResponse.getEncryptedConsentId())
			.queryParam("authorisationId", authResponse.getAuthorisationId())
			.build().toUriString();

		response.addHeader("Location", uriString);
		return ResponseEntity.<AuthorizeResponse>ok(authResponse);
//		return responseUtils.redirect(uriString, response);
	}
}

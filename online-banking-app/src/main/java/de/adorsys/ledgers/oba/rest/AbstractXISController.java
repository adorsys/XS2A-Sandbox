package de.adorsys.ledgers.oba.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.ledgers.oba.consentref.ConsentReference;
import de.adorsys.ledgers.oba.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.consentref.ConsentType;
import de.adorsys.ledgers.oba.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.domain.OnlineBankingResponse;

public abstract class AbstractXISController {
	private static final Logger logger = LoggerFactory.getLogger(AbstractXISController.class);

	@Value("${online-banking.sca.loginpage:web/login}")
	private String loginPage;
	
	@Autowired
	private ConsentReferencePolicy referencePolicy;
	
	@Autowired
	private ResponseUtils responseUtils;
	
	public abstract String getBasePath();
	
	protected ResponseEntity<OnlineBankingResponse> auth(
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
			authResponse.setScaId(consentReference.getScaId());
			// 2. Set cookies
			responseUtils.setCookies(response, consentReference, null, null);
		} catch (InvalidConsentException e) {
			logger.info(e.getMessage());
			return responseUtils.unknownCredentials(authResponse, response);
		}
		
		String uriString = UriComponentsBuilder.fromUriString(loginPage)
			.queryParam("scaId", consentReference.getScaId())
			.build().toUriString();

		responseUtils.setCookies(response, consentReference, null, null);
		return responseUtils.redirect(uriString, response);
	}
}

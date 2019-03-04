package de.adorsys.ledgers.oba.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;

public class ConsentAuthorizeResponse extends AuthorizeResponse  {
	private AisConsentTO consent;
	private String authMessageTemplate;
	
	public ConsentAuthorizeResponse() {
	}
	
	public ConsentAuthorizeResponse(AisConsentTO consent) {
		super();
		this.consent = consent;
	}
	public String getAuthMessageTemplate() {
		return authMessageTemplate;
	}
	public void setAuthMessageTemplate(String authMessageTemplate) {
		this.authMessageTemplate = authMessageTemplate;
	}
	public AisConsentTO getConsent() {
		return consent;
	}
}

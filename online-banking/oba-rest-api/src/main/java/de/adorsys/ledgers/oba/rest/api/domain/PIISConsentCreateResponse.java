package de.adorsys.ledgers.oba.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;

public class PIISConsentCreateResponse extends OnlineBankingResponse  {
	private AisConsentTO consent;
	
	public PIISConsentCreateResponse() {
	}

	public PIISConsentCreateResponse(AisConsentTO consent) {
		super();
		this.consent = consent;
	}

	public AisConsentTO getConsent() {
		return consent;
	}

	public void setConsent(AisConsentTO consent) {
		this.consent = consent;
	}

}

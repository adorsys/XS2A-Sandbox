package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;

import java.util.List;

public class ConsentAuthorizeResponse extends AuthorizeResponse  {
	private List<AccountDetailsTO> accounts;
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

	public List<AccountDetailsTO> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountDetailsTO> accounts) {
		this.accounts = accounts;
	}

	public void setConsent(AisConsentTO consent) {
		this.consent = consent;
	}

}

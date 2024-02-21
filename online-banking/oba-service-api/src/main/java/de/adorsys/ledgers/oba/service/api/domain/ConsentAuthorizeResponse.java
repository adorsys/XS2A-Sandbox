/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

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

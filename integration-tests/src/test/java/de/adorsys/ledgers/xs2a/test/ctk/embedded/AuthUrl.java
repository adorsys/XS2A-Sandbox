/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import org.apache.commons.lang3.StringUtils;

public class AuthUrl {
	private final String encryptedConsentId;
	private final String authorizationId;

	public AuthUrl(String encryptedConsentId, String authorizationId) {
		this.encryptedConsentId = encryptedConsentId;
		this.authorizationId = authorizationId;
	}
	public String getEncryptedConsentId() {
		return encryptedConsentId;
	}
	public String getAuthorizationId() {
		return authorizationId;
	}

	public static AuthUrl parse(String url) {
		String authorizationId = StringUtils.substringAfterLast(url, "authorisations/");
		String encryptedConsentId = StringUtils.substringBeforeLast(url, "/authorisations");
		encryptedConsentId = StringUtils.substringAfterLast(encryptedConsentId, "/");
		return new AuthUrl(encryptedConsentId, authorizationId);
	}
}

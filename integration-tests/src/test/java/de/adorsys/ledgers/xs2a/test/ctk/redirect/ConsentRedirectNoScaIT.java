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

package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import feign.FeignException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ConsentRedirectNoScaIT extends AbstractConsentRedirect {
	@Override
	protected String getPsuId() {
		return "marion.mueller";
	}
	@Override
	protected String getIban() {
		return "DE69760700240340283600";
	}

	@Test
	public void test_initiate_consent() {

		ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createDedicatedConsent();
		consentHelper.checkConsentStatus(createConsentResp, ConsentStatus.RECEIVED);

		try {
			consentHelper.login(createConsentResp);
			Assert.fail("Expecting a bad request");
		} catch(FeignException f) {
			// TODO: create Ticket why bad request. Middleware return exempted.
			Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), f.status());
		}
	}
}

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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.UpdatePsuAuthenticationResponse;
import feign.FeignException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public class ConsentEmbeddedUnknownUser  extends AbstractConsentEmbedded {
	@Override
	protected String getPsuId() {
		return "user.unknown";
	}
	@Override
	protected String getIban() {
		return "DE80760700240271232400";
	}

	@Test
	public void test_create_payment() {

		ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createDedicatedConsent();

		ConsentsResponse201 consents = createConsentResp.getBody();
		// Login User
		Assert.assertNotNull(consents);
		ConsentStatus consentStatus = consents.getConsentStatus();
		Assert.assertNotNull(consentStatus);
		Assert.assertEquals(ConsentStatus.RECEIVED, consentStatus);

		try {
			ResponseEntity<UpdatePsuAuthenticationResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
		} catch(FeignException f) {
			// TODO:  Middleware return not found. SPI design does not allow
			// pass thru of code. https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/629
			//			Assert.assertEquals(HttpStatus.NOT_FOUND.value(), f.status());
			Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), f.status());
		}

	}
}

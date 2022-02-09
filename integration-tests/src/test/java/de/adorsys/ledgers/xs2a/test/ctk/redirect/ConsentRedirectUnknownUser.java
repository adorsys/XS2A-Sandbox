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

import de.adorsys.ledgers.oba.service.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import feign.FeignException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public class ConsentRedirectUnknownUser extends AbstractConsentRedirect {
    @Override
    protected String getPsuId() {
        return "user.unknown";
    }

    @Override
    protected String getIban() {
        return "DE80760700240271232400";
    }

    @Test
    public void authenticating_a_user_for_consent_with_wrong_user_name_must_return_404() {

        ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createDedicatedConsent();

        ConsentsResponse201 consents = createConsentResp.getBody();
        // Login User
        Assert.assertNotNull(consents);
        ConsentStatus consentStatus = consents.getConsentStatus();
        Assert.assertNotNull(consentStatus);
        Assert.assertEquals(ConsentStatus.RECEIVED, consentStatus);

        try {
            ResponseEntity<ConsentAuthorizeResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
            Assert.fail("Expecting a 404");
            // TODO I have no way to check if the cookie is available. I can see it in the
            // traces. But i would be nice if we could test this condition applies.
        } catch (FeignException f) {
            Assert.assertEquals(404, f.status());
        }
    }
}

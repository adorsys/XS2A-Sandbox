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

import de.adorsys.ledgers.oba.rest.client.ObaAisApiClient;
import de.adorsys.ledgers.xs2a.client.AccountApiClient;
import de.adorsys.ledgers.xs2a.client.ConsentApiClient;
import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public abstract class AbstractConsentRedirect {

    @Autowired
    private ConsentApiClient consentApi;
    @Autowired
    private ObaAisApiClient obaAisApiClient;
    @Autowired
    private AccountApiClient accountApi;

    protected ConsentHelper consentHelper;

    @Before
    public void before() {
        consentHelper = new ConsentHelper(getPsuId(), getIban(), consentApi, obaAisApiClient, accountApi, getPsuPassword());
    }

    protected String getPsuPassword() {
        return "12345";
    }

    protected abstract String getIban();

    protected abstract String getPsuId();
}

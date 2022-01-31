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

import de.adorsys.psd2.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static de.adorsys.ledgers.xs2a.test.ctk.embedded.LinkResolver.getLink;

public class ConsentEmbeddedOneScaIT extends AbstractConsentEmbedded {
    @Override
    protected String getPsuId() {
        return "anton.brueckner";
    }

    @Override
    protected String getIban() {
        return "DE80760700240271232400";
    }

    @Test
    public void test_initiate_dedicated_consent() {

        // ============= INITIATE CONSENT =======================//
        ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createDedicatedConsent();
        ConsentsResponse201 consents = createConsentResp.getBody();
        Assert.assertNotNull(consents);
        Assert.assertEquals(ConsentStatus.RECEIVED, consents.getConsentStatus());

        // ============= IDENTIFY PSU =======================//
        ResponseEntity<UpdatePsuAuthenticationResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
        Assert.assertNotNull(loginResponseWrapper);
        Assert.assertEquals(HttpStatus.OK, loginResponseWrapper.getStatusCode());
        UpdatePsuAuthenticationResponse loginResponse = loginResponseWrapper.getBody();
        Assert.assertEquals(ScaStatus.SCAMETHODSELECTED, loginResponse.getScaStatus());

        String authoriseTransaction = getLink(loginResponse.getLinks(), "authoriseTransaction");
        ResponseEntity<ConsentStatusResponse200> loadonsentStatusResponse = consentHelper.loadConsentStatus(authoriseTransaction);
        ConsentStatusResponse200 consentStatusResponse200 = loadonsentStatusResponse.getBody();
        Assert.assertEquals(ConsentStatus.RECEIVED, consentStatusResponse200.getConsentStatus());

        // ============= AUTHORIZE CONSENT =======================//
        ResponseEntity<UpdatePsuAuthenticationResponse> authCodeResponseWrapper = consentHelper.authCode(loginResponse);
        Assert.assertNotNull(authCodeResponseWrapper);
        Assert.assertEquals(HttpStatus.OK, authCodeResponseWrapper.getStatusCode());
        UpdatePsuAuthenticationResponse authCodeResponse = authCodeResponseWrapper.getBody();
        // TODO: Missing ScaStatus from response
        Assert.assertEquals(ScaStatus.FINALISED, authCodeResponse.getScaStatus());

        String scaStatusUrl = getLink(authCodeResponse.getLinks(), "scaStatus");
        loadonsentStatusResponse = consentHelper.loadConsentStatus(scaStatusUrl);
        Assert.assertEquals(HttpStatus.OK, loadonsentStatusResponse.getStatusCode());
        consentStatusResponse200 = loadonsentStatusResponse.getBody();
        Assert.assertEquals(ConsentStatus.VALID, consentStatusResponse200.getConsentStatus());

        // ============== READ TRANSACTIONS ========================//
        Map<String, Map<String, TransactionList>> loadTransactions = consentHelper.loadTransactions(authCodeResponse, false);
        Assert.assertTrue(loadTransactions.size() > 0);

    }

    @Test
    public void test_initiate_allpsd2_consent() {

        // ============= INITIATE CONSENT =======================//
        ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createAllPSD2Consent();
        ConsentsResponse201 consents = createConsentResp.getBody();
        Assert.assertNotNull(consents);
        Assert.assertEquals(ConsentStatus.RECEIVED, consents.getConsentStatus());

        // ============= IDENTIFY PSU =======================//
        ResponseEntity<UpdatePsuAuthenticationResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
        Assert.assertNotNull(loginResponseWrapper);
        Assert.assertEquals(HttpStatus.OK, loginResponseWrapper.getStatusCode());
        UpdatePsuAuthenticationResponse loginResponse = loginResponseWrapper.getBody();
        Assert.assertEquals(ScaStatus.SCAMETHODSELECTED, loginResponse.getScaStatus());

        String authoriseTransaction = getLink(loginResponse.getLinks(), "authoriseTransaction");
        ResponseEntity<ConsentStatusResponse200> loadonsentStatusResponse = consentHelper.loadConsentStatus(authoriseTransaction);
        ConsentStatusResponse200 consentStatusResponse200 = loadonsentStatusResponse.getBody();
        Assert.assertEquals(ConsentStatus.RECEIVED, consentStatusResponse200.getConsentStatus());

        // ============= AUTHORIZE CONSENT =======================//
        ResponseEntity<UpdatePsuAuthenticationResponse> authCodeResponseWrapper = consentHelper.authCode(loginResponse);
        Assert.assertNotNull(authCodeResponseWrapper);
        Assert.assertEquals(HttpStatus.OK, authCodeResponseWrapper.getStatusCode());
        UpdatePsuAuthenticationResponse authCodeResponse = authCodeResponseWrapper.getBody();
        // TODO: Missing ScaStatus from response
        Assert.assertEquals(ScaStatus.FINALISED, authCodeResponse.getScaStatus());

        String scaStatusUrl = getLink(authCodeResponse.getLinks(), "scaStatus");
        loadonsentStatusResponse = consentHelper.loadConsentStatus(scaStatusUrl);
        Assert.assertEquals(HttpStatus.OK, loadonsentStatusResponse.getStatusCode());
        consentStatusResponse200 = loadonsentStatusResponse.getBody();
        Assert.assertEquals(ConsentStatus.VALID, consentStatusResponse200.getConsentStatus());
    }
}

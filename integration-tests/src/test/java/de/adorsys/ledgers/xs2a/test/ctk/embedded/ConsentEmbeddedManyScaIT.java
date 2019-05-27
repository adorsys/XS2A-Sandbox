package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import de.adorsys.psd2.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static de.adorsys.ledgers.xs2a.test.ctk.embedded.LinkResolver.getLink;

public class ConsentEmbeddedManyScaIT extends AbstractConsentEmbedded {
    @Override
    protected String getPsuId() {
        return "max.musterman";
    }

    @Override
    protected String getIban() {
        return "DE38760700240320465700";
    }

    @Test
    public void test_initiate_consent() {

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
        Assert.assertEquals(ScaStatus.PSUAUTHENTICATED, loginResponse.getScaStatus());

        String selectAuthenticationMethodUrl = getLink(loginResponse.getLinks(), "selectAuthenticationMethod");
        ResponseEntity<ConsentStatusResponse200> loadConsentStatusResponse = consentHelper.loadConsentStatus(selectAuthenticationMethodUrl);
        ConsentStatusResponse200 consentStatusResponse200 = loadConsentStatusResponse.getBody();
        Assert.assertEquals(ConsentStatus.RECEIVED, consentStatusResponse200.getConsentStatus());

        // ============= SELECT SCA =======================//
        ResponseEntity<UpdatePsuAuthenticationResponse> choseScaMethodResponseWrapper = consentHelper.choseScaMethod(loginResponse);
        Assert.assertNotNull(choseScaMethodResponseWrapper);
        Assert.assertEquals(HttpStatus.OK, choseScaMethodResponseWrapper.getStatusCode());
        UpdatePsuAuthenticationResponse choseScaMethodResponse = choseScaMethodResponseWrapper.getBody();
        Assert.assertEquals(ScaStatus.SCAMETHODSELECTED, choseScaMethodResponse.getScaStatus());

        String authoriseTransactionUrl = getLink(choseScaMethodResponse.getLinks(), "authoriseTransaction");
        loadConsentStatusResponse = consentHelper.loadConsentStatus(authoriseTransactionUrl);
        consentStatusResponse200 = loadConsentStatusResponse.getBody();
        Assert.assertEquals(ConsentStatus.RECEIVED, consentStatusResponse200.getConsentStatus());

        // ============= AUTHORIZE CONSENT =======================//
        ResponseEntity<UpdatePsuAuthenticationResponse> authCodeResponseWrapper = consentHelper.authCode(choseScaMethodResponse);
        Assert.assertNotNull(authCodeResponseWrapper);
        Assert.assertEquals(HttpStatus.OK, authCodeResponseWrapper.getStatusCode());
        UpdatePsuAuthenticationResponse authCodeResponse = authCodeResponseWrapper.getBody();
        Assert.assertEquals(ScaStatus.FINALISED, authCodeResponse.getScaStatus());

        String scaStatusUrl = getLink(authCodeResponse.getLinks(), "scaStatus");
        loadConsentStatusResponse = consentHelper.loadConsentStatus(scaStatusUrl);
        Assert.assertEquals(HttpStatus.OK, loadConsentStatusResponse.getStatusCode());
        consentStatusResponse200 = loadConsentStatusResponse.getBody();
        Assert.assertEquals(ConsentStatus.VALID, consentStatusResponse200.getConsentStatus());

        // ============== READ TRANSACTIONS ========================//
        Map<String, Map<String, List<TransactionDetails>>> loadTransactions = consentHelper.loadTransactions(authCodeResponse, false);
        Assert.assertTrue(loadTransactions.size() > 0);
    }
}

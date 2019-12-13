package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PIISConsentCreateResponse;
import de.adorsys.psd2.model.InlineResponse2003;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class PiisOneScaMethodIT extends AbstractPiis {
    @Override
    protected String getPsuId() {
        return "anton.brueckner";
    }

    @Override
    protected String getIban() {
        return "DE80760700240271232400";
    }

    @Test
    public void test_cif() {
        // Login
        ResponseEntity<AuthorizeResponse> login = cifHelper.login();

        // AuthCode
        ResponseEntity<AuthorizeResponse> authCode = cifHelper.checkAccessToken(login);

        ResponseEntity<PIISConsentCreateResponse> createPiisConsent = cifHelper.createPiisConsent(authCode);

        ResponseEntity<InlineResponse2003> confOfFund = cifHelper.confOfFund();
        InlineResponse2003 body = confOfFund.getBody();
        Assert.assertNotNull(body);
        // Intentional not null
        Assert.assertNotNull(body.getFundsAvailable());
    }
}

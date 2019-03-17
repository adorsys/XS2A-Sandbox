package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PIISConsentCreateResponse;
import de.adorsys.ledgers.xs2a.api.client.FundsConfirmationResponse;

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
	public void test_cif() throws MalformedURLException {
		// Login
		ResponseEntity<AuthorizeResponse> login = cifHelper.login();
		
		// AuthCode
		ResponseEntity<AuthorizeResponse> authCode = cifHelper.authCode(login);
		
		ResponseEntity<PIISConsentCreateResponse> createPiisConsent = cifHelper.createPiisConsent(authCode);
		
		// TODO: fix this after https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/648 addressed
		ResponseEntity<FundsConfirmationResponse> confOfFund = cifHelper.confOfFund(authCode);
		FundsConfirmationResponse inlineResponse200 = confOfFund.getBody();
		Assert.assertNotNull(inlineResponse200);
		// Intentional not null
		Assert.assertNotNull(inlineResponse200.isFundsAvailable());
	}
}

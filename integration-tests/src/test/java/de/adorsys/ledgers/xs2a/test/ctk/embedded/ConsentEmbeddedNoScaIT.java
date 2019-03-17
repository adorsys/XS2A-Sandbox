package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.UpdatePsuAuthenticationResponse;
import feign.FeignException;

public class ConsentEmbeddedNoScaIT extends AbstractConsentEmbedded {
	@Override
	protected String getPsuId() {
		return "marion.mueller";
	}
	@Override
	protected String getIban() {
		return "DE69760700240340283600";
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
			ResponseEntity<UpdatePsuAuthenticationResponse> loginResponse = consentHelper.login(createConsentResp);
			Assert.fail("Expecting a bad request");
		} catch(FeignException f) {
			// TODO: create Ticket why bad request. Middleware return exempted.
			Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), f.status());		
		}
	}
}

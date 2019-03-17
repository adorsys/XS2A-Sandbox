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
			Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), f.status());		
		}

	}
}

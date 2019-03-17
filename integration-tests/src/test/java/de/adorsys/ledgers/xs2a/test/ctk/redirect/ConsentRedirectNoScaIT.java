package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import feign.FeignException;

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
			ResponseEntity<ConsentAuthorizeResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
			ConsentAuthorizeResponse body = loginResponseWrapper.getBody();
			Assert.fail("Expecting a bad request");
		} catch(FeignException f) {
			// TODO: create Ticket why bad request. Middleware return exempted.
			Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), f.status());		
		}
	}
}

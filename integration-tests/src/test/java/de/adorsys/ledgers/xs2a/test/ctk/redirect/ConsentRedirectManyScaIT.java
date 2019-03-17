package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.TransactionDetails;

public class ConsentRedirectManyScaIT extends AbstractConsentRedirect {
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
		consentHelper.checkConsentStatus(createConsentResp, ConsentStatus.RECEIVED);
		
		// ============= IDENTIFY PSU =======================//
		ResponseEntity<ConsentAuthorizeResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
		consentHelper.validateResponseStatus(loginResponseWrapper, ScaStatusTO.PSUAUTHENTICATED);
		consentHelper.checkConsentStatus(loginResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.RECEIVED);
		
		// ============= SELECT SCA =======================//
		ResponseEntity<ConsentAuthorizeResponse> choseScaMethodResponseWrapper = consentHelper.choseScaMethod(loginResponseWrapper);
		consentHelper.validateResponseStatus(choseScaMethodResponseWrapper, ScaStatusTO.SCAMETHODSELECTED);
		consentHelper.checkConsentStatus(choseScaMethodResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.RECEIVED);
		
		// ============= AUTHORIZE CONSENT =======================//
		ResponseEntity<ConsentAuthorizeResponse> authCodeResponseWrapper = consentHelper.authCode(choseScaMethodResponseWrapper);
		consentHelper.validateResponseStatus(authCodeResponseWrapper, ScaStatusTO.FINALISED);
		consentHelper.checkConsentStatus(authCodeResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.VALID);

		// ============== READ TRANSACTIONS ========================//
		Map<String, Map<String, List<TransactionDetails>>> loadTransactions = consentHelper.loadTransactions(authCodeResponseWrapper.getBody(), false);
		Assert.assertTrue(loadTransactions.size()>0);
	}

}

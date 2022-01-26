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

import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.oba.service.api.domain.ConsentAuthorizeResponse;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.TransactionList;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ConsentRedirectOneScaIT extends AbstractConsentRedirect {
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
		consentHelper.checkConsentStatus(createConsentResp, ConsentStatus.RECEIVED);

		// ============= IDENTIFY PSU =======================//
		ResponseEntity<ConsentAuthorizeResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
		consentHelper.validateResponseStatus(loginResponseWrapper, ScaStatusTO.PSUIDENTIFIED);
		consentHelper.checkConsentStatus(loginResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.RECEIVED);

		// ============= STATRT SCA =======================//
		ResponseEntity<ConsentAuthorizeResponse> startConsentAuthWrapper = consentHelper.startSCA(loginResponseWrapper, getIban(), true, true, true);
		consentHelper.validateResponseStatus(startConsentAuthWrapper, ScaStatusTO.PSUAUTHENTICATED);
		consentHelper.checkConsentStatus(startConsentAuthWrapper.getBody().getEncryptedConsentId(), ConsentStatus.RECEIVED);

		// ============= SELECT SCA =======================//
		ResponseEntity<ConsentAuthorizeResponse> choseScaMethodResponseWrapper = consentHelper.choseScaMethod(startConsentAuthWrapper);
		consentHelper.validateResponseStatus(choseScaMethodResponseWrapper, ScaStatusTO.SCAMETHODSELECTED);
		consentHelper.checkConsentStatus(choseScaMethodResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.RECEIVED);

		// ============= AUTHORIZE CONSENT =======================//
		ResponseEntity<ConsentAuthorizeResponse> authCodeResponseWrapper = consentHelper.authCode(choseScaMethodResponseWrapper);
		consentHelper.validateResponseStatus(authCodeResponseWrapper, ScaStatusTO.FINALISED);
		consentHelper.checkConsentStatus(authCodeResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.VALID);

		// ============== READ TRANSACTIONS ========================//
		Map<String, Map<String, TransactionList>> loadTransactions = consentHelper.loadTransactions(authCodeResponseWrapper.getBody(), false);
		Assert.assertTrue(loadTransactions.size()>0);
	}

	@Test
	public void test_initiate_allPsd2_consent() {

		// ============= INITIATE CONSENT =======================//
		ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createAllPsd2Consent();
		consentHelper.checkConsentStatus(createConsentResp, ConsentStatus.RECEIVED);

		// ============= IDENTIFY PSU =======================//
		ResponseEntity<ConsentAuthorizeResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
		consentHelper.validateResponseStatus(loginResponseWrapper, ScaStatusTO.PSUIDENTIFIED);
		consentHelper.checkConsentStatus(loginResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.RECEIVED);

		// ============= STATRT SCA =======================//
		ResponseEntity<ConsentAuthorizeResponse> startConsentAuthWrapper = consentHelper.startSCA(loginResponseWrapper, getIban(), true, true, true);
		consentHelper.validateResponseStatus(startConsentAuthWrapper, ScaStatusTO.PSUAUTHENTICATED);
		consentHelper.checkConsentStatus(startConsentAuthWrapper.getBody().getEncryptedConsentId(), ConsentStatus.RECEIVED);

		// ============= SELECT SCA =======================//
		ResponseEntity<ConsentAuthorizeResponse> choseScaMethodResponseWrapper = consentHelper.choseScaMethod(startConsentAuthWrapper);
		consentHelper.validateResponseStatus(choseScaMethodResponseWrapper, ScaStatusTO.SCAMETHODSELECTED);
		consentHelper.checkConsentStatus(choseScaMethodResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.RECEIVED);

		// ============= AUTHORIZE CONSENT =======================//
		ResponseEntity<ConsentAuthorizeResponse> authCodeResponseWrapper = consentHelper.authCode(loginResponseWrapper);
		consentHelper.validateResponseStatus(authCodeResponseWrapper, ScaStatusTO.FINALISED);
		consentHelper.checkConsentStatus(authCodeResponseWrapper.getBody().getEncryptedConsentId(), ConsentStatus.VALID);

	}
}

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
import de.adorsys.psd2.model.ScaStatus;
import de.adorsys.psd2.model.TransactionList;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

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
		consentHelper.validateResponseStatus(loginResponseWrapper, ScaStatusTO.PSUIDENTIFIED);
		String encryptedConsentId = loginResponseWrapper.getBody().getEncryptedConsentId();
		String authorisationId = loginResponseWrapper.getBody().getAuthorisationId();
		consentHelper.checkConsentStatus(encryptedConsentId, ConsentStatus.RECEIVED);
		consentHelper.checkConsentScaStatusFromXS2A(encryptedConsentId, authorisationId, ScaStatus.PSUIDENTIFIED);

		// ============= STATRT SCA =======================//
		ResponseEntity<ConsentAuthorizeResponse> startConsentAuthWrapper = consentHelper.startSCA(loginResponseWrapper, getIban(), true, true, true);
		consentHelper.validateResponseStatus(startConsentAuthWrapper, ScaStatusTO.PSUAUTHENTICATED);
		consentHelper.checkConsentStatus(encryptedConsentId, ConsentStatus.RECEIVED);
		consentHelper.checkConsentScaStatusFromXS2A(encryptedConsentId, authorisationId, ScaStatus.PSUAUTHENTICATED);

		// ============= SELECT SCA =======================//
		ResponseEntity<ConsentAuthorizeResponse> choseScaMethodResponseWrapper = consentHelper.choseScaMethod(loginResponseWrapper);
		consentHelper.validateResponseStatus(choseScaMethodResponseWrapper, ScaStatusTO.SCAMETHODSELECTED);
		consentHelper.checkConsentStatus(encryptedConsentId, ConsentStatus.RECEIVED);
		consentHelper.checkConsentScaStatusFromXS2A(encryptedConsentId, authorisationId, ScaStatus.SCAMETHODSELECTED);

		// ============= AUTHORIZE CONSENT =======================//
		ResponseEntity<ConsentAuthorizeResponse> authCodeResponseWrapper = consentHelper.authCode(choseScaMethodResponseWrapper);
		consentHelper.validateResponseStatus(authCodeResponseWrapper, ScaStatusTO.FINALISED);
		consentHelper.checkConsentStatus(encryptedConsentId, ConsentStatus.VALID);
		consentHelper.checkConsentScaStatusFromXS2A(encryptedConsentId, authorisationId, ScaStatus.FINALISED);

		// ============== READ TRANSACTIONS ========================//
		Map<String, Map<String, TransactionList>> loadTransactions = consentHelper.loadTransactions(authCodeResponseWrapper.getBody(), false);
		Assert.assertTrue(loadTransactions.size()>0);
	}

}

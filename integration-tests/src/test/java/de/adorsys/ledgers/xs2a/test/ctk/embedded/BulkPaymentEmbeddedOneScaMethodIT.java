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

import org.junit.Test;

import de.adorsys.psd2.model.PaymentInitationRequestResponse201;
import de.adorsys.psd2.model.ScaStatus;
import de.adorsys.psd2.model.TransactionStatus;
import de.adorsys.psd2.model.UpdatePsuAuthenticationResponse;

public class BulkPaymentEmbeddedOneScaMethodIT extends AbstractPaymentEmbedded {

    @Test
    public void test_create_payment() {

    	// Initiate Payment
        PaymentInitationRequestResponse201 initiatedPaymentResponse = paymentInitService.initiatePayment();
        String paymentId = initiatedPaymentResponse.getPaymentId();

        // Login User
		UpdatePsuAuthenticationResponse loginResponse = paymentInitService.login(initiatedPaymentResponse);
		paymentInitService.validateResponseStatus(loginResponse, ScaStatus.SCAMETHODSELECTED);
		paymentInitService.checkTxStatus(paymentId, TransactionStatus.ACCP);

		UpdatePsuAuthenticationResponse psuAuthenticationResponse = paymentInitService.authCode();
		paymentInitService.validateResponseStatus(psuAuthenticationResponse, ScaStatus.FINALISED);
		paymentInitService.checkTxStatus(paymentId, TransactionStatus.ACSP);
    }
}

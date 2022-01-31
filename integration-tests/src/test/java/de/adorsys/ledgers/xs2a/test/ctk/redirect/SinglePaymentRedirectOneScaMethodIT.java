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

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.psd2.model.PaymentInitationRequestResponse201;
import de.adorsys.psd2.model.TransactionStatus;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;

public class SinglePaymentRedirectOneScaMethodIT extends AbstractSinglePaymentRedirect {

    @Test
    public void test_create_payment() throws MalformedURLException {
        // Initiate Payment
        PaymentInitationRequestResponse201 initiatedPayment = paymentInitService.initiatePayment();
        String paymentId = initiatedPayment.getPaymentId();

        // Login User
        ResponseEntity<PaymentAuthorizeResponse> loginResponseWrapper = paymentInitService.login(initiatedPayment);
        paymentInitService.validateResponseStatus(loginResponseWrapper.getBody(), ScaStatusTO.PSUIDENTIFIED, TransactionStatusTO.ACCP);
        paymentInitService.checkTxStatus(paymentId, TransactionStatus.ACCP);

        ResponseEntity<PaymentAuthorizeResponse> choseScaMethodResponseWrapper = paymentInitService.choseScaMethod(loginResponseWrapper);
        paymentInitService.validateResponseStatus(choseScaMethodResponseWrapper.getBody(), ScaStatusTO.SCAMETHODSELECTED, TransactionStatusTO.ACCP);
        paymentInitService.checkTxStatus(paymentId, TransactionStatus.ACCP);

        ResponseEntity<PaymentAuthorizeResponse> authCodeResponseWrapper = paymentInitService.authCode(loginResponseWrapper);
        paymentInitService.validateResponseStatus(authCodeResponseWrapper.getBody(), ScaStatusTO.FINALISED, TransactionStatusTO.ACSP);
        paymentInitService.checkTxStatus(paymentId, TransactionStatus.ACSP);
    }
}

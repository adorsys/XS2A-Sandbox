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

package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;

public interface CommonPaymentService {
    PaymentWorkflow selectScaForPayment(String encryptedPaymentId, String authorisationId, String scaMethodId, String psuId, BearerTokenTO tokenTO);

    PaymentWorkflow identifyPayment(String encryptedPaymentId, String authorizationId, BearerTokenTO bearerToken);

    void updateAspspConsentData(PaymentWorkflow paymentWorkflow);

    String resolveRedirectUrl(String encryptedPaymentId, String authorisationId, boolean isOauth2Integrated, String psuId, BearerTokenTO tokenTO, String authConfirmationCode);

    PaymentWorkflow initiatePaymentOpr(PaymentWorkflow paymentWorkflow, String psuId, OpTypeTO opType);

    PaymentWorkflow authorizePaymentOpr(PaymentWorkflow paymentWorkflow, String psuId, String authCode, OpTypeTO opType);
}

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

package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Data
public class PaymentWorkflow {
    private final CmsPaymentResponse paymentResponse;

    private String paymentStatus;
    private String authCodeMessage;
    private PaymentAuthorizeResponse authResponse;
    private final ConsentReference consentReference;
    private GlobalScaResponseTO scaResponse;

    public PaymentWorkflow(@NotNull CmsPaymentResponse paymentResponse, ConsentReference consentReference) {
        if (consentReference == null) {
            throw new IllegalStateException("Do not allow null input.");
        }
        this.paymentResponse = paymentResponse;
        this.consentReference = consentReference;
    }

    public String paymentId() {
        return paymentResponse.getPayment().getPaymentId();
    }

    public String authId() {
        return paymentResponse.getAuthorisationId();
    }

    public String encryptedConsentId() {
        return consentReference.getEncryptedConsentId();
    }

    public BearerTokenTO bearerToken() {
        return scaResponse == null
                   ? null
                   : scaResponse.getBearerToken();
    }

    public PaymentTypeTO paymentType() {
        return PaymentTypeTO.valueOf(paymentResponse.getPayment().getPaymentType().name());
    }

    public boolean singleScaMethod() {
        return scaResponse.getScaMethods() != null && scaResponse.getScaMethods().size() == 1;
    }

    public List<ScaUserDataTO> scaMethods() {
        return scaResponse.getScaMethods();
    }

    public ScaStatusTO scaStatus() {
        return scaResponse.getScaStatus();
    }

    public void processSCAResponse(GlobalScaResponseTO paymentResponse) {
        if (paymentResponse.getBearerToken() == null) {
            paymentResponse.setBearerToken(this.scaResponse.getBearerToken());
        }
        scaResponse = paymentResponse;
        Optional.ofNullable(paymentResponse.getAuthorisationId()).ifPresent(authResponse::setAuthorisationId);
        authResponse.setScaStatus(paymentResponse.getScaStatus());
        authResponse.setScaMethods(paymentResponse.getScaMethods());
        authResponse.setAuthConfirmationCode(paymentResponse.getAuthConfirmationCode());
        authCodeMessage = paymentResponse.getPsuMessage();
    }
}

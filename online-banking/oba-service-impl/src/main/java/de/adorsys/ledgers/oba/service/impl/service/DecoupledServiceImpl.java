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

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.Constants;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.OperationInitiationRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.CmsAspspConsentDataService;
import de.adorsys.ledgers.oba.service.api.service.DecoupledService;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import de.adorsys.psd2.xs2a.core.exception.AuthorisationIsExpiredException;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.AUTH_EXPIRED;
import static de.adorsys.psd2.consent.aspsp.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecoupledServiceImpl implements DecoupledService {
    private final KeycloakTokenService tokenService;
    private final AuthRequestInterceptor authInterceptor;
    private final OperationInitiationRestClient operationInitiationRestClient;
    private final RedirectScaRestClient redirectScaClient;
    private final CmsPsuPisService cmsPsuPisService;
    private final CmsPsuAisClient cmsPsuAisClient;
    private final CmsAspspConsentDataService dataService;
    private final AspspConsentDataClient aspspConsentDataClient;

    @Override
    public boolean executeDecoupledOpr(DecoupledConfRequest request, String token) {
        try {
            BearerTokenTO scaToken = tokenService.exchangeToken(token, request.getAuthorizationTTL(), Constants.SCOPE_SCA);
            authInterceptor.setAccessToken(scaToken.getAccess_token());
            GlobalScaResponseTO response = redirectScaClient.validateScaCode(request.getAuthorizationId(), request.getAuthCode()).getBody();
            Optional.ofNullable(response)
                .map(GlobalScaResponseTO::getBearerToken)
                .map(BearerTokenTO::getAccess_token)
                .ifPresent(authInterceptor::setAccessToken);
            if (EnumSet.of(OpTypeTO.PAYMENT, OpTypeTO.CANCEL_PAYMENT).contains(request.getOpType())) {
                String transactionStatus = executePaymentOperation(request, response);
                updateCmsForPayment(request.getAddressedUser(), response, transactionStatus);
            } else {
                updateCmForConsent(request.getAddressedUser(), response);
            }
        } finally {
            authInterceptor.setAccessToken(null);
        }
        return true;
    }

    private String executePaymentOperation(DecoupledConfRequest request, GlobalScaResponseTO response) {
        GlobalScaResponseTO globalScaResponseTO = operationInitiationRestClient.execution(request.getOpType(), request.getObjId()).getBody();

        if (!request.isConfirmed()) {
            response.setScaStatus(ScaStatusTO.FAILED);
            response.setBearerToken(null);
        }

        return Optional.ofNullable(globalScaResponseTO)
                   .map(GlobalScaResponseTO::getTransactionStatus)
                   .map(Enum::name)
                   .orElse(null);
    }


    private void updateCmForConsent(String psuId, GlobalScaResponseTO scaResponse) {
        // UPDATE CMS
        updateCmsScaConsentStatus(psuId, scaResponse);
        updateConsentStatus(scaResponse);
        updateAspspConsentData(scaResponse);
    }

    private void updateCmsScaConsentStatus(String psuId, GlobalScaResponseTO scaResponse) {
        cmsPsuAisClient.updateAuthorisationStatus(scaResponse.getOperationObjectId(), scaResponse.getScaStatus().name(),
                                                  scaResponse.getAuthorisationId(), psuId, null, null, null, DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, scaResponse.getAuthConfirmationCode()));
    }

    private void updateCmsForPayment(String psuId, GlobalScaResponseTO scaResponse, String transactionStatus) {
        updateCmsScaPaymentAuthStatus(scaResponse, psuId);
        Optional.ofNullable(transactionStatus).ifPresent(s -> updatePaymentStatus(scaResponse.getOperationObjectId(), s));
        updateAspspConsentData(scaResponse);
    }

    private void updateCmsScaPaymentAuthStatus(GlobalScaResponseTO scaResponse, String psuId) {
        try {
            cmsPsuPisService.updateAuthorisationStatus(new PsuIdData(psuId, null, null, null, null),
                                                       scaResponse.getOperationObjectId(), scaResponse.getAuthorisationId(), ScaStatus.valueOf(scaResponse.getScaStatus().name()), DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, scaResponse.getAuthConfirmationCode()));
        } catch (AuthorisationIsExpiredException e) {
            log.error("Authorization for your payment has expired!");
            throw ObaException.builder()
                      .obaErrorCode(AUTH_EXPIRED)
                      .devMessage(e.getMessage())
                      .build();
        }
    }

    private void updatePaymentStatus(String paymentId, String transactionStatus) {
        cmsPsuPisService.updatePaymentStatus(paymentId, TransactionStatus.valueOf(transactionStatus), DEFAULT_SERVICE_INSTANCE_ID);
    }

    private void updateConsentStatus(GlobalScaResponseTO response) {
        if (response.isPartiallyAuthorised()) {
            cmsPsuAisClient.authorisePartiallyConsent(response.getOperationObjectId(), DEFAULT_SERVICE_INSTANCE_ID);
        } else {
            cmsPsuAisClient.confirmConsent(response.getOperationObjectId(), DEFAULT_SERVICE_INSTANCE_ID);
        }
    }

    private void updateAspspConsentData(GlobalScaResponseTO scaResponse) {
        CmsAspspConsentDataBase64 consentData = new CmsAspspConsentDataBase64(scaResponse.getOperationObjectId(), dataService.toBase64String(scaResponse));
        aspspConsentDataClient.updateAspspConsentData(scaResponse.getExternalId(), consentData);
    }
}

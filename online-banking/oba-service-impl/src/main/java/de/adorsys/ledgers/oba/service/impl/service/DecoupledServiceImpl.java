package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.Constants;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
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
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;

import static de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode.CONSENT_DATA_UPDATE_FAILED;
import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.AUTH_EXPIRED;
import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient.DEFAULT_SERVICE_INSTANCE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecoupledServiceImpl implements DecoupledService {
    private final KeycloakTokenService tokenService;
    private final AuthRequestInterceptor authInterceptor;
    private final PaymentRestClient paymentRestClient;
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
            authInterceptor.setAccessToken(response.getBearerToken().getAccess_token());

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

    @Nullable
    private String executePaymentOperation(DecoupledConfRequest request, GlobalScaResponseTO response) {
        SCAPaymentResponseTO executionResponse = request.isConfirmed() && request.getOpType() == OpTypeTO.PAYMENT
                                                     ? paymentRestClient.executePayment(request.getObjId()).getBody()
                                                     : paymentRestClient.executeCancelPayment(request.getObjId()).getBody();

        if (!request.isConfirmed()) {
            response.setScaStatus(ScaStatusTO.FAILED);
            response.setBearerToken(null);
        }

        return Optional.ofNullable(executionResponse)
                   .map(SCAPaymentResponseTO::getTransactionStatus)
                   .map(Enum::name)
                   .orElse(null);
    }


    public void updateCmForConsent(String psuId, GlobalScaResponseTO scaResponse) {
        // UPDATE CMS
        updateCmsScaConsentStatus(psuId, scaResponse);
        updateAspspConsentData(scaResponse);
    }

    private void updateCmsScaConsentStatus(String psuId, GlobalScaResponseTO scaResponse) {
        cmsPsuAisClient.updateAuthorisationStatus(scaResponse.getOperationObjectId(), scaResponse.getScaStatus().name(),
                                                  scaResponse.getAuthorisationId(), psuId, null, null, null, CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, scaResponse.getAuthConfirmationCode()));
    }

    private void updateCmsForPayment(String psuId, GlobalScaResponseTO scaResponse, String transactionStatus) {
        updateCmsScaPaymentStatus(scaResponse, psuId);
        Optional.ofNullable(transactionStatus).ifPresent(s -> updatePaymentStatus(scaResponse.getOperationObjectId(), s));
        updateAspspConsentData(scaResponse);
    }

    private void updateCmsScaPaymentStatus(GlobalScaResponseTO scaResponse, String psuId) {
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

    private void updateAspspConsentData(GlobalScaResponseTO scaResponse) {
        CmsAspspConsentDataBase64 consentData;
        try {
            consentData = new CmsAspspConsentDataBase64(scaResponse.getOperationObjectId(), dataService.toBase64String(scaResponse));
        } catch (IOException e) {
            throw AuthorizationException.builder()
                      .errorCode(CONSENT_DATA_UPDATE_FAILED)
                      .devMessage("Consent data update failed")
                      .build();
        }
        aspspConsentDataClient.updateAspspConsentData(scaResponse.getExternalId(), consentData);
    }
}

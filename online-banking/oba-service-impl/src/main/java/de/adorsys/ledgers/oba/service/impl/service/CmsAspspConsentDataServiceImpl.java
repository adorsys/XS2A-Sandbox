package de.adorsys.ledgers.oba.service.impl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.oba.service.api.domain.LoginFailedCount;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.CmsAspspConsentDataService;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import lombok.RequiredArgsConstructor;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CmsAspspConsentDataServiceImpl implements CmsAspspConsentDataService {
    private final ObjectMapper mapper;
    private final AspspConsentDataClient client;

    @Value("${oba.maxLoginFailedCount:3}")
    private int loginFailedMax;

    private <T> byte[] toBytes(T response) throws IOException {
        return mapper.writeValueAsBytes(response);
    }

    @Override
    public <T> String toBase64String(T response) {
        try {
            return Base64.getEncoder().encodeToString(toBytes(response));
        } catch (IOException e) {
            throw ObaException.builder()
                      .devMessage("Consent data update failed")
                      .obaErrorCode(ObaErrorCode.CONVERSION_EXCEPTION)
                      .build();
        }
    }

    @Override
    public <T extends SCAResponseTO> GlobalScaResponseTO mapToGlobalResponse(T source, OpTypeTO type) {
        GlobalScaResponseTO target = new GlobalScaResponseTO();
        target.setOpType(type);
        target.setAuthorisationId(source.getAuthorisationId());
        target.setScaStatus(source.getScaStatus());
        target.setScaMethods(source.getScaMethods());
        target.setChallengeData(source.getChallengeData());
        target.setPsuMessage(source.getPsuMessage());
        target.setStatusDate(source.getStatusDate());
        target.setExpiresInSeconds(source.getExpiresInSeconds());
        target.setMultilevelScaRequired(source.isMultilevelScaRequired());
        target.setAuthConfirmationCode(source.getAuthConfirmationCode());
        target.setTan(null);//TODO consider tear it from psuMessage
        target.setBearerToken(source.getBearerToken());

        if (OpTypeTO.PAYMENT == type) {
            SCAPaymentResponseTO t = (SCAPaymentResponseTO) source;
            target.setOperationObjectId(t.getPaymentId());
            target.setPartiallyAuthorised(t.getTransactionStatus() == TransactionStatusTO.PATC);
        } else if (OpTypeTO.CONSENT == type) {
            SCAConsentResponseTO t = (SCAConsentResponseTO) source;
            target.setOperationObjectId(t.getConsentId());
            target.setPartiallyAuthorised(t.isPartiallyAuthorised());
        } else {
            target.setOperationObjectId(source.getAuthorisationId());
            target.setPartiallyAuthorised(source.isMultilevelScaRequired());
        }

        return target;
    }

    @Override
    public int updateLoginFailedCount(String encryptedId) {
        int failedCount = extractAspspConsentData(encryptedId).getFailedCount();
        client.updateAspspConsentData(encryptedId, new CmsAspspConsentDataBase64(encryptedId, toBase64String(new LoginFailedCount(failedCount + 1))));
        return loginFailedMax - 1 - failedCount;
    }

    @Override
    public boolean isFailedLogin(String encryptedId) {
        return extractAspspConsentData(encryptedId).getFailedCount() >= loginFailedMax;
    }

    private LoginFailedCount extractAspspConsentData(String encryptedId) {
        CmsAspspConsentDataBase64 body = client.getAspspConsentData(encryptedId).getBody();
        return Optional.ofNullable(body)
                   .map(this::getLoginFailedCount)
                   .orElse(new LoginFailedCount());
    }

    private LoginFailedCount getLoginFailedCount(CmsAspspConsentDataBase64 body) {
        try {
            byte[] decode = Base64.getDecoder().decode(body.getAspspConsentDataBase64());
            return mapper.readValue(decode, LoginFailedCount.class);
        } catch (IOException e) {
            return new LoginFailedCount();
        }
    }
}

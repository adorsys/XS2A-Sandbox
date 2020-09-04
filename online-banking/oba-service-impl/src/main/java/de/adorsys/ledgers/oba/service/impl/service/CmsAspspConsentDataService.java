package de.adorsys.ledgers.oba.service.impl.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CmsAspspConsentDataService {
    private final ObjectMapper mapper;

    public GlobalScaResponseTO fromBytes(byte[] tokenBytes) throws IOException {
        String type = readType(tokenBytes);
        if (SCAConsentResponseTO.class.getSimpleName().equals(type)) {
            return mapToGlobalResponse(mapper.readValue(tokenBytes, SCAConsentResponseTO.class), OpTypeTO.CONSENT);
        } else if (SCALoginResponseTO.class.getSimpleName().equals(type)) {
            return mapToGlobalResponse(mapper.readValue(tokenBytes, SCALoginResponseTO.class), OpTypeTO.LOGIN);
        } else if (SCAPaymentResponseTO.class.getSimpleName().equals(type)) {
            return mapToGlobalResponse(mapper.readValue(tokenBytes, SCAPaymentResponseTO.class), OpTypeTO.PAYMENT);
        } else {
            return mapper.readValue(tokenBytes, GlobalScaResponseTO.class);
        }
    }

    public byte[] toBytes(GlobalScaResponseTO response) throws IOException {
        return mapper.writeValueAsBytes(response);
    }

   /* public <T extends SCAResponseTO> T fromBytes(byte[] tokenBytes, Class<T> klass) throws IOException {
        String type = readType(tokenBytes);
        if (!klass.getSimpleName().equals(type)) {
            return null;
        }
        return mapper.readValue(tokenBytes, klass);
    }*/

    private String readType(byte[] tokenBytes) throws IOException {
        JsonNode jsonNode = mapper.readTree(tokenBytes);
        JsonNode objectType = jsonNode.get("objectType");
        if (objectType == null) {
            return null;
        }
        return objectType.toString();
    }

    public String toBase64String(GlobalScaResponseTO response) throws IOException {
        return Base64.getEncoder().encodeToString(toBytes(response));
    }

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
}

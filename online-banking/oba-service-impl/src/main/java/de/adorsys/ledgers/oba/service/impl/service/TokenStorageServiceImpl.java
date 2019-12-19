package de.adorsys.ledgers.oba.service.impl.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TokenStorageServiceImpl implements TokenStorageService {
    private final ObjectMapper mapper;

    @Override
    public SCAResponseTO fromBytes(byte[] tokenBytes) throws IOException {
        if (tokenBytes == null) {
            return null;
        }
        return read(tokenBytes);
    }

    @Override
    public byte[] toBytes(SCAResponseTO response) throws IOException {
        return mapper.writeValueAsBytes(response);
    }

    @Override
    public <T extends SCAResponseTO> T fromBytes(byte[] tokenBytes, Class<T> klass) throws IOException {
        return read(tokenBytes, klass);
    }

    @Override
    public String toBase64String(SCAResponseTO response) throws IOException {
        return Base64.getEncoder().encodeToString(toBytes(response));
    }

    private SCAResponseTO read(byte[] tokenBytes) throws IOException {
        JsonNode jsonNode = prepareNode(tokenBytes);
        String type = objectType(jsonNode);
        JsonParser jsonParser = mapper.treeAsTokens(jsonNode);
        if (SCAConsentResponseTO.class.getSimpleName().equals(type)) {
            return mapper.readValue(jsonParser, SCAConsentResponseTO.class);
        } else if (SCALoginResponseTO.class.getSimpleName().equals(type)) {
            return mapper.readValue(jsonParser, SCALoginResponseTO.class);
        } else if (SCAPaymentResponseTO.class.getSimpleName().equals(type)) {
            return mapper.readValue(jsonParser, SCAPaymentResponseTO.class);
        } else {
            throw new IOException("Unknown response type " + type);
        }
    }

    private <T extends SCAResponseTO> T read(byte[] tokenBytes, Class<T> klass) throws IOException {
        JsonNode jsonNode = prepareNode(tokenBytes);
        JsonParser jsonParser = mapper.treeAsTokens(jsonNode);
        return mapper.readValue(jsonParser, klass);
    }

    private JsonNode prepareNode(byte[] tokenBytes) throws IOException {
        JsonNode jsonNode = mapper.readTree(tokenBytes);
        // size
        if (jsonNode.size() == 1) { // unwrapped object
            jsonNode = jsonNode.iterator().next();
        }
        return jsonNode;
    }

    private String objectType(JsonNode jsonNode) {
        JsonNode objectType = jsonNode.get("objectType");
        return objectType != null
                   ? objectType.asText()
                   : null;
    }
}

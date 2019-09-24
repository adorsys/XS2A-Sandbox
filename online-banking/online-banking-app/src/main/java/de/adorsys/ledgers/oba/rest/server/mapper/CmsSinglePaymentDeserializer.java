package de.adorsys.ledgers.oba.rest.server.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.adorsys.ledgers.oba.rest.server.domain.ObaCmsSinglePayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;

import java.io.IOException;

public class CmsSinglePaymentDeserializer extends StdDeserializer<CmsSinglePayment> {
    private static final long serialVersionUID = 158931754435907227L;
    private ObjectMapper mapper;

    public CmsSinglePaymentDeserializer(ObjectMapper mapper) {
        super(CmsSinglePayment.class);
        this.mapper = mapper;
    }

    @Override
    public CmsSinglePayment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return mapper.convertValue(node, ObaCmsSinglePayment.class);
    }
}

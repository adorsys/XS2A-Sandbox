package de.adorsys.ledgers.oba.rest.server.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.adorsys.ledgers.oba.service.api.domain.ObaCmsBulkPayment;
import de.adorsys.ledgers.oba.service.api.domain.ObaCmsPeriodicPayment;
import de.adorsys.ledgers.oba.service.api.domain.ObaCmsSinglePayment;
import de.adorsys.psd2.consent.api.pis.CmsPayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;

import java.io.IOException;

public class CmsPaymentDeserializer extends StdDeserializer<CmsPayment> {
    private static final long serialVersionUID = 158931754435907227L;
    private ObjectMapper mapper;

    public CmsPaymentDeserializer(ObjectMapper mapper) {
        super(CmsPayment.class);
        this.mapper = mapper;
    }

    @Override
    public CmsPayment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String paymentTypeValue = node.get("paymentType").asText();
        PaymentType paymentType = PaymentType.getByValue(paymentTypeValue).orElseThrow(
            () -> new IllegalStateException(String.format("Unknown payment type %s", paymentTypeValue)));
        switch (paymentType) {
            case SINGLE:
                return mapper.convertValue(node, ObaCmsSinglePayment.class);
            case BULK:
                return mapper.convertValue(node, ObaCmsBulkPayment.class);
            case PERIODIC:
                return mapper.convertValue(node, ObaCmsPeriodicPayment.class);
            default:
                throw new IllegalStateException(String.format("Unknown payment type %s", paymentType.name()));
        }
    }
}

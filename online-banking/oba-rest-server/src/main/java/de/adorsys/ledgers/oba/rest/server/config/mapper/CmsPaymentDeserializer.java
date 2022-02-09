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

package de.adorsys.ledgers.oba.rest.server.config.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.adorsys.ledgers.oba.service.api.domain.ObaCmsBulkPayment;
import de.adorsys.ledgers.oba.service.api.domain.ObaCmsPeriodicPayment;
import de.adorsys.ledgers.oba.service.api.domain.ObaCmsSinglePayment;
import de.adorsys.psd2.consent.api.pis.BaseCmsPayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;

import java.io.IOException;

public class CmsPaymentDeserializer extends StdDeserializer<BaseCmsPayment> {
    private static final long serialVersionUID = 158931754435907227L;
    private ObjectMapper mapper;

    public CmsPaymentDeserializer(ObjectMapper mapper) {
        super(BaseCmsPayment.class);
        this.mapper = mapper;
    }

    @Override
    public BaseCmsPayment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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

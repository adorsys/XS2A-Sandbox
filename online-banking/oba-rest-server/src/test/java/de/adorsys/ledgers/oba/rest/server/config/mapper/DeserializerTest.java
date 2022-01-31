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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.psd2.consent.api.pis.BaseCmsPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeserializerTest {
    private static final SimpleModule MODULE = new SimpleModule();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MODULE.addDeserializer(BaseCmsPayment.class, new CmsPaymentDeserializer(MAPPER))
            .addDeserializer(CmsSinglePayment.class, new CmsSinglePaymentDeserializer(MAPPER));
        MAPPER.registerModule(MODULE)
            .registerModule(new JavaTimeModule());
    }

    @Test
    void paymentDeserializerTest() throws IOException {
        InputStream stream = new DefaultResourceLoader().getResource("CmsBulkResponse.json").getInputStream();
        CmsPaymentResponse result = MAPPER.readValue(stream, CmsPaymentResponse.class);
        assertNotNull(result);
    }
}

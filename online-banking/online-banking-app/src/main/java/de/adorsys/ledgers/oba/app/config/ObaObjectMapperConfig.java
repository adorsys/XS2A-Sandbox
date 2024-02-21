/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

package de.adorsys.ledgers.oba.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.ledgers.oba.rest.server.config.mapper.CmsPaymentDeserializer;
import de.adorsys.ledgers.oba.rest.server.config.mapper.CmsSinglePaymentDeserializer;
import de.adorsys.psd2.consent.api.pis.BaseCmsPayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class ObaObjectMapperConfig {
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void postConstruct() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BaseCmsPayment.class, new CmsPaymentDeserializer(objectMapper))
            .addDeserializer(CmsSinglePayment.class, new CmsSinglePaymentDeserializer(objectMapper));
        objectMapper.registerModule(module)
            .registerModule(new JavaTimeModule());
    }
}

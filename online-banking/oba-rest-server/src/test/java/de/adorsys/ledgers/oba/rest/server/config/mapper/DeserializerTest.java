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

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

class DeserializerTest {
    private static SimpleModule module = new SimpleModule();
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        module.addDeserializer(BaseCmsPayment.class, new CmsPaymentDeserializer(mapper))
            .addDeserializer(CmsSinglePayment.class, new CmsSinglePaymentDeserializer(mapper));
        mapper.registerModule(module)
            .registerModule(new JavaTimeModule());
    }

    @Test
    void paymentDeserializerTest() throws IOException {
        InputStream stream = new DefaultResourceLoader().getResource("CmsBulkResponse.json").getInputStream();
        mapper.readValue(stream, CmsPaymentResponse.class);
        assert true;
    }
}

package de.adorsys.ledgers.oba.rest.server.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.ledgers.oba.rest.server.config.CmsPaymentDeserializer;
import de.adorsys.ledgers.oba.service.impl.mapper.CmsSinglePaymentDeserializer;
import de.adorsys.psd2.consent.api.pis.CmsPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;

public class DeserializerTest {
    private static SimpleModule module = new SimpleModule();
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        module.addDeserializer(CmsPayment.class, new CmsPaymentDeserializer(mapper))
            .addDeserializer(CmsSinglePayment.class, new CmsSinglePaymentDeserializer(mapper));
        mapper.registerModule(module)
            .registerModule(new JavaTimeModule());
    }

    @Test
    public void paymentDeserializerTest() throws IOException {
        InputStream stream = new DefaultResourceLoader().getResource("CmsBulkResponse.json").getInputStream();
        mapper.readValue(stream, CmsPaymentResponse.class);
        assert true;
    }
}

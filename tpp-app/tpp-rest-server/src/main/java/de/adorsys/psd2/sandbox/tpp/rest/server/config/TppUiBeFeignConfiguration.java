package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.ScaUserDataMixedIn;
import feign.codec.Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TppUiBeFeignConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    public Encoder feignEncoder() {
        objectMapper.addMixIn(ScaUserDataTO.class, ScaUserDataMixedIn.class)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new SpringEncoder(objectFactory);
    }
}

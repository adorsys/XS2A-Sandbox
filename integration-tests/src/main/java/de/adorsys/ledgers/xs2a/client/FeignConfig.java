package de.adorsys.ledgers.xs2a.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public NullHeaderInterceptor nullHeaderInterceptor() {
        return new NullHeaderInterceptor();
    }
}

package de.adorsys.ledgers.xs2a.client;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public NullHeaderInterceptor nullHeaderInterceptor() {
        return new NullHeaderInterceptor();
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(10, TimeUnit.SECONDS,
                                   60, TimeUnit.SECONDS,
                                   false);
    }
}

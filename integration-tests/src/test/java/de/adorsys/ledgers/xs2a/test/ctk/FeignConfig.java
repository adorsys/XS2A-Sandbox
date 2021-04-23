package de.adorsys.ledgers.xs2a.test.ctk;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(10, TimeUnit.SECONDS,
                                   60, TimeUnit.SECONDS,
                                   false);
    }
}

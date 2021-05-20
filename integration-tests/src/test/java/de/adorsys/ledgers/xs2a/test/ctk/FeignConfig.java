package de.adorsys.ledgers.xs2a.test.ctk;

import feign.Client;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Client feignClient(okhttp3.OkHttpClient client) {
        return new feign.okhttp.OkHttpClient(client);
    }

    @Bean
    public okhttp3.OkHttpClient okHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                   .connectTimeout(60, TimeUnit.SECONDS)
                   .readTimeout(60, TimeUnit.SECONDS)
                   .writeTimeout(60, TimeUnit.SECONDS)
                   .followRedirects(false)
                   .followSslRedirects(false)
                   .retryOnConnectionFailure(true)
                   .build();
    }
}

package de.adorsys.ledgers.xs2a.client;

import feign.Client;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public NullHeaderInterceptor nullHeaderInterceptor() {
        return new NullHeaderInterceptor();
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
                   .addInterceptor(new RedirectInterceptor())
                   .build();
    }

    static class RedirectInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            var request = chain.request();
            var response = chain.proceed(request);
            if (HttpStatus.FOUND.value() == response.code()) {
                return response.newBuilder().code(HttpStatus.OK.value()).build();
            }
            return response;
        }
    }
}

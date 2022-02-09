/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.psd2.sandbox.admin.rest.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.psd2.sandbox.admin.rest.server.model.ScaUserDataMixedIn;
import feign.Client;
import feign.codec.Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminFeignConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public Encoder feignEncoder() {
        objectMapper.addMixIn(ScaUserDataTO.class, ScaUserDataMixedIn.class)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        HttpMessageConverter<?> jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new SpringEncoder(objectFactory);
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

/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

package org.adorsys.ledgers.consent.xs2a.rest.config;

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

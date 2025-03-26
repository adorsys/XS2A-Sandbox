/*
 * Copyright 2018-2025 adorsys GmbH & Co KG
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

package de.adorsys.ledgers.oba.rest.server.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.rest.server.auth.JWTAuthenticationFilter;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import de.adorsys.psd2.sandbox.auth.EnableSandboxSecurityFilter;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import de.adorsys.psd2.sandbox.auth.filter.LoginAuthenticationFilter;
import de.adorsys.psd2.sandbox.auth.filter.RefreshTokenFilter;
import de.adorsys.psd2.sandbox.auth.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.annotation.RequestScope;

import java.security.Principal;
import java.util.Optional;

import static de.adorsys.ledgers.oba.rest.server.config.security.PermittedResources.*;

@EnableSandboxSecurityFilter
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class WebSecurityConfig {

    @Configuration
    @RequiredArgsConstructor
    public static class ObaSecurityConfig {
        private final TokenAuthenticationService tokenAuthenticationService;
        private final AuthRequestInterceptor authInterceptor;
        private final ObjectMapper objectMapper;
        private final LoginAuthenticationFilter loginAuthenticationFilter;
        private final RefreshTokenFilter refreshTokenFilter;
        private final TokenAuthenticationFilter tokenAuthenticationFilter;

        @Bean
        protected SecurityFilterChain configureObaSecurity(HttpSecurity http) throws Exception {
            http
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                                   .requestMatchers(APP_INDEX_WHITELIST).permitAll()
                                                   .requestMatchers(APP_SCA_WHITELIST).permitAll()
                                                   .requestMatchers(APP_WHITELIST).permitAll()
                                                   .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                                   .requestMatchers(ACTUATOR_WHITELIST).permitAll()
                                                   .anyRequest().authenticated())

                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .addFilterBefore(new JWTAuthenticationFilter(tokenAuthenticationService, authInterceptor, objectMapper), BasicAuthenticationFilter.class)
                .addFilterBefore(loginAuthenticationFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(refreshTokenFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(tokenAuthenticationFilter, BasicAuthenticationFilter.class);

            return http.build();
        }
    }

    @Bean
    @RequestScope
    public Principal getPrincipal() {
        return authorize().orElse(null);
    }

    @Bean
    @RequestScope
    public MiddlewareAuthentication getMiddlewareAuthentication() {
        return authorize().orElse(null);
    }

    @Bean
    @RequestScope
    public AccessTokenTO getAccessToken() {
        return authorize().map(this::extractToken).orElse(null);
    }

    /**
     * Return Authentication or empty
     *
     * @return
     */
    private static Optional<MiddlewareAuthentication> authorize() {
        return SecurityContextHolder.getContext() == null ||
                   !(SecurityContextHolder.getContext().getAuthentication() instanceof MiddlewareAuthentication)
                   ? Optional.empty()
                   : Optional.of((MiddlewareAuthentication) SecurityContextHolder.getContext().getAuthentication());
    }

    private AccessTokenTO extractToken(MiddlewareAuthentication authentication) {
        return authentication.getBearerToken().getAccessTokenObject();
    }
}

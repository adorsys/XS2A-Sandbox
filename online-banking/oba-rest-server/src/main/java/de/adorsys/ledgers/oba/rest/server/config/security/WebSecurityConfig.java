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

package de.adorsys.ledgers.oba.rest.server.config.security;

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
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.annotation.RequestScope;

import java.security.Principal;
import java.util.Optional;

import static de.adorsys.ledgers.oba.rest.server.config.security.PermittedResources.*;

@SuppressWarnings("PMD.UnusedImports")
@EnableSandboxSecurityFilter
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Order(1)
    @Configuration
    @RequiredArgsConstructor
    public static class ObaSecurityConfig extends WebSecurityConfigurerAdapter {
        private final LoginAuthenticationFilter loginAuthenticationFilter;
        private final RefreshTokenFilter refreshTokenFilter;
        private final TokenAuthenticationFilter tokenAuthenticationFilter;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/**")
                .authorizeRequests()
                .antMatchers(APP_WHITELIST).permitAll()
                .and()
                .authorizeRequests().anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .disable();

            http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.headers().frameOptions().disable();

            http.addFilterBefore(loginAuthenticationFilter, BasicAuthenticationFilter.class);
            http.addFilterBefore(refreshTokenFilter, BasicAuthenticationFilter.class);
            http.addFilterBefore(tokenAuthenticationFilter, BasicAuthenticationFilter.class);
        }
    }

    @Order(2)
    @Configuration
    @RequiredArgsConstructor
    public static class ObaScaSecurityConfig extends WebSecurityConfigurerAdapter {
        private final TokenAuthenticationService tokenAuthenticationService;
        private final AuthRequestInterceptor authInterceptor;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests().antMatchers(APP_INDEX_WHITELIST).permitAll()
                .and()
                .authorizeRequests().antMatchers(APP_SCA_WHITELIST).permitAll()
                .and()
                .authorizeRequests().antMatchers(APP_WHITELIST).permitAll()
                .and()
                .authorizeRequests().antMatchers(SWAGGER_WHITELIST).permitAll()
                .and()
                .authorizeRequests().antMatchers(ACTUATOR_WHITELIST).permitAll()
                .and()
                .cors()
                .and()
                .authorizeRequests().anyRequest().authenticated();

            http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.headers().frameOptions().disable();
            http.addFilterBefore(new JWTAuthenticationFilter(tokenAuthenticationService, authInterceptor), BasicAuthenticationFilter.class);


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

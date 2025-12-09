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

package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import de.adorsys.psd2.sandbox.auth.EnableSandboxSecurityFilter;
import de.adorsys.psd2.sandbox.auth.filter.LoginAuthenticationFilter;
import de.adorsys.psd2.sandbox.auth.filter.RefreshTokenFilter;
import de.adorsys.psd2.sandbox.auth.filter.TokenAuthenticationFilter;
import de.adorsys.psd2.sandbox.tpp.rest.server.auth.DisableEndpointFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static de.adorsys.psd2.sandbox.tpp.rest.server.config.PermittedResources.*;

@EnableSandboxSecurityFilter
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class TppWebSecurityConfig {
    private final LoginAuthenticationFilter loginAuthenticationFilter;
    private final RefreshTokenFilter refreshTokenFilter;
    private final Environment environment;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(a -> a.requestMatchers(INDEX_WHITELIST).permitAll()
                                            .requestMatchers(APP_WHITELIST).permitAll()
                                            .requestMatchers(ACTUATOR_WHITELIST).permitAll()
                                            .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                            // Permit unauthenticated access to VOP endpoints (internal verification service)
                                            .requestMatchers("/v1/**").permitAll()
                                            .anyRequest().authenticated())

            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .addFilterBefore(new DisableEndpointFilter(environment), BasicAuthenticationFilter.class)
            .addFilterBefore(loginAuthenticationFilter, BasicAuthenticationFilter.class)
            .addFilterBefore(refreshTokenFilter, BasicAuthenticationFilter.class)
            .addFilterBefore(tokenAuthenticationFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}

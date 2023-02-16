/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import de.adorsys.psd2.sandbox.auth.EnableSandboxSecurityFilter;
import de.adorsys.psd2.sandbox.tpp.rest.server.auth.DisableEndpointFilter;
import de.adorsys.psd2.sandbox.auth.filter.LoginAuthenticationFilter;
import de.adorsys.psd2.sandbox.auth.filter.RefreshTokenFilter;
import de.adorsys.psd2.sandbox.auth.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static de.adorsys.psd2.sandbox.tpp.rest.server.config.PermittedResources.*;

@SuppressWarnings("PMD.UnusedImports")
@EnableSandboxSecurityFilter
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class TppWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final LoginAuthenticationFilter loginAuthenticationFilter;
    private final RefreshTokenFilter refreshTokenFilter;
    private final Environment environment;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests().antMatchers(INDEX_WHITELIST).permitAll()
            .and()
            .authorizeRequests().antMatchers(APP_WHITELIST).permitAll()
            .and()
            .authorizeRequests().antMatchers(ACTUATOR_WHITELIST).permitAll()
            .and()
            .authorizeRequests().antMatchers(SWAGGER_WHITELIST).permitAll()
            .and()
            .cors()
            .and()
            .authorizeRequests().anyRequest().authenticated();

        http.headers().frameOptions().disable();
        http.httpBasic().disable();
        http.addFilterBefore(new DisableEndpointFilter(environment), BasicAuthenticationFilter.class);
        http.addFilterBefore(loginAuthenticationFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(refreshTokenFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(tokenAuthenticationFilter, BasicAuthenticationFilter.class);
    }
}

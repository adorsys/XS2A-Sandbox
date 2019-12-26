package de.adorsys.ledgers.oba.rest.server.config.security;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.server.auth.JWTAuthenticationFilter;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.rest.server.auth.oba.LoginAuthenticationFilter;
import de.adorsys.ledgers.oba.rest.server.auth.oba.TokenAuthenticationFilter;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
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

import static de.adorsys.ledgers.oba.rest.server.auth.oba.PermittedResources.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Order(1)
    @Configuration
    @RequiredArgsConstructor
    public static class ObaSecurityConfig extends WebSecurityConfigurerAdapter {
        private final UserMgmtRestClient userMgmtRestClient;
        private final AuthRequestInterceptor authInterceptor;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/**")
                .authorizeRequests()
                .antMatchers(APP_WHITELIST).permitAll()
                    .and()
                .authorizeRequests().anyRequest()
                .authenticated();

            http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.headers().frameOptions().disable();

            http.addFilterBefore(new LoginAuthenticationFilter(userMgmtRestClient), BasicAuthenticationFilter.class);
            http.addFilterBefore(new TokenAuthenticationFilter(userMgmtRestClient, authInterceptor), BasicAuthenticationFilter.class);
        }
    }

    @Order(2)
    @Configuration
    @RequiredArgsConstructor
    public static class ObaScaSecurityConfig extends WebSecurityConfigurerAdapter {
        private final TokenAuthenticationService tokenAuthenticationService;

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

            http.addFilterBefore(new JWTAuthenticationFilter(tokenAuthenticationService), BasicAuthenticationFilter.class);
        }
    }

    @Bean
    @RequestScope
    public Principal getPrincipal() {
        return auth().orElse(null);
    }

    @Bean
    @RequestScope
    public ObaMiddlewareAuthentication getMiddlewareAuthentication() {
        return auth().orElse(null);
    }

    @Bean
    @RequestScope
    public AccessTokenTO getAccessToken() {
        return auth().map(this::extractToken).orElse(null);
    }

    /**
     * Return Authentication or empty
     *
     * @return
     */
    private static Optional<ObaMiddlewareAuthentication> auth() {
        return SecurityContextHolder.getContext() == null ||
                   !(SecurityContextHolder.getContext().getAuthentication() instanceof ObaMiddlewareAuthentication)
                   ? Optional.empty()
                   : Optional.of((ObaMiddlewareAuthentication) SecurityContextHolder.getContext().getAuthentication());
    }

    private AccessTokenTO extractToken(ObaMiddlewareAuthentication authentication) {
        return authentication.getBearerToken().getAccessTokenObject();
    }
}

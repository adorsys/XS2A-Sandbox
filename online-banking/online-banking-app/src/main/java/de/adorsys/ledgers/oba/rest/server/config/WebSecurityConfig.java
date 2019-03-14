package de.adorsys.ledgers.oba.rest.server.config;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.oba.rest.server.auth.JWTAuthenticationFilter;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.rest.server.auth.TokenAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.Principal;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String[] SWAGGER_WHITELIST = {"/swagger-resources/**", "/swagger-resources", "/swagger-ui.html**", "/v2/api-docs",
        "/webjars/**", "favicon.ico", "/error"};
    private static final String[] APP_INDEX_WHITELIST = {"/", "/index.css", "/img/*", "/favicon.ico"};
    private static final String[] APP_SCA_WHITELIST = {"/sca/login", "/pis/auth/**", "/pis/*/authorisation/*/login", "/ais/auth/**", "/ais/*/authorisation/*/login"};
    private static final String[] ACTUATOR_WHITELIST = {"/actuator/health"};

    private final TokenAuthenticationService tokenAuthenticationService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/error");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests().antMatchers(APP_INDEX_WHITELIST).permitAll()
            .and()
            .authorizeRequests().antMatchers(APP_SCA_WHITELIST).permitAll()
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

    @Bean
    @RequestScope
    public Principal getPrincipal() {
        return auth().orElse(null);
    }

    @Bean
    @RequestScope
    public MiddlewareAuthentication getMiddlewareAuthentication() {
        return auth().orElse(null);
    }

    @Bean
    @RequestScope
    public AccessTokenTO getAccessToken() {
        return auth().map(this::extractToken).orElse(null);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    /**
     * Return Authentication or empty
     *
     * @return
     */
    private static Optional<MiddlewareAuthentication> auth() {
        return SecurityContextHolder.getContext() == null ||
                   !(SecurityContextHolder.getContext().getAuthentication() instanceof MiddlewareAuthentication)
                   ? Optional.empty()
                   : Optional.of((MiddlewareAuthentication) SecurityContextHolder.getContext().getAuthentication());
    }

    private AccessTokenTO extractToken(MiddlewareAuthentication authentication) {
        return authentication.getBearerToken().getAccessTokenObject();
    }
}

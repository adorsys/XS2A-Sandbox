package de.adorsys.psd2.sandbox.auth;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.psd2.sandbox.auth.filter.LoginAuthenticationFilter;
import de.adorsys.psd2.sandbox.auth.filter.RefreshTokenFilter;
import de.adorsys.psd2.sandbox.auth.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SandboxSecurityFilterConfig {
    private final KeycloakTokenService tokenService;
    private final LoginAuthorization loginAuthorization;
    private final AuthRequestInterceptor authInterceptor;

    @Bean
    public LoginAuthenticationFilter loginAuthenticationFilter() {
        return new LoginAuthenticationFilter(tokenService, loginAuthorization);
    }

    @Bean
    public RefreshTokenFilter refreshTokenFilter() {
        return new RefreshTokenFilter(tokenService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(authInterceptor, tokenService);
    }
}

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticationServiceImplTest {

    private static final String TOKEN = "token";
    @InjectMocks
    private TokenAuthenticationServiceImpl service;

    @Mock
    private UserMgmtRestClient ledgersUserMgmt;
    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private KeycloakTokenService tokenService;

    @Test
    void getAuthentication() {
        // Given
        when(tokenService.validate(anyString())).thenReturn(getBearer());

        // When
        UserAuthentication result = service.getAuthentication(TOKEN);

        // Then
        assertThat(result).isEqualTo(new UserAuthentication(getBearer()));
    }

    @Test
    void getAuthentication_null_bearer() {
        // Given
        when(tokenService.validate(anyString())).thenReturn(null);

        // When
        UserAuthentication result = service.getAuthentication(TOKEN);

        // Then
        assertNull(result);
    }

    @Test
    void getAuthentication_null_token() {
        UserAuthentication result = service.getAuthentication(null);
        assertThat(result).isEqualTo(null);
    }

    private BearerTokenTO getBearer() {
        return new BearerTokenTO(TOKEN, "some type", 600, null, new AccessTokenTO(), new HashSet<>());
    } //TODO FIX ME!!!
}

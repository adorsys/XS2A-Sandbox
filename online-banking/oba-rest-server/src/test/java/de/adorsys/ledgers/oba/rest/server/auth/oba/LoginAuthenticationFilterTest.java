package de.adorsys.ledgers.oba.rest.server.auth.oba;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginAuthenticationFilterTest {

    @InjectMocks
    private LoginAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request = new MockHttpServletRequest();
    @Mock
    private HttpServletResponse response = new MockHttpServletResponse();
    @Mock
    private FilterChain chain;

    @Mock
    private KeycloakTokenService tokenService;

    @Test
    void doFilter() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getHeader("login")).thenReturn("anton.brueckner");
        when(request.getHeader("pin")).thenReturn("12345");
        when(tokenService.login(anyString(), anyString())).thenReturn(getBearer());
        when(tokenService.validate(any())).thenReturn(getBearer());

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(tokenService, times(1)).login(anyString(), anyString());
    }

    private BearerTokenTO getBearer() {
        return new BearerTokenTO(null, null, 600, null, new AccessTokenTO(), new HashSet<>());
    }

    @Test
    void doFilter_null_bearer() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(tokenService, times(0)).validate(anyString());
        verify(chain, times(1)).doFilter(any(), any());
    }
}

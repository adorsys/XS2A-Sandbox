package de.adorsys.ledgers.oba.rest.server.auth;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTAuthenticationFilterTest {
    @InjectMocks
    private JWTAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request = new MockHttpServletRequest();
    @Mock
    private HttpServletResponse response = new MockHttpServletResponse();
    @Mock
    private FilterChain chain;

    @Mock
    private TokenAuthenticationService tokenAuthenticationService;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        // Given
        SecurityContextHolder.clearContext();
        when(tokenAuthenticationService.getAuthentication(any())).thenReturn(new UserAuthentication(getBearer()));

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(tokenAuthenticationService, times(1)).getAuthentication(any());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    void shouldNotFilter() {
        // Given
        when(request.getServletPath()).thenReturn("/someUrl/login");

        // When
        boolean result = filter.shouldNotFilter(request);

        // Then
        assertTrue(result);
    }

    private BearerTokenTO getBearer() {
        AccessTokenTO token = new AccessTokenTO();
        token.setLogin("anton.brueckner");
        return new BearerTokenTO(null, null, 600, null, token,new HashSet<>());
    }
}

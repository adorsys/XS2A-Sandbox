package de.adorsys.ledgers.oba.rest.server.auth;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JWTAuthenticationFilterTest {
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
    public void doFilterInternal() throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        when(tokenAuthenticationService.getAuthentication(any())).thenReturn(new UserAuthentication(getBearer()));
        filter.doFilterInternal(request, response, chain);

        verify(tokenAuthenticationService, times(1)).getAuthentication(any());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    public void shouldNotFilter() {
        when(request.getServletPath()).thenReturn("/someUrl/login");
        boolean result = filter.shouldNotFilter(request);
        assertThat(result).isTrue();
    }

    private BearerTokenTO getBearer() {
        AccessTokenTO token = new AccessTokenTO();
        token.setLogin("anton.brueckner");
        return new BearerTokenTO(null, null, 600, null, token);
    }
}

package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticationFilterTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    @InjectMocks
    private TokenAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request = new MockHttpServletRequest();
    @Mock
    private HttpServletResponse response = new MockHttpServletResponse();
    @Mock
    private FilterChain chain;

    @Mock
    private KeycloakTokenService tokenService;
    @Mock
    private AuthRequestInterceptor authInterceptor;

    @Test
    void doFilter() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn("Bearer bearerToken");
        when(tokenService.validate(anyString())).thenReturn(getBearer());

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(tokenService, times(1)).validate(anyString());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    void doFilter_null_bearer() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(tokenService, times(0)).validate(anyString());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    void doFilter_error() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn("Bearer bearerToken");
        when(response.getOutputStream()).thenReturn(new MockHttpServletResponse().getOutputStream());
        when(tokenService.validate(anyString())).thenThrow(FeignException.errorStatus("method", getResponse()));
        response.getOutputStream().println(123);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(tokenService, times(1)).validate(anyString());
        verify(chain, times(0)).doFilter(any(), any());
    }

    private Response getResponse() throws JsonProcessingException {
        return Response.builder()
                   .request(Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null))
                   .reason("Msg")
                   .headers(new HashMap<>())
                   .status(401)
                   .body(mapper.writeValueAsBytes(Map.of("devMessage", "Msg")))
                   .build();
    }

    private BearerTokenTO getBearer() {
        AccessTokenTO token = new AccessTokenTO();
        token.setLogin("anton.brueckner");
        return new BearerTokenTO(null, null, 600, null, token, new HashSet<>());
    }
}

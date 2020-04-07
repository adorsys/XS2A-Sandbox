package de.adorsys.ledgers.oba.rest.server.auth.oba;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenAuthenticationFilterTest {
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
    private UserMgmtRestClient ledgersUserMgmt;
    @Mock
    private AuthRequestInterceptor authInterceptor;

    @Test
    public void doFilter() throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn("Bearer bearerToken");
        when(ledgersUserMgmt.validate(anyString())).thenReturn(ResponseEntity.ok(getBearer()));
        filter.doFilter(request, response, chain);

        verify(ledgersUserMgmt, times(1)).validate(anyString());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_null_bearer() throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilter(request, response, chain);

        verify(ledgersUserMgmt, times(0)).validate(anyString());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_error() throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn("Bearer bearerToken");
        when(response.getOutputStream()).thenReturn(new ByteArrayServletOutputStream());
        when(ledgersUserMgmt.validate(anyString())).thenThrow(FeignException.errorStatus("method", getResponse()));
        response.getOutputStream().println(123);
        filter.doFilter(request, response, chain);


        verify(ledgersUserMgmt, times(1)).validate(anyString());
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
        return new BearerTokenTO(null, null, 600, null, token);
    }
}

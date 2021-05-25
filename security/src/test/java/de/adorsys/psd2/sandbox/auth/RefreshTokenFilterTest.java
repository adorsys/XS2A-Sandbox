package de.adorsys.psd2.sandbox.auth;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.psd2.sandbox.auth.filter.RefreshTokenFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenFilterTest {
    public static final String TOKEN_ID = "token_id";
    @Spy
    @InjectMocks
    RefreshTokenFilter filter;
    @Mock
    private HttpServletRequest request = Mockito.mock(MockHttpServletRequest.class);
    @Mock
    private HttpServletResponse response = new MockHttpServletResponse();

    @Mock
    private FilterChain chain;

    @Mock
    private KeycloakTokenService tokenService;


    @Test
    public void doFilterInternal() throws Exception {
        // Given
        SecurityContextHolder.clearContext();
        BearerTokenTO bearer = getBearer();
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(SecurityConstant.BEARER_TOKEN_PREFIX + bearer.getAccess_token());
        doReturn(120L).when(filter).expiredTimeInSec(anyString());
        doReturn(TOKEN_ID).when(filter).jwtId(anyString());
        doReturn(bearer.getRefresh_token()).when(filter).getCookieValue(request, SecurityConstant.REFRESH_TOKEN_COOKIE_PREFIX + TOKEN_ID);
        doReturn(true, false).when(filter).isExpiredToken(anyString());
        when(tokenService.refreshToken(anyString())).thenReturn(bearer);
        filter.doFilterInternal(request, response, chain);
        verify(tokenService, times(1)).refreshToken(anyString());

    }


    private BearerTokenTO getBearer() {
        AccessTokenTO token = new AccessTokenTO();
        token.setRole(UserRoleTO.CUSTOMER);
        return new BearerTokenTO("access_token", null, 600, "refresh_token", token, new HashSet<>());
    }
}


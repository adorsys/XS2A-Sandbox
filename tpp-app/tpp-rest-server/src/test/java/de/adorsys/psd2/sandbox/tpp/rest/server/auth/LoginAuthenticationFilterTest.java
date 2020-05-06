package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserCredentialsTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import feign.FeignException;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginAuthenticationFilterTest {
    private static final String LOGIN = "anton.brueckner";
    private static final String PIN = "12345";

    @InjectMocks
    private LoginAuthenticationFilter filter;

    @Mock
    private final HttpServletRequest request = new MockHttpServletRequest();
    @Mock
    private final HttpServletResponse response = new MockHttpServletResponse();
    @Mock
    private FilterChain chain;

    @Mock
    private UserMgmtStaffRestClient userMgmtStaffRestClient;

    @Test
    void doFilter() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getHeader("login")).thenReturn(LOGIN);
        when(request.getHeader("pin")).thenReturn(PIN);
        when(userMgmtStaffRestClient.login(any())).thenReturn(ResponseEntity.ok(getScaLoginResponse()));

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(userMgmtStaffRestClient, times(1)).login(any());
    }

    @Test
    void doFilter_login_as_tpp() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getHeader("login")).thenReturn(LOGIN);
        when(request.getHeader("pin")).thenReturn(PIN);
        UserCredentialsTO adminCred = new UserCredentialsTO(LOGIN, PIN, UserRoleTO.SYSTEM);
        UserCredentialsTO tppCred = new UserCredentialsTO(LOGIN, PIN, UserRoleTO.STAFF);
        when(userMgmtStaffRestClient.login(adminCred)).thenThrow(FeignException.class);
        when(userMgmtStaffRestClient.login(tppCred)).thenReturn(ResponseEntity.ok(getScaLoginResponse()));
        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(userMgmtStaffRestClient, times(2)).login(any());
    }

    private SCALoginResponseTO getScaLoginResponse() {
        SCALoginResponseTO resp = new SCALoginResponseTO();
        AccessTokenTO token = new AccessTokenTO();
        token.setLogin(LOGIN);
        resp.setBearerToken(new BearerTokenTO(null, null, 600, null, token));
        return resp;
    }
}

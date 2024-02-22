/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

package de.adorsys.psd2.sandbox.auth;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.psd2.sandbox.auth.filter.LoginAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginAuthenticationFilterTest {

    @Spy
    @InjectMocks
    private LoginAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request = new MockHttpServletRequest();
    @Mock
    private HttpServletResponse response = new MockHttpServletResponse();
    @Mock
    private FilterChain chain;

    @Mock
    private LoginAuthorization loginAuthorization;

    @Mock
    private KeycloakTokenService tokenService;

    @Test
    void doFilter() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getHeader("login")).thenReturn("anton.brueckner");
        when(request.getHeader("pin")).thenReturn("12345");
        when(loginAuthorization.canLogin(any())).thenReturn(true);
        when(tokenService.login(any(), any())).thenReturn(getBearerToken(UserRoleTO.SYSTEM));
        when(tokenService.validate(any())).thenReturn(getBearerToken(UserRoleTO.SYSTEM));
        doReturn(120L).when(filter).expiredTimeInSec(null);
        doReturn("").when(filter).jwtId(null);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(tokenService, times(1)).login(any(), any());
        verify(filter, times(1)).addRefreshTokenCookie(response, "", null, false);

    }

    private BearerTokenTO getBearerToken(UserRoleTO role) {
        AccessTokenTO token = new AccessTokenTO();
        token.setRole(role);
        return new BearerTokenTO(null, null, 600, null, token, new HashSet<>());
    }
}

/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.server.auth;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
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

    @Mock
    private AuthRequestInterceptor authInterceptor;

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
        return new BearerTokenTO(null, null, 600, null, token, new HashSet<>());
    }
}

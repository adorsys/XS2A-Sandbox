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

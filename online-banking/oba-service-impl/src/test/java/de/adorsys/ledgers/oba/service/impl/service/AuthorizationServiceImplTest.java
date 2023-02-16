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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {
    private static final String PIN = "12345";
    private static final String URI = "www.google.com";
    @InjectMocks
    private AuthorizationServiceImpl service;


    @Test
    void resolveAuthConfirmationCodeRedirectUri_no_params() {
        // When
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI, PIN);

        // Then
        assertThat(result).isEqualTo(URI + "?" + "authConfirmationCode=" + PIN);
    }

    @Test
    void resolveAuthConfirmationCodeRedirectUri_with_param() {
        // When
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI + "?aaa=123", PIN);

        // Then
        assertThat(result).isEqualTo(URI + "?aaa=123" + "&" + "authConfirmationCode=" + PIN);
    }

    @Test
    void resolveAuthConfirmationCodeRedirectUri_code_null_or_empty() {
        // When
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI, null);

        // Then
        assertThat(result).isEqualTo(URI);
    }
}

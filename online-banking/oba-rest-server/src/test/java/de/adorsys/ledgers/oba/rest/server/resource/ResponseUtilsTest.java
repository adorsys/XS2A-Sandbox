/*
 * Copyright 2018-2025 adorsys GmbH & Co KG
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

package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.oba.rest.server.config.cors.CookieConfigProperties;
import de.adorsys.ledgers.oba.service.api.domain.OnlineBankingResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResponseUtilsTest {

    @InjectMocks
    private ResponseUtils responseUtils;
    @Mock
    private CookieConfigProperties cookieConfigProperties;


    @Test
    void redirect() {
        // Given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        // When
        ResponseEntity<OnlineBankingResponse> responseResponseEntity = responseUtils.redirect("locationURI", response);

        // Then
        assertTrue(responseResponseEntity.getStatusCode().is3xxRedirection());
        assertSame(HttpStatus.FOUND, responseResponseEntity.getStatusCode());
    }

    @Test
    void redirect_relative_url() {
        // Given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // When
        ResponseEntity<OnlineBankingResponse> responseResponseEntity = responseUtils.redirect("www.google.com", response);

        // Then
        assertTrue(responseResponseEntity.getStatusCode().is3xxRedirection());
        assertSame(HttpStatus.FOUND, responseResponseEntity.getStatusCode());
        assertEquals("http://www.google.com", responseResponseEntity.getHeaders().get("Location").get(0));
    }

    @Test
    void redirect_absolute_url() {
        // Given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // When
        ResponseEntity<OnlineBankingResponse> responseResponseEntity = responseUtils.redirect("http://www.google.com", response);

        // Then
        assertTrue(responseResponseEntity.getStatusCode().is3xxRedirection());
        assertSame(HttpStatus.FOUND, responseResponseEntity.getStatusCode());
        assertEquals("http://www.google.com", responseResponseEntity.getHeaders().get("Location").get(0));
    }

}

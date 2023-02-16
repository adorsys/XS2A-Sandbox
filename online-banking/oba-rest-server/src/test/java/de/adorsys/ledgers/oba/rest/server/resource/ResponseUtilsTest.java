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

package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.oba.rest.server.config.cors.CookieConfigProperties;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.OnlineBankingResponse;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponseUtilsTest {
    private static final String LOGIN = "anton.brueckner";
    private static final String ENCRYPTED_ID = "ENC_123";
    private static final String AUTH_ID = "AUTH_1";

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

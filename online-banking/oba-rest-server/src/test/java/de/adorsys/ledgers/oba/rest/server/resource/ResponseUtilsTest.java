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

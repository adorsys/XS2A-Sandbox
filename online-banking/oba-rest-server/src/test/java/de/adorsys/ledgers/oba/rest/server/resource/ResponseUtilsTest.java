package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.oba.rest.server.config.cors.CookieConfigProperties;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.OnlineBankingResponse;
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
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
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
    void setCookies() {
        // Given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getMaxAge()).thenReturn(300);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        // Then
        assertThatCode(() -> responseUtils.setCookies(response, getConsentReference(null), "accessTokenString", getAccessTokenTO())).doesNotThrowAnyException();
    }

    @Test
    void setCookies_accessTokenNull() {
        // Given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getMaxAge()).thenReturn(300);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        // Then
        assertThatCode(() -> responseUtils.setCookies(response, getConsentReference("someCookie"), null, getAccessTokenTO())).doesNotThrowAnyException();
    }

    @Test
    void removeCookies() {
        // Given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        // Then
        assertThatCode(() -> responseUtils.removeCookies(response)).doesNotThrowAnyException();
    }

    @Test
    void redirect() {
        // Given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

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
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

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
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        // When
        ResponseEntity<OnlineBankingResponse> responseResponseEntity = responseUtils.redirect("http://www.google.com", response);

        // Then
        assertTrue(responseResponseEntity.getStatusCode().is3xxRedirection());
        assertSame(HttpStatus.FOUND, responseResponseEntity.getStatusCode());
        assertEquals("http://www.google.com", responseResponseEntity.getHeaders().get("Location").get(0));
    }

    @Test
    void consentCookie() {
        // When
        String cookie = responseUtils.consentCookie("CONSENT=mklrfkwrj3i4jrhugui3i4htvou34d");

        // Then
        assertFalse(StringUtils.isEmpty(cookie));
        assertFalse(StringUtils.isBlank(cookie));
    }

    @Test
    void consentCookie_cookieNull() {
        // When
        String cookie = responseUtils.consentCookie(null);

        // Then
        assertTrue(StringUtils.isEmpty(cookie));
    }

    private ConsentReference getConsentReference(String cookie) {
        ConsentReference cr = new ConsentReference();
        cr.setConsentType(ConsentType.AIS);
        cr.setRedirectId(AUTH_ID);
        cr.setEncryptedConsentId(ENCRYPTED_ID);
        cr.setAuthorizationId(AUTH_ID);
        cr.setCookieString(cookie);
        return cr;
    }

    private AccessTokenTO getAccessTokenTO() {
        AccessTokenTO tokenTO = new AccessTokenTO();
        tokenTO.setLogin(LOGIN);
        tokenTO.setExp(new Date());
        return tokenTO;
    }
}

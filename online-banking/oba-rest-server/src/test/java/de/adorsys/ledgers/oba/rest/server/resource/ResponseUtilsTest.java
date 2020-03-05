package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.oba.rest.server.config.cors.CookieConfigProperties;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.OnlineBankingResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResponseUtilsTest {
    private static final String LOGIN = "anton.brueckner";
    private static final String ENCRYPTED_ID = "ENC_123";
    private static final String AUTH_ID = "AUTH_1";

    @InjectMocks
    private ResponseUtils responseUtils;
    @Mock
    private CookieConfigProperties cookieConfigProperties;

    @Test
    public void setCookies() {
        //given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getMaxAge()).thenReturn(300);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        //then
        assertThatCode(() -> responseUtils.setCookies(response, getConsentReference(null), "accessTokenString", getAccessTokenTO())).doesNotThrowAnyException();
    }

    @Test
    public void setCookies_accessTokenNull() {
        //given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getMaxAge()).thenReturn(300);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        //then
        assertThatCode(() -> responseUtils.setCookies(response, getConsentReference("someCookie"), null, getAccessTokenTO())).doesNotThrowAnyException();
    }

    @Test
    public void removeCookies() {
        //given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        //then
        assertThatCode(() -> responseUtils.removeCookies(response)).doesNotThrowAnyException();
    }

    @Test
    public void unknownCredentials() {
        //given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        //when
        ResponseEntity<OnlineBankingResponse> responseResponseEntity = responseUtils.unknownCredentials(new AuthorizeResponse(), response);

        //then
        assertTrue(responseResponseEntity.getStatusCode().is4xxClientError());
        assertSame(HttpStatus.FORBIDDEN, responseResponseEntity.getStatusCode());
        assertFalse(Objects.requireNonNull(responseResponseEntity.getBody()).getPsuMessages().isEmpty());
    }

    @Test
    public void couldNotProcessRequest() {
        //given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        //when
        ResponseEntity<OnlineBankingResponse> responseResponseEntity = responseUtils.couldNotProcessRequest(new AuthorizeResponse(), "couldNotProcessRequest", HttpStatus.BAD_REQUEST, response);

        //then
        assertTrue(responseResponseEntity.getStatusCode().is4xxClientError());
        assertSame(HttpStatus.BAD_REQUEST, responseResponseEntity.getStatusCode());
        assertFalse(Objects.requireNonNull(responseResponseEntity.getBody()).getPsuMessages().isEmpty());
    }

    @Test
    public void redirect() {
        //given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(cookieConfigProperties.getPath()).thenReturn("somePath");

        //when
        ResponseEntity<OnlineBankingResponse> responseResponseEntity = responseUtils.redirect("locationURI", response);

        //then
        assertTrue(responseResponseEntity.getStatusCode().is3xxRedirection());
        assertSame(HttpStatus.FOUND, responseResponseEntity.getStatusCode());
    }

    @Test
    public void consentCookie() {
        //when
        String cookie = responseUtils.consentCookie("CONSENT=mklrfkwrj3i4jrhugui3i4htvou34d");

        //then
        assertFalse(StringUtils.isEmpty(cookie));
        assertFalse(StringUtils.isBlank(cookie));
    }

    @Test
    public void consentCookie_cookieNull() {
        //when
        String cookie = responseUtils.consentCookie(null);

        //then
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

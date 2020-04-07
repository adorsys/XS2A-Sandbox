package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceImplTest {

    private static final String LOGIN = "anton.brueckner";
    private static final String PIN = "12345";
    private static final String URI = "www.google.com";
    @InjectMocks
    private AuthorizationServiceImpl service;

    @Mock
    private UserMgmtRestClient userMgmtRestClient;

    @Test
    public void login() {
        when(userMgmtRestClient.authorise(any(), any(), eq(UserRoleTO.CUSTOMER))).thenReturn(getLoginResponse());
        SCALoginResponseTO result = service.login(LOGIN, PIN);
        assertThat(result).isEqualTo(getLoginResponse().getBody());
    }

    @Test(expected = AuthorizationException.class)
    public void login_fail() {
        when(userMgmtRestClient.authorise(any(), any(), eq(UserRoleTO.CUSTOMER))).thenThrow(getFeignExceptionWithDevMsg());
        SCALoginResponseTO result = service.login(LOGIN, PIN);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getLoginResponse());
    }

    private FeignException getFeignExceptionWithDevMsg() {
        return FeignException.errorStatus("zzz", Response.builder()
                                                     .body(new byte[]{})
                                                     .status(400)
                                                     .headers(new HashMap<>())
                                                     .reason("")
                                                     .request(Request.create(Request.HttpMethod.POST, "null", new HashMap<>(), null))
                                                     .build());
    }

    private ResponseEntity<SCALoginResponseTO> getLoginResponse() {
        return ResponseEntity.ok(new SCALoginResponseTO());
    }

    @Test
    public void resolveAuthConfirmationCodeRedirectUri_no_params() {
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI, PIN);
        assertThat(result).isEqualTo(URI + "?" + "authConfirmationCode=" + PIN);
    }

    @Test
    public void resolveAuthConfirmationCodeRedirectUri_with_param() {
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI+"?aaa=123", PIN);
        assertThat(result).isEqualTo(URI + "?aaa=123"+"&" + "authConfirmationCode=" + PIN);
    }

    @Test
    public void resolveAuthConfirmationCodeRedirectUri_code_null_or_empty() {
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI, null);
        assertThat(result).isEqualTo(URI);
    }
}

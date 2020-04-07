package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenAuthenticationServiceImplTest {

    private static final String TOKEN = "token";
    @InjectMocks
    private TokenAuthenticationServiceImpl service;

    @Mock
    private UserMgmtRestClient ledgersUserMgmt;
    @Mock
    private AuthRequestInterceptor authInterceptor;

    @Test
    public void getAuthentication() {
        when(ledgersUserMgmt.validate(anyString())).thenReturn(ResponseEntity.ok(getBearer()));
        UserAuthentication result = service.getAuthentication(TOKEN);
        assertThat(result).isEqualTo(new UserAuthentication(getBearer()));
    }

    @Test
    public void getAuthentication_null_bearer() {
        when(ledgersUserMgmt.validate(anyString())).thenReturn(ResponseEntity.ok(null));
        UserAuthentication result = service.getAuthentication(TOKEN);
        assertThat(result).isEqualTo(null);
    }

    @Test
    public void getAuthentication_null_token() {
        UserAuthentication result = service.getAuthentication(null);
        assertThat(result).isEqualTo(null);
    }

    private BearerTokenTO getBearer() {
        return new BearerTokenTO(TOKEN, "some type", 600, null, new AccessTokenTO());
    }
}

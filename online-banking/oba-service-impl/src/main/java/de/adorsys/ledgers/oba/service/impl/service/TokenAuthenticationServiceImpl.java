package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

    private final UserMgmtRestClient ledgersUserMgmt;
    private final AuthRequestInterceptor authInterceptor;

    @Override
    public UserAuthentication getAuthentication(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        BearerTokenTO bearerToken = null;
        try {
            authInterceptor.setAccessToken(accessToken);
            ResponseEntity<BearerTokenTO> responseEntity = ledgersUserMgmt.validate(accessToken);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                bearerToken = responseEntity.getBody();
            }
        } finally {
            authInterceptor.setAccessToken(null);
        }

        if (bearerToken == null) {
            debug("Token is not valid.");
            return null;
        }
        return new UserAuthentication(bearerToken);
    }

    private void debug(String s) {
        if (log.isDebugEnabled()) {
            log.debug(s);
        }
    }
}

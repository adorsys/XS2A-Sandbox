package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

    private final UserMgmtRestClient ledgersUserMgmt;
    private final AuthRequestInterceptor authInterceptor;
    private final KeycloakTokenService tokenService;

    @Override
    public UserAuthentication getAuthentication(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        BearerTokenTO bearerToken = tokenService.validate(accessToken);

        if (bearerToken == null) {
            debug();
            return null;
        }
        return new UserAuthentication(bearerToken);
    }

    private void debug() {
        if (log.isDebugEnabled()) {
            log.debug("Token is not valid.");
        }
    }

    @Override
    public GlobalScaResponseTO login(String login, String pin, String authorizationId) {
        if (StringUtils.isBlank(authorizationId) || StringUtils.isBlank(login) || StringUtils.isBlank(pin)) {
            throw ObaException.builder()
                      .devMessage("Authorization Id is missing!")
                      .obaErrorCode(ObaErrorCode.LOGIN_FAILED)
                      .build();
        }
        GlobalScaResponseTO response = login(login, pin);
        response.setAuthorisationId(authorizationId);
        return response;
    }

    @Override
    public GlobalScaResponseTO login(String login, String pin) {
        if (StringUtils.isBlank(login) || StringUtils.isBlank(pin)) {
            throw ObaException.builder()
                      .devMessage("Authorization Id is missing!")
                      .obaErrorCode(ObaErrorCode.LOGIN_FAILED)
                      .build();
        }
        try {
            BearerTokenTO token = tokenService.login(login, pin);
            return getScaResponseTO(token.getAccess_token());
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    @Override
    public GlobalScaResponseTO loginWithCookie(String tokenCookie) {
        try {
            String token = Arrays.stream(tokenCookie.split(";"))
                               .filter(s -> s.contains("ACCESS_TOKEN=")).findFirst()
                               .map(s -> s.replace("ACCESS_TOKEN=", ""))
                               .orElse("");
            return getScaResponseTO(token);
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    private GlobalScaResponseTO getScaResponseTO(String tokenString) {
        BearerTokenTO token = tokenService.validate(tokenString);
        authInterceptor.setAccessToken(token.getAccess_token());
        UserTO user = ledgersUserMgmt.getUser().getBody();
        GlobalScaResponseTO response = new GlobalScaResponseTO();
        response.setBearerToken(token);
        response.setScaMethods(user.getScaUserData());
        response.setScaStatus(ScaStatusTO.PSUIDENTIFIED);
        authInterceptor.setAccessToken(null);
        return response;
    }

    private ObaException throwException(Exception e) {
        log.error(e.getMessage());
        authInterceptor.setAccessToken(null);
        return ObaException.builder()
                   .devMessage(e.getMessage())
                   .obaErrorCode(ObaErrorCode.LOGIN_FAILED)
                   .build();
    }
}

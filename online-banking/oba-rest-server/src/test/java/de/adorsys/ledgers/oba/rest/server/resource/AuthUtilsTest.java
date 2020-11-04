package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthUtilsTest {

    private static final String LOGIN = "anton.brueckner";

    @Test
    void checkIfUserInitiatedOperation() {
        AuthUtils.checkIfUserInitiatedOperation(getLoginResult(), Collections.singletonList(getPsuIdData(LOGIN)));
    }

    @Test
    void checkIfUserInitiatedOperation_fail() {
        // Then
        assertThrows(AuthorizationException.class, () -> AuthUtils.checkIfUserInitiatedOperation(getLoginResult(), Collections.singletonList(getPsuIdData("some wrong login"))));
    }

    private PsuIdData getPsuIdData(String login) {
        return new PsuIdData(login, null, null, null, null);
    }

    private GlobalScaResponseTO getLoginResult() {
        GlobalScaResponseTO result = new GlobalScaResponseTO();
        BearerTokenTO token = new BearerTokenTO();
        AccessTokenTO acc = new AccessTokenTO();
        acc.setLogin(LOGIN);
        token.setAccessTokenObject(acc);
        result.setBearerToken(token);
        return result;
    }
}

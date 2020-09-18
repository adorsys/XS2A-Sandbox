package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import static de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode.LOGIN_FAILED;

public class AuthUtils {

    private AuthUtils() {
    }

    public static String psuId(ObaMiddlewareAuthentication auth) {
        if (auth == null) {
            return null;
        }
        return psuId(auth.getBearerToken());
    }

    public static String psuId(BearerTokenTO bearerToken) {
        return bearerToken.getAccessTokenObject().getLogin();
    }

    public static void checkIfUserInitiatedOperation(GlobalScaResponseTO loginResult, List<PsuIdData> psuIdData) {
        if (CollectionUtils.isNotEmpty(psuIdData)) {
            AuthUtils.checkUserInitiatedProcedure(psuIdData.get(0).getPsuId(), loginResult.getBearerToken());
        }
    }

    private static void checkUserInitiatedProcedure(String loginFromRequest, BearerTokenTO bearerToken) {
        if (!psuId(bearerToken).equals(loginFromRequest)) {
            throw AuthorizationException.builder()
                      .errorCode(LOGIN_FAILED)
                      .devMessage("Operation you're logging in is not meant for current user")
                      .build();
        }
    }
}

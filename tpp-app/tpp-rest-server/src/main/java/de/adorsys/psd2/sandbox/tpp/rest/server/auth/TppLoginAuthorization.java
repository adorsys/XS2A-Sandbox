package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.psd2.sandbox.auth.LoginAuthorization;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class TppLoginAuthorization implements LoginAuthorization {
    @Override
    public boolean canLogin(BearerTokenTO bearerTokenTO) {
        return EnumSet.of(UserRoleTO.SYSTEM, UserRoleTO.STAFF).contains(bearerTokenTO.getAccessTokenObject().getRole());
    }
}

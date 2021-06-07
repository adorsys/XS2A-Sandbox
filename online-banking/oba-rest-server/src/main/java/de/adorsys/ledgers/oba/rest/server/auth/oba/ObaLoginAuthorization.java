package de.adorsys.ledgers.oba.rest.server.auth.oba;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.psd2.sandbox.auth.LoginAuthorization;
import org.springframework.stereotype.Component;

@Component
public class ObaLoginAuthorization implements LoginAuthorization {
    @Override
    public boolean canLogin(BearerTokenTO bearerTokenTO) {
        return UserRoleTO.CUSTOMER == bearerTokenTO.getAccessTokenObject().getRole();
    }
}

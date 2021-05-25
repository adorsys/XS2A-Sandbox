package de.adorsys.psd2.sandbox.auth;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;

public interface LoginAuthorization {

    boolean canLogin(BearerTokenTO bearerTokenTO);
}

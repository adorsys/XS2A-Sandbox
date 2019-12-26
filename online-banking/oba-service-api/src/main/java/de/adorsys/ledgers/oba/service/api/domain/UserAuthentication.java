package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import lombok.Value;

@Value
public class UserAuthentication {
    private BearerTokenTO bearerToken;
}

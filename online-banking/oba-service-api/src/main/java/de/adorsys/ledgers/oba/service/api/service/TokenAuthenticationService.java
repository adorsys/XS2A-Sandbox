package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;

public interface TokenAuthenticationService {

    UserAuthentication getAuthentication(String accessToken);

}

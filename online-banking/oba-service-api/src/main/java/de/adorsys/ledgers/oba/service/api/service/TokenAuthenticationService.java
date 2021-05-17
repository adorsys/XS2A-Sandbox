package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;

public interface TokenAuthenticationService {

    GlobalScaResponseTO login(String login, String pin, String authorizationId);

    GlobalScaResponseTO login(String login, String pin);

    UserAuthentication getAuthentication(String accessToken);

}

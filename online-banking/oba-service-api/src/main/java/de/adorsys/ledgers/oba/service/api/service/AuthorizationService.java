package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;

public interface AuthorizationService {
    SCALoginResponseTO login(String login, String pin);
}

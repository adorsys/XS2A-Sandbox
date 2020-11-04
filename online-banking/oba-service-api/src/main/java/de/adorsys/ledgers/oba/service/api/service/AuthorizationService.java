package de.adorsys.ledgers.oba.service.api.service;

public interface AuthorizationService {
    String resolveAuthConfirmationCodeRedirectUri(String redirectUri, String code);
}

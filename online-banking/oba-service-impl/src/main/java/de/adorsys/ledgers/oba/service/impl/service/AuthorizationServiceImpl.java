package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.service.AuthorizationService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final UserMgmtRestClient userMgmtRestClient;
    private static final String LOGIN_ERROR_MSG_LOGGER = "Error during login operation for user: {}, status: {}, message: {}";
    private static final String LOGIN_ERROR_MSG = "Error during login operation for user: %s";

    @Override
    public SCALoginResponseTO login(String login, String pin) {
        try {
            return userMgmtRestClient.authorise(login, pin, UserRoleTO.CUSTOMER).getBody();
        } catch (FeignException e) {
            log.error(LOGIN_ERROR_MSG_LOGGER, login, e.status(), e.getMessage());
            throw AuthorizationException.builder()
                      .devMessage(String.format(LOGIN_ERROR_MSG, login))
                      .errorCode(AuthErrorCode.LOGIN_FAILED)
                      .build();
        }
    }
}

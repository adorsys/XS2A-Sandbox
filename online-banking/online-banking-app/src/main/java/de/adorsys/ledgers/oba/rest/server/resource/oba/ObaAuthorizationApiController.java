package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserCredentialsTO;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaAuthorizationApi;
import de.adorsys.ledgers.oba.rest.server.mapper.LedgersResponseMapper;
import de.adorsys.ledgers.oba.rest.server.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ObaAuthorizationApi.BASE_PATH)
@RequiredArgsConstructor
public class ObaAuthorizationApiController implements ObaAuthorizationApi {
    private final AuthorizationService authorizationService;
    private final LedgersResponseMapper responseMapper;

    @Override
    public ResponseEntity<AuthorizeResponse> login(UserCredentialsTO credentials) {
        SCALoginResponseTO response = authorizationService.login(credentials.getLogin(), credentials.getPin());
        return ResponseEntity.ok(responseMapper.toAuthorizeResponse(response));
    }
}

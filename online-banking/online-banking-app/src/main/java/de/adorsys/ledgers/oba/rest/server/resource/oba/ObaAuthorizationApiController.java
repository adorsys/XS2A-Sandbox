package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaAuthorizationApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ObaAuthorizationApi.BASE_PATH)
@RequiredArgsConstructor
public class ObaAuthorizationApiController implements ObaAuthorizationApi {

    @Override
    public void login(String login, String password) {}
}

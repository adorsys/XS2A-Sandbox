package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaOauthApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ObaOauthApi.BASE_PATH)
public class ObaOauthController implements ObaOauthApi {

    @Override
    public void oauthCode(String login, String pin, String redirectUi) {}

    @Override
    public void oauthToken(String code) {}
}

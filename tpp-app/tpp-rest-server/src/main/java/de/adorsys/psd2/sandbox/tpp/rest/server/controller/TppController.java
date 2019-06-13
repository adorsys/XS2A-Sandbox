package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.TppInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.TppInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppRestApi.BASE_PATH)
public class TppController implements TppRestApi {
    private final TppInfoMapper tppInfoMapper;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;

    @Override
    public void login(String login, String pin) {}

    @Override
    public ResponseEntity<Void> register(TppInfo tppInfo) {
        ResponseEntity<UserTO> response = userMgmtStaffRestClient.register(tppInfo.getId(), tppInfoMapper.fromTppInfo(tppInfo));

        return HttpStatus.OK == response.getStatusCode()
                   ? ResponseEntity.status(CREATED).build()
                   : ResponseEntity.badRequest().build();
    }
}

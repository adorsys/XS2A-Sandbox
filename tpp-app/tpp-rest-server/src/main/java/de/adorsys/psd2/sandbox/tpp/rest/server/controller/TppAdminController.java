package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppAdminRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppAdminRestApi.BASE_PATH)
public class TppAdminController implements TppAdminRestApi {
    private final UserMapper userMapper;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;
    private final DataRestClient dataRestClient;

    @Override
    public ResponseEntity<Void> register(User user) {
        UserTO userTO = userMapper.toUserTO(user);
        userMgmtStaffRestClient.register(user.getId(), userTO);
        return ResponseEntity.status(CREATED).build();
    }

    @Override
    public ResponseEntity<Void> remove(String tppId) {
        return dataRestClient.branch(tppId);
    }
}

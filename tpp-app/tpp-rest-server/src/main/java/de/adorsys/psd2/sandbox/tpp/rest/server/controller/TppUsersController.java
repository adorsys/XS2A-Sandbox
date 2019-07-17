package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppUsersRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppUsersRestApi.BASE_PATH)
public class TppUsersController implements TppUsersRestApi {
    private final UserMapper userMapper;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;

    @Override
    public ResponseEntity<UserTO> createUser(User user) {
        return userMgmtStaffRestClient.createUser(userMapper.toUserTO(user));
    }

    @Override
    public ResponseEntity<List<UserTO>> getAllUsers() {
        return userMgmtStaffRestClient.getBranchUsersByRoles(Arrays.asList(UserRoleTO.CUSTOMER));
    }
}

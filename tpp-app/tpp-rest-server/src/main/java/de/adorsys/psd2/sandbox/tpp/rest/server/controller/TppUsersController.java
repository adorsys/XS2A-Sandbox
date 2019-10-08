package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.middleware.rest.utils.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppUsersRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO.CUSTOMER;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppUsersRestApi.BASE_PATH)
public class TppUsersController implements TppUsersRestApi {
    private final UserMapper userMapper;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;
    private final UserMgmtRestClient userMgmtRestClient;

    @Override
    public ResponseEntity<UserTO> createUser(User user) {
        return userMgmtStaffRestClient.createUser(userMapper.toUserTO(user));
    }

    @Override
    public ResponseEntity<CustomPageImpl<UserTO>> getAllUsers(int page, int size) {
        return ResponseEntity.ok(userMgmtStaffRestClient.getBranchUsersByRoles(singletonList(CUSTOMER), page, size).getBody());
    }

    // TODO resolve 'branch' on Ledgers side
    @Override
    public ResponseEntity<Void> updateUser(User user) {
        if (StringUtils.isBlank(user.getId())) {
            throw new TppException("User id is not present in body!", 400);
        }
        String branch = Optional.ofNullable(userMgmtRestClient.getUser().getBody())
                            .map(UserTO::getBranch)
                            .orElseThrow(() -> new TppException("No tpp code present!", 400));
        UserTO userTO = userMapper.toUserTO(user);
        userTO.setBranch(branch);
        userMgmtStaffRestClient.modifyUser(branch, userTO);
        return new ResponseEntity<>(OK);
    }

    @Override
    public ResponseEntity<UserTO> getUser(String userId) {
        return userMgmtRestClient.getUserById(userId);
    }

    @Override
    public ResponseEntity<UserTO> getSelf() {
        return userMgmtRestClient.getUser();
    }
}

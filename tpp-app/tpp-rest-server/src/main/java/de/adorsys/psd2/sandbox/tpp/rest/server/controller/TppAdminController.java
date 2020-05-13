package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AdminRestClient;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppAdminRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
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
    private final DataRestClient dataRestClient;
    private final AdminRestClient adminRestClient;
    private final UserMgmtRestClient userClient;
    private final UserMgmtStaffRestClient userStaffClient;

    @Override
    public ResponseEntity<CustomPageImpl<UserTO>> users(String countryCode, String tppId, String tppLogin, String userLogin, UserRoleTO role, Boolean blocked, int page, int size) {
        return adminRestClient.users(countryCode, tppId, tppLogin, userLogin, role, blocked, page, size);
    }

    @Override
    public ResponseEntity<CustomPageImpl<UserTO>> user(UserTO user) {
        checkUpdateData(user);
        userStaffClient.modifyUser(user.getBranch(), user);
        return ResponseEntity.accepted().build();
    }

    private void checkUpdateData(UserTO user) {
        UserTO userStored = userClient.getUserById(user.getId()).getBody();
        if (userStored.isBlocked() || userStored.isSystemBlocked()) {
            throw new TppException("You are not allowed to modify a Blocked user!", 400);
        }
        if (!userStored.getUserRoles().containsAll(user.getUserRoles())) {
            throw new TppException("You are not allowed to modify users roles!", 400);
        }
        if (!userStored.getBranch().equals(user.getBranch())) {
            throw new TppException("User are not allowed to modify users tpp relation!", 400);
        }
    }

    @Override
    public ResponseEntity<CustomPageImpl<AccountDetailsTO>> accounts(String countryCode, String tppId, String tppLogin, String ibanParam, Boolean isBlocked, int page, int size) {
        return adminRestClient.accounts(countryCode, tppId, tppLogin, ibanParam, isBlocked, page, size);
    }

    @Override
    public ResponseEntity<Void> register(User user, String tppId) {
        UserTO userTO = userMapper.toUserTO(user);
        if (userTO.getUserRoles().contains(UserRoleTO.CUSTOMER)) {
            userTO.setBranch(tppId);
        }
        adminRestClient.register(userTO);
        return ResponseEntity.status(CREATED).build();
    }

    @Override
    public ResponseEntity<Void> remove(String tppId) {
        return dataRestClient.branch(tppId);
    }

    @Override
    public ResponseEntity<Void> updatePassword(String tppId, String password) {
        return adminRestClient.updatePassword(tppId, password);
    }

    @Override
    public ResponseEntity<Boolean> changeStatus(String tppId) {
        return adminRestClient.changeStatus(tppId);
    }
}

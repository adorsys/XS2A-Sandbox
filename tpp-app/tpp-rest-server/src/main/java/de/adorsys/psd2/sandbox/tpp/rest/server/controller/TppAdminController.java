package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AdminRestClient;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
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
    private final DataRestClient dataRestClient;
    private final AdminRestClient adminRestClient;

    @Override
    public ResponseEntity<CustomPageImpl<UserExtendedTO>> users(String countryCode, String tppId, String tppLogin, String userLogin, UserRoleTO role, Boolean blocked, int page, int size) {
        return adminRestClient.users(countryCode, tppId, tppLogin, userLogin, role, blocked, page, size);
    }

    @Override
    public ResponseEntity<Void> user(UserTO user) {
        return adminRestClient.user(user);
    }

    @Override
    public ResponseEntity<CustomPageImpl<AccountDetailsExtendedTO>> accounts(String countryCode, String tppId, String tppLogin, String ibanParam, Boolean isBlocked, int page, int size) {
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
    public ResponseEntity<Boolean> changeStatus(String userId) {
        return adminRestClient.changeStatus(userId);
    }
}

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AdminRestClient;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.CmsDbNativeService;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppAdminRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(TppAdminRestApi.BASE_PATH)
public class TppAdminController implements TppAdminRestApi {
    private static final int PAUSE_AFTER_BATCH = 20;

    private final UserMapper userMapper;
    private final DataRestClient dataRestClient;
    private final AdminRestClient adminRestClient;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;
    private final CmsDbNativeService cmsDbNativeService;

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
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> admin(User user) {
        UserTO userTO = userMapper.toUserTO(user);
        userTO.setUserRoles(Collections.singletonList(UserRoleTO.SYSTEM));
        adminRestClient.register(userTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<CustomPageImpl<UserTO>> admins(int page, int size) {
        return adminRestClient.admins(page, size);
    }

    @Override
    public ResponseEntity<Void> remove(String tppId) {
        ResponseEntity<List<String>> loginsByBranchIdResponse = userMgmtStaffRestClient.getBranchUserLoginsByBranchId(tppId);
        List<String> logins = loginsByBranchIdResponse.getBody();
        dataRestClient.branch(tppId);
        log.debug("User data for login [{}] was removed from Ledgers.", tppId);
        cmsDbNativeService.deleteConsentsByUserIds(logins);
        log.debug("User data for login [{}] was removed from CMS.", logins);
        log.info("TPP [{}] was removed.", tppId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Void> removeAllTestData() throws InterruptedException {
        long start = System.currentTimeMillis();
        ResponseEntity<CustomPageImpl<UserExtendedTO>> response = adminRestClient.users(null, null, null, null, UserRoleTO.STAFF, null, 0, 9999);
        if (response != null && response.getBody() != null) {
            List<UserExtendedTO> content = response.getBody().getContent(); //NOSONAR
            List<UserExtendedTO> collect = content.stream().filter(u -> u.getLogin().matches("[0-9]+")).collect(Collectors.toList());
            Set<String> ids = collect.stream().map(UserTO::getId).collect(Collectors.toSet());

            var count = 0;
            for (String id : ids) {
                remove(id);
                if (count % PAUSE_AFTER_BATCH == 0) {
                    Thread.sleep(1000);
                }
                count++;
            }

            log.info("Deletion of test TPPs is completed: {} items, in {}ms", ids.size(), System.currentTimeMillis() - start);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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

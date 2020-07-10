package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.ResetPasswordRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaAuthorizationApi;
import de.adorsys.ledgers.security.ResetPassword;
import de.adorsys.ledgers.security.SendCode;
import de.adorsys.ledgers.security.UpdatePassword;
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
    private final ResetPasswordRestClient resetPasswordRestClient;
    private final UserMgmtRestClient userMgmtRestClient;

    @Override
    public void login(String login, String password) {
        //See corresponding Filter
    }

    @Override
    public ResponseEntity<UserTO> getSelf() {
        return userMgmtRestClient.getUser();
    }

    @Override
    public ResponseEntity<Void> editSelf(UserTO user) {
        userMgmtRestClient.editSelf(user);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<SendCode> sendCode(ResetPassword resetPassword) {
        return resetPasswordRestClient.sendCode(resetPassword);
    }

    @Override
    public ResponseEntity<UpdatePassword> updatePassword(ResetPassword resetPassword) {
        return resetPasswordRestClient.updatePassword(resetPassword);
    }
}

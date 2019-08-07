package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.client.rest.ResetPasswordRestClient;
import de.adorsys.ledgers.security.ResetPassword;
import de.adorsys.ledgers.security.SendCode;
import de.adorsys.ledgers.security.UpdatePassword;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppResetPasswordRestApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppResetPasswordRestApi.BASE_PATH)
public class TppResetPasswordController implements TppResetPasswordRestApi {
    private final ResetPasswordRestClient resetPasswordRestClient;

    @Override
    public ResponseEntity<SendCode> sendCode(ResetPassword resetPassword) {
        return resetPasswordRestClient.sendCode(resetPassword);
    }

    @Override
    public ResponseEntity<UpdatePassword> updatePassword(ResetPassword resetPassword) {
        return resetPasswordRestClient.updatePassword(resetPassword);
    }
}

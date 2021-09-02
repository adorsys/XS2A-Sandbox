package de.adorsys.psd2.sandbox.admin.rest.server.controller;

import de.adorsys.ledgers.middleware.client.rest.ScaVerificationRestClient;
import de.adorsys.psd2.sandbox.admin.rest.api.resource.AdminEmailVerificationRestApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(AdminEmailVerificationRestApi.BASE_PATH)
public class AdminEmailVerificationController implements AdminEmailVerificationRestApi {
    private final ScaVerificationRestClient scaVerificationRestClient;

    @Override
    public ResponseEntity<Void> sendEmailVerification(String email) {
        return scaVerificationRestClient.sendEmailVerification(email);
    }

    @Override
    public ResponseEntity<Void> confirmVerificationToken(String token) {
        return scaVerificationRestClient.confirmVerificationToken(token);
    }
}

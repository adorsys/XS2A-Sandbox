package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.client.rest.ResetPasswordRestClient;
import de.adorsys.ledgers.security.ResetPassword;
import de.adorsys.ledgers.security.SendCode;
import de.adorsys.ledgers.security.UpdatePassword;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppResetPasswordControllerTest {
    private static final String LOGIN = "login";
    private static final String EMAIL = "email";
    private static final String CODE = "code";
    private static final String PHONE = "phone";
    private static final String NEW_PASS = "new_pass";

    @InjectMocks
    private TppResetPasswordController tppResetPasswordController;
    @Mock
    private ResetPasswordRestClient resetPasswordRestClient;

    @Test
    void sendCode() {
        // Given
        when(resetPasswordRestClient.sendCode(any())).thenReturn(ResponseEntity.ok(getCode()));

        // When
        ResponseEntity<SendCode> response = tppResetPasswordController.sendCode(getResetPassword());

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isSent());
    }

    @Test
    void updatePassword() {
        // Given
        when(resetPasswordRestClient.updatePassword(any())).thenReturn(ResponseEntity.ok(getUpdatePassword()));

        // When
        ResponseEntity<UpdatePassword> response = tppResetPasswordController.updatePassword(getResetPassword());

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isUpdated());
    }

    private UpdatePassword getUpdatePassword() {
        return new UpdatePassword(true);
    }

    private SendCode getCode() {
        return new SendCode(true);
    }

    private ResetPassword getResetPassword() {
        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setCode(CODE);
        resetPassword.setEmail(EMAIL);
        resetPassword.setLogin(LOGIN);
        resetPassword.setNewPassword(NEW_PASS);
        resetPassword.setPhone(PHONE);
        return resetPassword;
    }
}

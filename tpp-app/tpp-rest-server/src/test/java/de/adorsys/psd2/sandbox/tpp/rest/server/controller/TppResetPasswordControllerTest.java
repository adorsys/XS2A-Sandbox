package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.client.rest.ResetPasswordRestClient;
import de.adorsys.ledgers.security.ResetPassword;
import de.adorsys.ledgers.security.SendCode;
import de.adorsys.ledgers.security.UpdatePassword;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TppResetPasswordControllerTest {
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
    public void sendCode() {
        //given
        when(resetPasswordRestClient.sendCode(any())).thenReturn(ResponseEntity.ok(getCode()));

        //when
        ResponseEntity<SendCode> response = tppResetPasswordController.sendCode(getResetPassword());

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertTrue(response.getBody().isSent());
    }

    @Test
    public void updatePassword() {
        //given
        when(resetPasswordRestClient.updatePassword(any())).thenReturn(ResponseEntity.ok(getUpdatePassword()));

        //when
        ResponseEntity<UpdatePassword> response = tppResetPasswordController.updatePassword(getResetPassword());

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
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

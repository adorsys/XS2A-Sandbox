package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.client.rest.ScaVerificationRestClient;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TppEmailVerificationControllerTest {
    private static final String EMAIL = "EMAIL";
    private static final String TOKEN = "TOKEN";

    @InjectMocks
    private TppEmailVerificationController verificationController;
    @Mock
    private ScaVerificationRestClient scaVerificationRestClient;

    @Test
    public void sendEmailVerification() {
        //given
        when(scaVerificationRestClient.sendEmailVerification(anyString())).thenAnswer(i -> ResponseEntity.ok().build());

        //when
        ResponseEntity<Void> response = verificationController.sendEmailVerification(EMAIL);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    public void confirmVerificationToken() {
        //given
        when(scaVerificationRestClient.confirmVerificationToken(anyString())).thenAnswer(i -> ResponseEntity.ok().build());

        //when
        ResponseEntity<Void> response = verificationController.confirmVerificationToken(TOKEN);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}

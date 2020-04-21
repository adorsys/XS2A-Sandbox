package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.client.rest.ScaVerificationRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppEmailVerificationControllerTest {
    private static final String EMAIL = "EMAIL";
    private static final String TOKEN = "TOKEN";

    @InjectMocks
    private TppEmailVerificationController verificationController;
    @Mock
    private ScaVerificationRestClient scaVerificationRestClient;

    @Test
    void sendEmailVerification() {
        // Given
        when(scaVerificationRestClient.sendEmailVerification(anyString())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = verificationController.sendEmailVerification(EMAIL);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void confirmVerificationToken() {
        // Given
        when(scaVerificationRestClient.confirmVerificationToken(anyString())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = verificationController.confirmVerificationToken(TOKEN);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}

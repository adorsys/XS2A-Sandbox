package de.adorsys.ledgers.oba.service.impl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {
    private static final String PIN = "12345";
    private static final String URI = "www.google.com";
    @InjectMocks
    private AuthorizationServiceImpl service;


    @Test
    void resolveAuthConfirmationCodeRedirectUri_no_params() {
        // When
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI, PIN);

        // Then
        assertThat(result).isEqualTo(URI + "?" + "authConfirmationCode=" + PIN);
    }

    @Test
    void resolveAuthConfirmationCodeRedirectUri_with_param() {
        // When
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI + "?aaa=123", PIN);

        // Then
        assertThat(result).isEqualTo(URI + "?aaa=123" + "&" + "authConfirmationCode=" + PIN);
    }

    @Test
    void resolveAuthConfirmationCodeRedirectUri_code_null_or_empty() {
        // When
        String result = service.resolveAuthConfirmationCodeRedirectUri(URI, null);

        // Then
        assertThat(result).isEqualTo(URI);
    }
}

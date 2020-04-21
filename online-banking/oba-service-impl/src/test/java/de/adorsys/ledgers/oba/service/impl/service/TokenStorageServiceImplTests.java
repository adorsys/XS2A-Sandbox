package de.adorsys.ledgers.oba.service.impl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TokenStorageServiceImplTests {
    private static final String SCA_ID = "sca id";
    private static final String CONSENT_ID = "consent id";
    private final ObjectMapper STATIC_MAPPER = new ObjectMapper().findAndRegisterModules().registerModule(new JavaTimeModule());

    @InjectMocks
    private TokenStorageServiceImpl service;

    @Test
    void fromBytes_login() throws IOException, NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("mapper"), STATIC_MAPPER);

        // When
        SCAResponseTO result = service.fromBytes(getBytes(getLoginResponse()));

        // Then
        assertEquals(getLoginResponse(), result);
    }

    @Test
    void fromBytes_consent() throws IOException, NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("mapper"), STATIC_MAPPER);

        // When
        SCAResponseTO result = service.fromBytes(getBytes(getConsentResponse()));

        // Then
        assertEquals(getConsentResponse(), result);
    }

    @Test
    void fromBytes_payment() throws IOException, NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("mapper"), STATIC_MAPPER);

        // When
        SCAResponseTO result = service.fromBytes(getBytes(getPaymentResponse()));

        // Then
        assertEquals(getPaymentResponse(), result);
    }

    @Test
    void fromBytes_unknown_type() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("mapper"), STATIC_MAPPER);

        // When
        assertThrows(IOException.class, () -> service.fromBytes(new byte[]{}));
    }

    @Test
    void fromBytes_bytes_are_null() throws IOException {
        // When
        SCAResponseTO result = service.fromBytes(null);

        // When
        assertThat(result).isNull();
    }

    @Test
    void toBytes() throws IOException, NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("mapper"), STATIC_MAPPER);

        // When
        byte[] result = service.toBytes(getLoginResponse());

        // Then
        assertThat(result).isEqualTo(getBytes(getLoginResponse()));
    }

    @Test
    void fromBytes() throws IOException, NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("mapper"), STATIC_MAPPER);

        // When
        SCALoginResponseTO result = service.fromBytes(getBytes(getLoginResponse()), SCALoginResponseTO.class);

        // Then
        assertEquals(getLoginResponse(), result);
    }

    @Test
    void toBase64String() throws IOException, NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("mapper"), STATIC_MAPPER);

        // When
        String result = service.toBase64String(getLoginResponse());
        assertThat(result).isEqualTo(getExpectedBase64());
    }

    private String getExpectedBase64() {
        return "eyJzY2FTdGF0dXMiOm51bGwsImF1dGhvcmlzYXRpb25JZCI6bnVsbCwic2NhTWV0aG9kcyI6bnVsbCwiY2hvc2VuU2NhTWV0aG9kIjpudWxsLCJjaGFsbGVuZ2VEYXRhIjpudWxsLCJwc3VNZXNzYWdlIjpudWxsLCJzdGF0dXNEYXRlIjpudWxsLCJleHBpcmVzSW5TZWNvbmRzIjowLCJtdWx0aWxldmVsU2NhUmVxdWlyZWQiOmZhbHNlLCJhdXRoQ29uZmlybWF0aW9uQ29kZSI6bnVsbCwiYmVhcmVyVG9rZW4iOm51bGwsIm9iamVjdFR5cGUiOiJTQ0FMb2dpblJlc3BvbnNlVE8iLCJzY2FJZCI6InNjYSBpZCJ9";
    }

    private <T extends SCAResponseTO> byte[] getBytes(T response) throws JsonProcessingException {
        return STATIC_MAPPER.writeValueAsBytes(response);
    }

    private SCALoginResponseTO getLoginResponse() {
        SCALoginResponseTO to = new SCALoginResponseTO();
        to.setScaId(SCA_ID);
        return to;
    }

    private SCAConsentResponseTO getConsentResponse() {
        SCAConsentResponseTO to = new SCAConsentResponseTO();
        to.setConsentId(CONSENT_ID);
        to.setPartiallyAuthorised(false);
        return to;
    }

    private SCAPaymentResponseTO getPaymentResponse() {
        SCAPaymentResponseTO to = new SCAPaymentResponseTO();
        to.setPaymentId(CONSENT_ID);
        to.setPaymentProduct("sepa");
        to.setPaymentType(PaymentTypeTO.SINGLE);
        to.setTransactionStatus(TransactionStatusTO.RCVD);
        return to;
    }
}

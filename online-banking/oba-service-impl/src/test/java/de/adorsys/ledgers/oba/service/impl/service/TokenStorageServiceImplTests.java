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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TokenStorageServiceImplTests {
    private static final String SCA_ID = "sca id";
    private static final String CONSENT_ID = "consent id";
    private final ObjectMapper STATIC_MAPPER = new ObjectMapper().findAndRegisterModules().registerModule(new JavaTimeModule());

    @InjectMocks
    private TokenStorageServiceImpl service;

    @Test
    public void fromBytes_login() throws IOException {
        Whitebox.setInternalState(service, "mapper", STATIC_MAPPER);
        SCAResponseTO result = service.fromBytes(getBytes(getLoginResponse()));
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getLoginResponse());
    }

    @Test
    public void fromBytes_consent() throws IOException {
        Whitebox.setInternalState(service, "mapper", STATIC_MAPPER);
        SCAResponseTO result = service.fromBytes(getBytes(getConsentResponse()));
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getConsentResponse());
    }

    @Test
    public void fromBytes_payment() throws IOException {
        Whitebox.setInternalState(service, "mapper", STATIC_MAPPER);
        SCAResponseTO result = service.fromBytes(getBytes(getPaymentResponse()));
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getPaymentResponse());
    }

    @Test(expected = IOException.class)
    public void fromBytes_unknown_type() throws IOException {
        Whitebox.setInternalState(service, "mapper", STATIC_MAPPER);
        SCAResponseTO result = service.fromBytes(new byte[]{});
    }

    @Test
    public void fromBytes_bytes_are_null() throws IOException {
        SCAResponseTO result = service.fromBytes(null);
        assertThat(result).isNull();
    }

    @Test
    public void toBytes() throws IOException {
        Whitebox.setInternalState(service, "mapper", STATIC_MAPPER);
        byte[] result = service.toBytes(getLoginResponse());
        assertThat(result).isEqualTo(getBytes(getLoginResponse()));
    }

    @Test
    public void fromBytes() throws IOException {
        Whitebox.setInternalState(service, "mapper", STATIC_MAPPER);
        SCALoginResponseTO result = service.fromBytes(getBytes(getLoginResponse()), SCALoginResponseTO.class);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getLoginResponse());
    }

    @Test
    public void toBase64String() throws IOException {
        Whitebox.setInternalState(service, "mapper", STATIC_MAPPER);
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

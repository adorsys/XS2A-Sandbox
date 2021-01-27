package de.adorsys.ledgers.oba.service.impl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.oba.service.api.domain.LoginFailedCount;
import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CmsAspspConsentDataServiceImplTest {
    @InjectMocks
    private CmsAspspConsentDataServiceImpl service;

    @Mock
    private AspspConsentDataClient client;

    @BeforeEach
    void set() throws NoSuchFieldException {
        FieldSetter.setField(service, service.getClass().getDeclaredField("mapper"), new ObjectMapper());
        FieldSetter.setField(service, service.getClass().getDeclaredField("loginFailedMax"), 3);
    }

    @Test
    void toBase64String() {
        String expected = "eyJmYWlsZWRDb3VudCI6MX0=";
        String s = service.toBase64String(new LoginFailedCount(1));
        assertEquals(expected, s);
    }

    @Test
    void mapToGlobalResponse_cns() {
        LocalDateTime now = LocalDateTime.now();
        UserTO user = new UserTO();
        GlobalScaResponseTO expected = getGlobalScaResponseTO(now, user, OpTypeTO.CONSENT);
        SCAConsentResponseTO source = getScaConsentResponseTO(now, user);

        GlobalScaResponseTO result = service.mapToGlobalResponse(source, OpTypeTO.CONSENT);
        assertEquals(expected, result);
    }

    @Test
    void mapToGlobalResponse_pmt() {
        LocalDateTime now = LocalDateTime.now();
        UserTO user = new UserTO();
        GlobalScaResponseTO expected = getGlobalScaResponseTO(now, user, OpTypeTO.PAYMENT);
        SCAPaymentResponseTO source = getScaPaymentResponseTO(now, user);

        GlobalScaResponseTO result = service.mapToGlobalResponse(source, OpTypeTO.PAYMENT);
        assertEquals(expected, result);
    }

    private SCAPaymentResponseTO getScaPaymentResponseTO(LocalDateTime now, UserTO user) {
        SCAPaymentResponseTO source = new SCAPaymentResponseTO();
        source.setTransactionStatus(TransactionStatusTO.ACSC);
        source.setPaymentProduct("product");
        source.setPaymentType(PaymentTypeTO.SINGLE);
        source.setPaymentId("id");
        source.setAuthorisationId("authId");
        source.setBearerToken(new BearerTokenTO());
        source.setPsuMessage("msg");
        source.setScaStatus(ScaStatusTO.FINALISED);
        source.setScaMethods(List.of(getScaData(user)));
        source.setChosenScaMethod(getScaData(user));
        source.setChallengeData(new ChallengeDataTO());
        source.setStatusDate(now);
        source.setExpiresInSeconds(1);
        source.setMultilevelScaRequired(true);
        source.setAuthConfirmationCode("code");
        source.setObjectType(SCAPaymentResponseTO.class.getTypeName());
        return source;
    }

    private GlobalScaResponseTO getGlobalScaResponseTO(LocalDateTime now, UserTO user, OpTypeTO opType) {
        GlobalScaResponseTO expected = new GlobalScaResponseTO();
        expected.setPartiallyAuthorised(false);
        expected.setOperationObjectId("id");
        expected.setOpType(opType);
        expected.setExternalId(null);
        expected.setAuthorisationId("authId");
        expected.setScaStatus(ScaStatusTO.FINALISED);
        expected.setScaMethods(List.of(getScaData(user)));
        expected.setChallengeData(new ChallengeDataTO());
        expected.setPsuMessage("msg");
        expected.setStatusDate(now);
        expected.setExpiresInSeconds(1);
        expected.setMultilevelScaRequired(true);
        expected.setAuthConfirmationCode("code");
        expected.setTan(null);
        expected.setBearerToken(new BearerTokenTO());
        expected.setObjectType("");
        return expected;
    }

    private SCAConsentResponseTO getScaConsentResponseTO(LocalDateTime now, UserTO user) {
        SCAConsentResponseTO source = new SCAConsentResponseTO();
        source.setPartiallyAuthorised(false);
        source.setConsentId("id");
        source.setAuthorisationId("authId");
        source.setBearerToken(new BearerTokenTO());
        source.setPsuMessage("msg");
        source.setScaStatus(ScaStatusTO.FINALISED);
        source.setScaMethods(List.of(getScaData(user)));
        source.setChosenScaMethod(getScaData(user));
        source.setChallengeData(new ChallengeDataTO());
        source.setStatusDate(now);
        source.setExpiresInSeconds(1);
        source.setMultilevelScaRequired(true);
        source.setAuthConfirmationCode("code");
        source.setObjectType(SCAConsentResponseTO.class.getTypeName());
        return source;
    }

    private ScaUserDataTO getScaData(UserTO user) {
        return new ScaUserDataTO("id", ScaMethodTypeTO.APP_OTP, "val", user, false, null, true, true);
    }

    @Test
    void updateLoginFailedCount() throws JsonProcessingException {
        when(client.getAspspConsentData(any())).thenReturn(getCmsAspspCnsData(0));
        int attemptsLeft = service.updateLoginFailedCount("id");
        assertEquals(2, attemptsLeft);
    }

    @Test
    void updateLoginFailedCount_last_attempt() throws JsonProcessingException {
        when(client.getAspspConsentData(any())).thenReturn(getCmsAspspCnsData(2));
        int attemptsLeft = service.updateLoginFailedCount("id");
        assertEquals(0, attemptsLeft);
    }

    private ResponseEntity<CmsAspspConsentDataBase64> getCmsAspspCnsData(int failedCount) throws JsonProcessingException {
        CmsAspspConsentDataBase64 base64 = new CmsAspspConsentDataBase64("id", Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(new LoginFailedCount(failedCount))));
        return ResponseEntity.ok(base64);
    }

    @Test
    void isFailedLogin() throws JsonProcessingException {
        when(client.getAspspConsentData(any())).thenReturn(getCmsAspspCnsData(1));
        boolean isFailed = service.isFailedLogin("id");
        assertFalse(isFailed);
    }

    @Test
    void isFailedLogin_failed() throws JsonProcessingException {
        when(client.getAspspConsentData(any())).thenReturn(getCmsAspspCnsData(3));
        boolean isFailed = service.isFailedLogin("id");
        assertTrue(isFailed);
    }
}

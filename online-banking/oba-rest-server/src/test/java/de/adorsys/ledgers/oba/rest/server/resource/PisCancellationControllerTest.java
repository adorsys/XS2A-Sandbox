package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.payment.*;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import de.adorsys.psd2.consent.api.pis.CmsCommonPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;

import static de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO.ACSP;
import static de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO.RCVD;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PisCancellationControllerTest {
    private static final String PIN = "12345";
    private static final String LOGIN = "anton.brueckner";
    private static final String ENCRYPTED_ID = "ENC_123";
    private static final String AUTH_ID = "AUTH_1";
    private static final String METHOD_ID = "SCA_1";
    private static final String COOKIE = "COOKIE";
    private static final String TOKEN = "TOKEN";
    private static final String OK_URI = "OK_URI";
    private static final String NOK_URI = "NOK_URI";
    private static final String ASPSP_ACC_ID = "ASPSP_ACC_ID";
    private static final String IBAN = "DE123456789";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final LocalDate DATE = LocalDate.of(2020, 1, 24);
    private static final String SEPA = "sepa-credit-transfers";
    private static final String PMT_ID = "PMT_123";

    @InjectMocks
    private PisCancellationController controller;

    @Mock
    private CommonPaymentService paymentService;
    @Mock
    private XISControllerService xisService;
    @Mock
    private ResponseUtils responseUtils;
    @Mock
    private ObaMiddlewareAuthentication middlewareAuth;
    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private TokenAuthenticationService authenticationService;

    @Test
    void login() {
        // Given
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED, ACSP));
        when(authenticationService.login(anyString(), anyString(), anyString())).thenReturn(getScaLoginResponse());
        when(xisService.resolvePaymentWorkflow(any())).thenReturn(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, PSUIDENTIFIED)));

        // When
        ResponseEntity<PaymentAuthorizeResponse> result = controller.login(ENCRYPTED_ID, AUTH_ID, LOGIN, PIN, COOKIE);

        // Then
        assertEquals(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, PSUIDENTIFIED)), result);
    }

    @Test
    void login_failure() {
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED, RCVD));

        // Then
        assertThrows(AuthorizationException.class, () -> controller.login(ENCRYPTED_ID, AUTH_ID, LOGIN, PIN, COOKIE));
    }


    @Test
    void authorisePayment() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(controller, controller.getClass().getDeclaredField("middlewareAuth"), new ObaMiddlewareAuthentication(null, getBearerToken()));

        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED, ACSP));
        when(paymentService.authorizePaymentOpr(any(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(FINALISED, ACSP));

        // When
        ResponseEntity<PaymentAuthorizeResponse> result = controller.authorisePayment(ENCRYPTED_ID, AUTH_ID, METHOD_ID, COOKIE);

        // Then
        assertEquals(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, FINALISED)), result);
    }

    @Test
    void pisDone() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(controller, controller.getClass().getDeclaredField("middlewareAuth"), new ObaMiddlewareAuthentication(null, getBearerToken()));

        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.resolveRedirectUrl(anyString(), anyString(), anyString(), anyBoolean(), anyString(), any(), anyString())).thenReturn(NOK_URI);
        when(responseUtils.redirect(anyString(), any())).thenReturn(ResponseEntity.ok(getPaymentAuthorizeResponse(false, false, FAILED)));

        // When
        ResponseEntity<PaymentAuthorizeResponse> result = controller.pisDone(ENCRYPTED_ID, AUTH_ID, COOKIE, false, "code");

        // Then
        assertEquals(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, FAILED)), result);
    }

    private PaymentAuthorizeResponse getPaymentAuthorizeResponse(boolean hasAuth, boolean hasEncrPmtId, ScaStatusTO scaStatus) {
        PaymentAuthorizeResponse resp = new PaymentAuthorizeResponse(getPayment());
        resp.setAuthorisationId(AUTH_ID);
        resp.setScaStatus(scaStatus);
        resp.setAuthorisationId(hasAuth ? AUTH_ID : null);
        resp.setEncryptedConsentId(hasEncrPmtId ? ENCRYPTED_ID : null);
        return resp;
    }

    private GlobalScaResponseTO getScaLoginResponse() {
        GlobalScaResponseTO to = new GlobalScaResponseTO();
        to.setAuthorisationId(AUTH_ID);
        to.setScaStatus(PSUIDENTIFIED);
        to.setBearerToken(new BearerTokenTO(null, null, 999, null, getAccessTokenTO(), new HashSet<>()));
        return to;
    }

    private PaymentTO getPayment() {
        PaymentTO to = new PaymentTO();
        to.setTransactionStatus(TransactionStatusTO.RCVD);
        to.setPaymentProduct(SEPA);
        to.setPaymentType(PaymentTypeTO.SINGLE);
        to.setPaymentId(PMT_ID);
        to.setRequestedExecutionDate(DATE);
        to.setDebtorAccount(new AccountReferenceTO(IBAN, null, null, null, null, EUR));
        to.setAccountId(ASPSP_ACC_ID);
        PaymentTargetTO target = new PaymentTargetTO();
        target.setCreditorAccount(new AccountReferenceTO(IBAN, null, null, null, null, EUR));
        target.setCreditorName("NAME");
        target.setInstructedAmount(new AmountTO(EUR, BigDecimal.TEN));
        target.setEndToEndIdentification("END_TO_END");
        to.setTargets(Collections.singletonList(target));
        return to;
    }

    private PaymentWorkflow getPaymentWorkflow(ScaStatusTO status, TransactionStatusTO paymentStatus) {
        PaymentWorkflow workflow = new PaymentWorkflow(getCmsPaymentResponse(), getConsentReference());
        workflow.setAuthResponse(getPaymentAuthorizeResponse(true, true, status));
        workflow.setScaResponse(getScaResponse(status));
        workflow.setPaymentStatus(paymentStatus.name());
        return workflow;
    }

    private GlobalScaResponseTO getScaResponse(ScaStatusTO status) {
        GlobalScaResponseTO to = new GlobalScaResponseTO();
        to.setScaStatus(status);
        to.setAuthorisationId(AUTH_ID);
        to.setOperationObjectId(PMT_ID);
        to.setBearerToken(getBearerToken());
        to.setMultilevelScaRequired(false);
        return to;
    }

    private BearerTokenTO getBearerToken() {
        return new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO(), new HashSet<>());
    }

    private CmsPaymentResponse getCmsPaymentResponse() {
        CmsCommonPayment commonPayment = new CmsCommonPayment(SEPA);
        commonPayment.setPaymentId(PMT_ID);
        return new CmsPaymentResponse(commonPayment, AUTH_ID, OK_URI, NOK_URI);
    }

    private AccessTokenTO getAccessTokenTO() {
        AccessTokenTO tokenTO = new AccessTokenTO();
        tokenTO.setLogin(LOGIN);
        return tokenTO;
    }

    private ConsentReference getConsentReference() {
        ConsentReference ref = new ConsentReference();
        ref.setAuthorizationId(AUTH_ID);
        ref.setConsentType(ConsentType.AIS);
        ref.setCookieString(COOKIE);
        ref.setEncryptedConsentId(ENCRYPTED_ID);
        ref.setRedirectId(AUTH_ID);
        return ref;
    }
}

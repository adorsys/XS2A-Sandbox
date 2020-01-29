package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.payment.*;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.psd2.consent.api.pis.CmsCommonPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PisCancellationControllerTest {
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
    private HttpServletResponse response;
    @Mock
    private ResponseUtils responseUtils;
    @Mock
    private ObaMiddlewareAuthentication middlewareAuth;
    @Mock
    private AuthRequestInterceptor authInterceptor;

    @Test
    public void login() {
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED));
        when(xisService.performLoginForConsent(anyString(), anyString(), anyString(), anyString(), any(OpTypeTO.class))).thenReturn(getScaLoginResponse());
        when(xisService.resolvePaymentWorkflow(any())).thenReturn(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, PSUIDENTIFIED)));

        ResponseEntity<PaymentAuthorizeResponse> result = controller.login(ENCRYPTED_ID, AUTH_ID, LOGIN, PIN, COOKIE);
        assertThat(result).isEqualTo(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, PSUIDENTIFIED)));
    }

    @Test
    public void selectMethod() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.selectScaForPayment(anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString(), any())).thenReturn(getPaymentWorkflow(SCAMETHODSELECTED));

        ResponseEntity<PaymentAuthorizeResponse> result = controller.selectMethod(ENCRYPTED_ID, AUTH_ID, METHOD_ID, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, SCAMETHODSELECTED)));
    }

    @Test
    public void authorisePayment() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED));
        when(paymentService.authorizeCancelPayment(any(), anyString(), anyString())).thenReturn(getPaymentWorkflow(FINALISED));

        ResponseEntity<PaymentAuthorizeResponse> result = controller.authorisePayment(ENCRYPTED_ID, AUTH_ID, METHOD_ID, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, FINALISED)));
    }

    @Test
    public void pisDone() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.resolveRedirectUrl(anyString(), anyString(), anyString(), anyBoolean(), anyString(), any())).thenReturn(NOK_URI);
        when(responseUtils.redirect(anyString(), any())).thenReturn(ResponseEntity.ok(getPaymentAuthorizeResponse(false, false, FAILED)));

        ResponseEntity<PaymentAuthorizeResponse> result = controller.pisDone(ENCRYPTED_ID, AUTH_ID, COOKIE, false, true, false);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, FAILED)));
    }

    private PaymentAuthorizeResponse getPaymentAuthorizeResponse(boolean hasAuth, boolean hasEncrPmtId, ScaStatusTO scaStatus) {
        PaymentAuthorizeResponse resp = new PaymentAuthorizeResponse(getPayment());
        resp.setAuthorisationId(AUTH_ID);
        resp.setScaStatus(scaStatus);
        resp.setAuthorisationId(hasAuth ? AUTH_ID : null);
        resp.setEncryptedConsentId(hasEncrPmtId ? ENCRYPTED_ID : null);
        return resp;
    }

    private ResponseEntity<SCALoginResponseTO> getScaLoginResponse() {
        SCALoginResponseTO to = new SCALoginResponseTO();
        to.setAuthorisationId(AUTH_ID);
        to.setScaStatus(PSUIDENTIFIED);

        to.setBearerToken(new BearerTokenTO(null, null, 999, null, getAccessTokenTO()));
        return ResponseEntity.ok(to);
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

    private PaymentWorkflow getPaymentWorkflow(ScaStatusTO status) {
        PaymentWorkflow workflow = new PaymentWorkflow(getCmsPaymentResponse(), getConsentReference());
        workflow.setAuthResponse(getPaymentAuthorizeResponse(true, true, status));
        workflow.setScaResponse(getScaResponse(status));
        workflow.setPaymentStatus(TransactionStatusTO.ACSP.name());
        return workflow;
    }

    private SCAResponseTO getScaResponse(ScaStatusTO status) {
        SCAPaymentResponseTO to = new SCAPaymentResponseTO();
        to.setScaStatus(status);
        to.setAuthorisationId(AUTH_ID);
        to.setPaymentId(PMT_ID);
        to.setBearerToken(getBearerToken());
        to.setPaymentProduct(SEPA);
        to.setPaymentType(PaymentTypeTO.SINGLE);
        to.setMultilevelScaRequired(false);
        return to;
    }

    private BearerTokenTO getBearerToken() {
        return new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO());
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

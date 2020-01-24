package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.payment.*;
import de.adorsys.ledgers.middleware.api.domain.sca.*;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.*;
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
public class PISControllerTest {
    private static final String PIN = "12345";
    private static final String LOGIN = "anton.brueckner";
    private static final String ENCRYPTED_ID = "ENC_123";
    private static final String AUTH_ID = "AUTH_1";
    private static final String METHOD_ID = "SCA_1";
    private static final String COOKIE = "COOKIE";
    private static final String TOKEN = "TOKEN";
    private static final String OK_URI = "OK_URI";
    private static final String NOK_URI = "NOK_URI";
    private static final String REDIRECT_ID = "12345";
    private static final String ASPSP_ACC_ID = "ASPSP_ACC_ID";
    private static final String IBAN = "DE123456789";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final LocalDate DATE = LocalDate.of(2020, 1, 24);
    private static final String SEPA = "sepa-credit-transfers";
    private static final String PMT_ID = "PMT_123";

    @InjectMocks
    private PISController controller;
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
    public void pisAuth() {
        when(xisService.auth(any(), any(), any(), any())).thenReturn(getAuthResponse());

        ResponseEntity<AuthorizeResponse> result = controller.pisAuth(REDIRECT_ID, ENCRYPTED_ID, TOKEN);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getAuthResponse());
    }

    @Test
    public void login() {
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED));
        when(xisService.performLoginForConsent(anyString(), anyString(), anyString(), anyString(), any(OpTypeTO.class))).thenReturn(getScaLoginResponse());
        when(paymentService.initiatePayment(any(), anyString())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED));
        when(xisService.resolvePaymentWorkflow(any())).thenReturn(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, PSUIDENTIFIED)));

        ResponseEntity<PaymentAuthorizeResponse> result = controller.login(ENCRYPTED_ID, AUTH_ID, LOGIN, PIN, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, PSUIDENTIFIED)));
    }

    @Test
    public void initiatePayment() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED));
        when(paymentService.initiatePayment(any(), anyString())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED));
        when(xisService.resolvePaymentWorkflow(any())).thenReturn(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, PSUIDENTIFIED)));

        ResponseEntity<PaymentAuthorizeResponse> result = controller.initiatePayment(ENCRYPTED_ID, AUTH_ID, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, PSUIDENTIFIED)));
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
    public void authrizedPayment() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(PSUIDENTIFIED));
        when(paymentService.authorizePayment(any(), anyString(), anyString())).thenReturn(getPaymentWorkflow(FINALISED));

        ResponseEntity<PaymentAuthorizeResponse> result = controller.authrizedPayment(ENCRYPTED_ID, AUTH_ID, METHOD_ID, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, FINALISED)));
    }

    @Test
    public void failPaymentAuthorisation() {
        Whitebox.setInternalState(controller, "middlewareAuth", new ObaMiddlewareAuthentication(null, new BearerTokenTO(TOKEN, null, 999, null, getAccessTokenTO())));
        when(responseUtils.consentCookie(any())).thenReturn(COOKIE);
        when(paymentService.identifyPayment(anyString(), anyString(), anyBoolean(), anyString(), anyString(), any())).thenReturn(getPaymentWorkflow(FAILED));

        ResponseEntity<PaymentAuthorizeResponse> result = controller.failPaymentAuthorisation(ENCRYPTED_ID, AUTH_ID, COOKIE);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPaymentAuthorizeResponse(true, true, FAILED)));
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

    private PaymentWorkflow getPaymentWorkflow(ScaStatusTO status) {
        PaymentWorkflow workflow = new PaymentWorkflow(getCmsPaymentResponse(), getConsentReference());
        workflow.setAuthResponse(getPaymentAuthorizeResponse(true, true, status));
        workflow.setScaResponse(getScaResponse(status));
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

    private PaymentAuthorizeResponse getPaymentAuthorizeResponse(boolean hasAuth, boolean hasEncrPmtId, ScaStatusTO scaStatus) {
        PaymentAuthorizeResponse resp = new PaymentAuthorizeResponse(getPayment());
        resp.setAuthorisationId(AUTH_ID);
        resp.setScaStatus(scaStatus);
        resp.setAuthorisationId(hasAuth ? AUTH_ID : null);
        resp.setEncryptedConsentId(hasEncrPmtId ? ENCRYPTED_ID : null);
        return resp;
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

    private ResponseEntity<AuthorizeResponse> getAuthResponse() {
        AuthorizeResponse resp = new AuthorizeResponse();
        resp.setAuthorisationId(AUTH_ID);
        resp.setEncryptedConsentId(ENCRYPTED_ID);
        resp.setScaStatus(PSUIDENTIFIED);
        resp.setScaMethods(Collections.emptyList());
        return ResponseEntity.ok(resp);
    }

    private ResponseEntity<SCALoginResponseTO> getScaLoginResponse() {
        SCALoginResponseTO to = new SCALoginResponseTO();
        to.setAuthorisationId(AUTH_ID);
        to.setScaStatus(PSUIDENTIFIED);

        to.setBearerToken(new BearerTokenTO(null, null, 999, null, getAccessTokenTO()));
        return ResponseEntity.ok(to);
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

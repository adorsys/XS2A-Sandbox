package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.payment.*;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.*;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import de.adorsys.psd2.consent.api.pis.CmsCommonPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.FINALISED;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.PSUIDENTIFIED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XISControllerServiceTest {
    private static final String PIN = "12345";
    private static final String LOGIN = "anton.brueckner";
    private static final String ENCRYPTED_ID = "ENC_123";
    private static final String AUTH_ID = "AUTH_1";
    private static final String COOKIE = "COOKIE";
    private static final String TOKEN = "TOKEN";
    private static final String OK_URI = "OK_URI";
    private static final String NOK_URI = "NOK_URI";
    private static final String CONSENT_ID = "12345";
    private static final String ASPSP_ACC_ID = "ASPSP_ACC_ID";
    private static final String IBAN = "DE123456789";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final String REDIRECT_ID = "12345";
    private static final LocalDate DATE = LocalDate.of(2020, 1, 24);
    private static final String SEPA = "sepa-credit-transfers";
    private static final String PMT_ID = "PMT_123";

    @InjectMocks
    private XISControllerService service;

    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;
    @Mock
    private ConsentReferencePolicy referencePolicy;
    @Mock
    private ResponseUtils responseUtils;

    @Test
    public void auth() {
        Whitebox.setInternalState(service, "response", new MockHttpServletResponse());
        Whitebox.setInternalState(service, "loginPage", "www.loginPage.html");
        when(referencePolicy.fromURL(anyString(), any(), anyString())).thenReturn(getConsentReference());

        ResponseEntity<AuthorizeResponse> result = service.auth(AUTH_ID, ConsentType.AIS, ENCRYPTED_ID, response);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getExpectedAuthResponse());
    }

    @Test
    public void performLoginForConsent() {
        Whitebox.setInternalState(service, "request", new MockHttpServletRequest());
        when(userMgmtRestClient.authoriseForConsent(anyString(), anyString(), anyString(), anyString(), any())).thenReturn(getLoginResponse());

        ResponseEntity<SCALoginResponseTO> result = service.performLoginForConsent(LOGIN, PIN, CONSENT_ID, AUTH_ID, OpTypeTO.CONSENT);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getLoginResponse());
    }

    @Test
    public void resolvePaymentWorkflow() {
        ResponseEntity<PaymentAuthorizeResponse> result = service.resolvePaymentWorkflow(getPaymentWorkflow());
        assertThat(result).isEqualToComparingFieldByFieldRecursively(ResponseEntity.ok(getPaymentAuthorizeResponse()));
    }

    private ResponseEntity<SCALoginResponseTO> getLoginResponse() {
        SCALoginResponseTO res = new SCALoginResponseTO();
        res.setScaStatus(PSUIDENTIFIED);
        AccessTokenTO to = new AccessTokenTO();
        to.setAuthorisationId(AUTH_ID);
        res.setBearerToken(new BearerTokenTO(null, null, 0, null, to));
        res.setScaId(ENCRYPTED_ID);
        res.setAuthorisationId(AUTH_ID);
        res.setMultilevelScaRequired(false);
        res.setScaMethods(Collections.emptyList());
        return ResponseEntity.ok(res);
    }

    private ConsentReference getConsentReference() {
        ConsentReference ref = new ConsentReference();
        ref.setAuthorizationId(AUTH_ID);
        ref.setConsentType(ConsentType.AIS);
        ref.setCookieString(COOKIE);
        ref.setEncryptedConsentId(ENCRYPTED_ID);
        ref.setRedirectId(REDIRECT_ID);
        return ref;
    }

    private ResponseEntity<AuthorizeResponse> getExpectedAuthResponse() {
        AuthorizeResponse res = new AuthorizeResponse();
        res.setEncryptedConsentId(ENCRYPTED_ID);
        res.setAuthorisationId(AUTH_ID);
        res.setPsuMessages(Collections.emptyList());
        return ResponseEntity.ok(res);
    }

    private PaymentWorkflow getPaymentWorkflow() {
        PaymentWorkflow workflow = new PaymentWorkflow(getCmsPaymentResponse(), getConsentReference());
        workflow.setAuthResponse(getPaymentAuthorizeResponse());
        workflow.setScaResponse(getScaResponse());
        return workflow;
    }

    private SCAResponseTO getScaResponse() {
        SCAPaymentResponseTO to = new SCAPaymentResponseTO();
        to.setScaStatus(FINALISED);
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

    private PaymentAuthorizeResponse getPaymentAuthorizeResponse() {
        PaymentAuthorizeResponse resp = new PaymentAuthorizeResponse(getPayment());
        resp.setAuthorisationId(AUTH_ID);
        resp.setScaStatus(FINALISED);
        resp.setAuthorisationId(AUTH_ID);
        resp.setEncryptedConsentId(ENCRYPTED_ID);
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

    private AccessTokenTO getAccessTokenTO() {
        AccessTokenTO tokenTO = new AccessTokenTO();
        tokenTO.setLogin(LOGIN);
        return tokenTO;
    }
}

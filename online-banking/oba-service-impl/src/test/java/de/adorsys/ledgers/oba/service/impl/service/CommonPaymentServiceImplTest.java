package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.mappers.PaymentMapperTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.AuthorizationService;
import de.adorsys.ledgers.oba.service.api.service.CmsAspspConsentDataService;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import de.adorsys.psd2.consent.api.pis.CmsCommonPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.psu.api.CmsPsuAuthorisation;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import de.adorsys.psd2.xs2a.core.exception.AuthorisationIsExpiredException;
import de.adorsys.psd2.xs2a.core.exception.RedirectUrlIsExpiredException;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Optional;

import static de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO.ACCP;
import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.ACSC;
import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.CANC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommonPaymentServiceImplTest {

    private static final String ENCRYPTED_ID = "ENC_123";
    private static final String AUTH_ID = "AUTH_1";
    private static final String METHOD_ID = "SCA_1";
    private static final String COOKIE = "COOKIE";
    private static final String PSU_ID = "PSU_1";
    private static final String PAYMENT_ID = "PAYMENT_1";
    private static final String OK_REDIRECT_URI = "www.ok.ua";
    private static final String NOK_REDIRECT_URI = "www.nok.ua";
    private static final String AUTH_CODE = "123456";

    @InjectMocks
    private CommonPaymentServiceImpl service;

    @Mock
    private ConsentReferencePolicy referencePolicy;
    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private CmsPsuPisService cmsPsuPisService;
    @Mock
    private PaymentRestClient paymentRestClient;
    @Mock
    private AspspConsentDataClient aspspConsentDataClient;
    @Mock
    private CmsAspspConsentDataService dataService;
    @Mock
    private PaymentMapperTO paymentMapper;
    @Mock
    private OauthRestClient oauthRestClient;
    @Mock
    private AuthorizationService authService;
    @Mock
    private RedirectScaRestClient redirectScaClient;

    @Test
    void selectScaForPayment() throws RedirectUrlIsExpiredException {
        // Given
        when(paymentMapper.toAbstractPayment(anyString(), anyString(), anyString())).thenReturn(getPaymentTO(ACCP));
        when(referencePolicy.fromRequest(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(getConsentReference());
        when(cmsPsuPisService.checkRedirectAndGetPayment(anyString(), anyString())).thenReturn(getCmsPaymentResponse());
        when(redirectScaClient.startSca(any())).thenReturn(ResponseEntity.ok(new GlobalScaResponseTO()));
        when(redirectScaClient.selectMethod(any(), any())).thenReturn(ResponseEntity.ok(getSelectMethodResponse()));
        // When
        PaymentWorkflow result = service.selectScaForPayment(ENCRYPTED_ID, AUTH_ID, METHOD_ID, COOKIE, PSU_ID, new BearerTokenTO());

        // Then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getExpectedWorkflow(TransactionStatus.ACCP.name()));
    }

    @Test
    void identifyPayment() throws RedirectUrlIsExpiredException {
        // Given
        when(referencePolicy.fromRequest(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(getConsentReference());
        when(cmsPsuPisService.checkRedirectAndGetPayment(anyString(), anyString())).thenReturn(getCmsPaymentResponse());
        when(paymentMapper.toAbstractPayment(anyString(), anyString(), anyString())).thenReturn(getPaymentTO(ACCP));

        // When
        PaymentWorkflow result = service.identifyPayment(ENCRYPTED_ID, AUTH_ID, false, COOKIE, new BearerTokenTO());

        // Then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getExpectedIdentifyWorkflow(TransactionStatus.ACCP.name()));
    }

    @Test
    void identifyPayment_fail() throws RedirectUrlIsExpiredException {
        // Given
        when(referencePolicy.fromRequest(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(getConsentReference());
        when(cmsPsuPisService.checkRedirectAndGetPayment(anyString(), anyString())).thenThrow(RedirectUrlIsExpiredException.class);
        BearerTokenTO token = new BearerTokenTO();
        // Then
        assertThrows(ObaException.class, () -> service.identifyPayment(ENCRYPTED_ID, AUTH_ID, false, COOKIE, token));
    }

    @Test
    void resolveRedirectUrl() throws RedirectUrlIsExpiredException {
        // Given
        when(referencePolicy.fromRequest(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(getConsentReference());
        when(cmsPsuPisService.checkRedirectAndGetPayment(anyString(), anyString())).thenReturn(getCmsPaymentResponse());
        when(paymentMapper.toAbstractPayment(anyString(), anyString(), anyString())).thenReturn(getPaymentTO(ACCP));
        when(cmsPsuPisService.getAuthorisationByAuthorisationId(anyString(), anyString())).thenReturn(geCmsPsuAuth(ScaStatus.FINALISED));
        when(authService.resolveAuthConfirmationCodeRedirectUri(anyString(), anyString())).thenReturn("www.ok.ua");

        // When
        String result = service.resolveRedirectUrl(ENCRYPTED_ID, AUTH_ID, COOKIE, false, PSU_ID, new BearerTokenTO(), "");

        // Then
        assertEquals(OK_REDIRECT_URI, result);
    }

    @Test
    void resolveRedirectUrl_fail() throws RedirectUrlIsExpiredException {
        // Given
        when(referencePolicy.fromRequest(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(getConsentReference());
        when(cmsPsuPisService.checkRedirectAndGetPayment(anyString(), anyString())).thenReturn(getCmsPaymentResponse());
        when(paymentMapper.toAbstractPayment(anyString(), anyString(), anyString())).thenReturn(getPaymentTO(ACCP));
        when(cmsPsuPisService.getAuthorisationByAuthorisationId(anyString(), anyString())).thenReturn(Optional.empty());
        when(authService.resolveAuthConfirmationCodeRedirectUri(anyString(), anyString())).thenReturn("www.ok.ua");
        BearerTokenTO token = new BearerTokenTO();
        // Then
        assertThrows(ObaException.class, () -> service.resolveRedirectUrl(ENCRYPTED_ID, AUTH_ID, COOKIE, false, PSU_ID, token, ""));
    }

    @Test
    void resolveRedirectUrl_not_final_status() throws RedirectUrlIsExpiredException {
        // Given
        when(referencePolicy.fromRequest(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(getConsentReference());
        when(cmsPsuPisService.checkRedirectAndGetPayment(anyString(), anyString())).thenReturn(getCmsPaymentResponse());
        when(paymentMapper.toAbstractPayment(anyString(), anyString(), anyString())).thenReturn(getPaymentTO(ACCP));
        when(cmsPsuPisService.getAuthorisationByAuthorisationId(anyString(), anyString())).thenReturn(geCmsPsuAuth(ScaStatus.SCAMETHODSELECTED));
        when(authService.resolveAuthConfirmationCodeRedirectUri(anyString(), anyString())).thenReturn("www.ok.ua");

        // When
        String result = service.resolveRedirectUrl(ENCRYPTED_ID, AUTH_ID, COOKIE, false, PSU_ID, new BearerTokenTO(), "");

        // Then
        assertEquals(NOK_REDIRECT_URI, result);
    }

    @Test
    void initiatePayment() {
        // Given
        when(paymentRestClient.initiatePayment(any(), any())).thenReturn(ResponseEntity.ok(getSelectMethodResponse(TransactionStatus.ACCP.name())));
        when(dataService.mapToGlobalResponse(any(), any())).thenReturn(getSelectMethodResponse());

        // When
        PaymentWorkflow result = service.initiatePaymentOpr(getExpectedWorkflow(null), PSU_ID, OpTypeTO.PAYMENT);

        // Then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getExpectedWorkflow(TransactionStatus.ACCP.name()));
    }

    @Test
    void initiatePayment_fail() throws AuthorisationIsExpiredException {
        // Given
        when(paymentRestClient.initiatePayment(any(), any())).thenReturn(ResponseEntity.ok(getSelectMethodResponse(TransactionStatus.ACCP.name())));
        when(dataService.mapToGlobalResponse(any(), any())).thenReturn(getSelectMethodResponse());
        when(cmsPsuPisService.updateAuthorisationStatus(any(), anyString(), anyString(), any(), anyString(), any())).thenThrow(AuthorisationIsExpiredException.class);
        PaymentWorkflow workflow = getExpectedWorkflow(null);
        // Then
        assertThrows(ObaException.class, () -> service.initiatePaymentOpr(workflow, PSU_ID, OpTypeTO.PAYMENT));
    }

    @Test
    void initiateCancelPayment() {
        // Given
        when(paymentRestClient.initiatePmtCancellation(anyString())).thenReturn(ResponseEntity.ok(getSelectMethodResponse(TransactionStatus.ACCP.name())));
        when(dataService.mapToGlobalResponse(any(), any())).thenReturn(getSelectMethodResponse());
        // When
        PaymentWorkflow result = service.initiatePaymentOpr(getExpectedWorkflow(null), PSU_ID, OpTypeTO.CANCEL_PAYMENT);

        // Then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getExpectedWorkflow(TransactionStatus.ACCP.name()));
    }

    @Test
    void authorizePayment() {
        // Given
        when(paymentRestClient.executePayment(anyString())).thenReturn(ResponseEntity.ok(getSelectMethodResponse(ACSC.name())));
        when(redirectScaClient.validateScaCode(any(), any())).thenReturn(ResponseEntity.ok(getSelectMethodResponse()));

        // When
        PaymentWorkflow result = service.authorizePaymentOpr(getExpectedWorkflow(null), PSU_ID, AUTH_CODE, OpTypeTO.PAYMENT);

        // Then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getExpectedWorkflow(ACSC.name()));
    }

    @Test
    void authorizeCancelPayment() {
        // Given
        when(paymentRestClient.executeCancelPayment(anyString())).thenReturn(ResponseEntity.ok(getSelectMethodResponse(ACSC.name())));
        when(redirectScaClient.validateScaCode(any(), any())).thenReturn(ResponseEntity.ok(getSelectMethodResponse()));

        // When
        PaymentWorkflow result = service.authorizePaymentOpr(getExpectedWorkflow(null), PSU_ID, AUTH_CODE, OpTypeTO.CANCEL_PAYMENT);

        // Then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getExpectedWorkflow(CANC.name()));
    }

    private PaymentWorkflow getExpectedWorkflow(String status) {
        PaymentWorkflow workflow = new PaymentWorkflow(getCmsPaymentResponse().get(), getConsentReference());
        workflow.setScaResponse(getSelectMethodResponse());
        workflow.setAuthResponse(getAuthResponse(ACCP));
        workflow.setPaymentStatus(status);
        Optional.ofNullable(status).map(TransactionStatusTO::valueOf).map(this::getAuthResponse).ifPresent(workflow::setAuthResponse);
        return workflow;
    }

    private PaymentWorkflow getExpectedIdentifyWorkflow(String status) {
        PaymentWorkflow workflow = new PaymentWorkflow(getCmsPaymentResponse().get(), getConsentReference());
        GlobalScaResponseTO to = new GlobalScaResponseTO();
        to.setAuthorisationId(null);
        to.setBearerToken(new BearerTokenTO());
        workflow.setScaResponse(to);
        workflow.setAuthResponse(getAuthResponse(ACCP));
        workflow.setPaymentStatus(status);
        Optional.ofNullable(status).map(TransactionStatusTO::valueOf).map(this::getAuthResponse).ifPresent(workflow::setAuthResponse);
        return workflow;
    }

    private PaymentAuthorizeResponse getAuthResponse(TransactionStatusTO status) {
        return new PaymentAuthorizeResponse(getPaymentTO(status));
    }

    private SCAPaymentResponseTO getSelectMethodResponse(String status) {
        SCAPaymentResponseTO response = new SCAPaymentResponseTO();
        Optional.ofNullable(status).map(TransactionStatusTO::valueOf).ifPresent(response::setTransactionStatus);
        response.setScaStatus(ScaStatusTO.SCAMETHODSELECTED);
        response.setBearerToken(getBearer());
        response.setAuthorisationId(AUTH_ID);
        return response;
    }

    private BearerTokenTO getBearer() {
        return new BearerTokenTO(null, null, 0, null, new AccessTokenTO(), new HashSet<>());
    }

    private GlobalScaResponseTO getSelectMethodResponse() {
        GlobalScaResponseTO response = new GlobalScaResponseTO();
        response.setScaStatus(ScaStatusTO.SCAMETHODSELECTED);
        response.setBearerToken(new BearerTokenTO());
        response.setAuthorisationId(AUTH_ID);
        return response;
    }

    private PaymentTO getPaymentTO(TransactionStatusTO status) {
        PaymentTO payment = new PaymentTO();
        payment.setPaymentId(PAYMENT_ID);
        payment.setPaymentType(PaymentTypeTO.SINGLE);
        payment.setPaymentProduct("sepa");
        payment.setTransactionStatus(status);
        return payment;
    }

    private Optional<CmsPaymentResponse> getCmsPaymentResponse() {
        return Optional.of(new CmsPaymentResponse(getCommonPayment(), AUTH_ID, OK_REDIRECT_URI, NOK_REDIRECT_URI));
    }

    private CmsCommonPayment getCommonPayment() {
        CmsCommonPayment payment = new CmsCommonPayment("sepa");
        payment.setPaymentId(PAYMENT_ID);
        payment.setPaymentData(getPaymentString());
        payment.setPaymentType(PaymentType.SINGLE);
        payment.setTransactionStatus(TransactionStatus.ACCP);
        return payment;
    }

    private ConsentReference getConsentReference() {
        ConsentReference cr = new ConsentReference();
        cr.setConsentType(ConsentType.AIS);
        cr.setRedirectId(AUTH_ID);
        cr.setEncryptedConsentId(ENCRYPTED_ID);
        cr.setAuthorizationId(AUTH_ID);
        cr.setCookieString(null);
        return cr;
    }

    private byte[] getPaymentString() {
        String s = "{\n" +
                       "  \"endToEndIdentification\": \"WBG-123456789\",\n" +
                       "  \"requestedExecutionDate\": \"2019-12-12\",\n" +
                       "  \"debtorAccount\": {\n" +
                       "    \"currency\": \"USD\",\n" +
                       "    \"iban\": \"DE40500105178578796457\"\n" +
                       "  },\n" +
                       "  \"instructedAmount\": {\n" +
                       "    \"currency\": \"CHF\",\n" +
                       "    \"amount\": \"1.00\"\n" +
                       "  },\n" +
                       "  \"creditorAccount\": {\n" +
                       "    \"currency\": \"EUR\",\n" +
                       "    \"iban\": \"DE40500105178578796457\"\n" +
                       "  },\n" +
                       "  \"creditorAgent\" : \"AAAADEBBXXX\",\n" +
                       "  \"creditorName\": \"WBG\",\n" +
                       "  \"creditorAddress\": {\n" +
                       "    \"buildingNumber\": \"56\",\n" +
                       "    \"townName\": \"Nürnberg\",\n" +
                       "    \"country\": \"DE\",\n" +
                       "    \"postCode\": \"90543\",\n" +
                       "    \"streetName\": \"WBG Straße\"\n" +
                       "  },\n" +
                       "  \"remittanceInformationUnstructured\": \"Ref. Number WBG-1222\"\n" +
                       "}\n";
        return s.getBytes();
    }

    private Optional<CmsPsuAuthorisation> geCmsPsuAuth(ScaStatus status) {
        CmsPsuAuthorisation auth = new CmsPsuAuthorisation();
        auth.setScaStatus(status);
        return Optional.of(auth);
    }
}

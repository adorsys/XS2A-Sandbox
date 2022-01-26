/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.rest.client.ObaPisApiClient;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.xs2a.client.PaymentApiClient;
import de.adorsys.psd2.model.PaymentInitationRequestResponse201;
import de.adorsys.psd2.model.PaymentInitiationStatusResponse200Json;
import de.adorsys.psd2.model.TransactionStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.util.UUID;

import static de.adorsys.ledgers.xs2a.test.ctk.embedded.LinkResolver.getLink;

public class PaymentExecutionHelper {

    private final String digest = null;
    private final String signature = null;
    private final byte[] tpPSignatureCertificate = null;
    private final String psUIDType = null;
    private final String psUCorporateID = null;
    private final String psUCorporateIDType = null;
    private final String psUIPAddress = "127.0.0.1";
    private final String psUIPPort = null;
    private final String psUAccept = null;
    private final String psUAcceptCharset = null;
    private final String psUAcceptEncoding = null;
    private final String psUAcceptLanguage = null;
    private final String psUUserAgent = null;
    private final String psUHttpMethod = null;
    private final UUID psUDeviceID = UUID.randomUUID();
    private final String psUGeoLocation = null;

    private final PaymentApiClient paymentApi;
    private final ObaPisApiClient pisApiClient;
    private final PaymentCase paymentCase;
    private final String paymentService;
    private final String paymentProduct;


    public PaymentExecutionHelper(PaymentApiClient paymentApi, ObaPisApiClient pisApiClient, PaymentCase paymentCase,
                                  String paymentService, String paymentProduct) {
        super();
        this.paymentApi = paymentApi;
        this.pisApiClient = pisApiClient;
        this.paymentCase = paymentCase;
        this.paymentService = paymentService;
        this.paymentProduct = paymentProduct;
    }

    public PaymentInitationRequestResponse201 initiatePayment() {
        Object payment = paymentCase.getPayment();
        UUID xRequestID = UUID.randomUUID();
        String PSU_ID = paymentCase.getPsuId();
        String consentID = null;
        String tpPRedirectPreferred = "true";
        String tpPRedirectURI = "http://localhost:8080/tpp/ok";
        String tpPNokRedirectURI = "http://localhost:8080/tpp/nok";
        Boolean tpPExplicitAuthorisationPreferred = true;
        PaymentInitationRequestResponse201 initiatedPayment = paymentApi._initiatePayment(payment, xRequestID, psUIPAddress,
            paymentService, paymentProduct, digest, signature, tpPSignatureCertificate, PSU_ID, psUIDType,
            psUCorporateID, psUCorporateIDType, consentID, tpPRedirectPreferred, tpPRedirectURI, tpPNokRedirectURI,
            tpPExplicitAuthorisationPreferred, psUIPPort, psUAccept, psUAcceptCharset, psUAcceptEncoding,
            psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation).getBody();

        Assert.assertNotNull(initiatedPayment);
        Assert.assertNotNull(getLink(initiatedPayment.getLinks(), "scaRedirect"));

        Assert.assertNotNull(initiatedPayment.getPaymentId());
        Assert.assertNotNull(initiatedPayment.getTransactionStatus());
        Assert.assertEquals("RCVD", initiatedPayment.getTransactionStatus().name());
        Assert.assertNotNull(initiatedPayment.getPaymentId());

        return initiatedPayment;
    }

    static class RedirectedParams {
        private final String redirectId;
        private final String encryptedPaymentId;

        public RedirectedParams(String scaRedirect) {
            encryptedPaymentId = StringUtils.substringBetween(scaRedirect, "paymentId=", "&redirectId=");
            redirectId = StringUtils.substringAfter(scaRedirect, "&redirectId=");
        }

        public String getRedirectId() {
            return redirectId;
        }

        public String getEncryptedPaymentId() {
            return encryptedPaymentId;
        }
    }

    public ResponseEntity<PaymentAuthorizeResponse> login(PaymentInitationRequestResponse201 initiatedPayment) throws MalformedURLException {
        String scaRedirectLink = getLink(initiatedPayment.getLinks(), "scaRedirect");
        String encryptedPaymentId = initiatedPayment.getPaymentId();
        String redirectId = QuerryParser.param(scaRedirectLink, "redirectId");
        String encryptedPaymentIdFromOnlineBanking = QuerryParser.param(scaRedirectLink, "paymentId");
        Assert.assertEquals(encryptedPaymentId, encryptedPaymentIdFromOnlineBanking);
        String authorisationId = redirectId;
        ResponseEntity<PaymentAuthorizeResponse> loginResponse = pisApiClient.login(encryptedPaymentId, authorisationId, paymentCase.getPsuId(), "12345");
        Assert.assertNotNull(loginResponse);
        Assert.assertTrue(loginResponse.getStatusCode().is2xxSuccessful());
        return loginResponse;
    }

    public ResponseEntity<PaymentInitiationStatusResponse200Json> loadPaymentStatus(String encryptedPaymentId) {
        UUID xRequestID = UUID.randomUUID();
        ResponseEntity<PaymentInitiationStatusResponse200Json> _getPaymentInitiationStatus = paymentApi
                                                                                                 ._getPaymentInitiationStatus(paymentService, paymentProduct, encryptedPaymentId, xRequestID, digest, signature,
                                                                                                     tpPSignatureCertificate, psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset,
                                                                                                     psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod, psUDeviceID, psUGeoLocation);

        PaymentInitiationStatusResponse200Json paymentInitiationStatus = _getPaymentInitiationStatus.getBody();

        Assert.assertNotNull(paymentInitiationStatus);

        return _getPaymentInitiationStatus;
    }

    public ResponseEntity<PaymentAuthorizeResponse> authCode(ResponseEntity<PaymentAuthorizeResponse> paymentResponse) {
        Assert.assertNotNull(paymentResponse);
        Assert.assertTrue(paymentResponse.getStatusCode().is2xxSuccessful());
        PaymentAuthorizeResponse paymentAuthorizeResponse = paymentResponse.getBody();
        ResponseEntity<PaymentAuthorizeResponse> authrizedPaymentResponse =
            pisApiClient.authrizedPayment(paymentAuthorizeResponse.getEncryptedConsentId(), paymentAuthorizeResponse.getAuthorisationId(), "123456");
        Assert.assertNotNull(authrizedPaymentResponse);
        Assert.assertTrue(authrizedPaymentResponse.getStatusCode().is2xxSuccessful());
        return authrizedPaymentResponse;
    }

    public ResponseEntity<PaymentAuthorizeResponse> choseScaMethod(ResponseEntity<PaymentAuthorizeResponse> paymentResponse) {
        Assert.assertNotNull(paymentResponse);
        Assert.assertTrue(paymentResponse.getStatusCode().is2xxSuccessful());
        PaymentAuthorizeResponse paymentAuthorizeResponse = paymentResponse.getBody();
        ScaUserDataTO scaUserDataTO = paymentAuthorizeResponse.getScaMethods().iterator().next();
        ResponseEntity<PaymentAuthorizeResponse> authrizedPaymentResponse = pisApiClient.selectMethod(paymentAuthorizeResponse.getEncryptedConsentId(), paymentAuthorizeResponse.getAuthorisationId(),
            scaUserDataTO.getId());
        Assert.assertNotNull(authrizedPaymentResponse);
        Assert.assertTrue(authrizedPaymentResponse.getStatusCode().is2xxSuccessful());
        return authrizedPaymentResponse;
    }

    public void checkTxStatus(String paymentId, TransactionStatus expectedStatus) {
        ResponseEntity<PaymentInitiationStatusResponse200Json> loadPaymentStatusResponseWrapper;
        loadPaymentStatusResponseWrapper = loadPaymentStatus(paymentId);
        PaymentInitiationStatusResponse200Json loadPaymentStatusResponse = loadPaymentStatusResponseWrapper.getBody();
        Assert.assertNotNull(loadPaymentStatusResponse);
        TransactionStatus currentStatus = loadPaymentStatusResponse.getTransactionStatus();
        Assert.assertNotNull(currentStatus);
        Assert.assertEquals(expectedStatus, currentStatus);
    }

    public void validateResponseStatus(PaymentAuthorizeResponse authResponse, ScaStatusTO expectedScaStatus, TransactionStatusTO expectedPaymentStatus) {
        ScaStatusTO scaStatus = authResponse.getScaStatus();
        Assert.assertNotNull(scaStatus);
        Assert.assertEquals(expectedScaStatus, scaStatus);
        Assert.assertEquals(expectedPaymentStatus, authResponse.getPayment().getTransactionStatus());
    }

}

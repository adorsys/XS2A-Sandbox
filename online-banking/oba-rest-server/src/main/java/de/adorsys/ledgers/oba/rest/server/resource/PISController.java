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

package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.rest.api.resource.PISApi;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static de.adorsys.ledgers.oba.rest.api.resource.PISApi.BASE_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_PATH)
@Api(value = BASE_PATH, tags = "PSU PIS. Provides access to online banking payment functionality")
public class PISController implements PISApi {
    private final CommonPaymentService paymentService;
    private final XISControllerService xisService;
    private final HttpServletResponse response;
    private final ResponseUtils responseUtils;
    private final MiddlewareAuthentication middlewareAuth;
    private final AuthRequestInterceptor authInterceptor;
    private final TokenAuthenticationService authenticationService;
    private final AccountRestClient accountRestClient;

    @Override
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    public ResponseEntity<PaymentAuthorizeResponse> login(String encryptedPaymentId, String authorisationId, String login, String pin) {
        PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, null);
        xisService.checkFailedCount(encryptedPaymentId);

        // Authorize
        try {
            GlobalScaResponseTO ledgersResponse = authenticationService.login(login, pin, authorisationId);
            workflow.processSCAResponse(ledgersResponse);
            AuthUtils.checkIfUserInitiatedOperation(ledgersResponse, workflow.getPaymentResponse().getPayment().getPsuIdDatas());
        } catch (FeignException | ObaException e) {
            xisService.resolveFailedLoginAttempt(encryptedPaymentId, workflow.paymentId(), login, workflow.authId(), OpTypeTO.PAYMENT);
        }

        return xisService.resolvePaymentWorkflow(workflow);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> initiatePayment(String encryptedPaymentId, String authorisationId, AccountReferenceTO accountReference) {

        String psuId = AuthUtils.psuId(middlewareAuth);
        // Identity the link and load the workflow.
        PaymentWorkflow identifyPaymentWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, middlewareAuth.getBearerToken());

        if (StringUtils.isNotBlank(accountReference.getIban())) {
            identifyPaymentWorkflow.getAuthResponse().getPayment().setDebtorAccount(accountReference);
        }

        PaymentWorkflow initiatePaymentWorkflow = paymentService.initiatePaymentOpr(identifyPaymentWorkflow, psuId, OpTypeTO.PAYMENT);

        // Store result in token.
        responseUtils.addAccessTokenHeader(response, initiatePaymentWorkflow.bearerToken().getAccess_token());
        return ResponseEntity.ok(initiatePaymentWorkflow.getAuthResponse());
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> selectMethod(String encryptedPaymentId, String authorisationId, String scaMethodId) {
        return xisService.selectScaMethod(encryptedPaymentId, authorisationId, scaMethodId);
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> authrizedPayment(String encryptedPaymentId, String authorisationId, String authCode) {
        String psuId = AuthUtils.psuId(middlewareAuth);
        try {
            PaymentWorkflow identifyPaymentWorkflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, middlewareAuth.getBearerToken());
            PaymentWorkflow authorizePaymentWorkflow = paymentService.authorizePaymentOpr(identifyPaymentWorkflow, psuId, authCode, OpTypeTO.PAYMENT);

            responseUtils.addAccessTokenHeader(response, authorizePaymentWorkflow.bearerToken().getAccess_token());
            log.info("Confirmation code: {}", authorizePaymentWorkflow.getAuthResponse().getAuthConfirmationCode());
            return ResponseEntity.ok(authorizePaymentWorkflow.getAuthResponse());
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> failPaymentAuthorisation(String encryptedPaymentId, String authorisationId) {
        try {
            PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, middlewareAuth.getBearerToken());

            authInterceptor.setAccessToken(workflow.bearerToken().getAccess_token());

            workflow.getScaResponse().setScaStatus(ScaStatusTO.FAILED);
            paymentService.updateAspspConsentData(workflow);

            return ResponseEntity.ok(workflow.getAuthResponse());
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

    @Override
    public ResponseEntity<PaymentAuthorizeResponse> pisDone(String encryptedPaymentId, String authorisationId, boolean isOauth2Integrated, String authConfirmationCode) {
        PaymentWorkflow workflow = paymentService.identifyPayment(encryptedPaymentId, authorisationId, middlewareAuth.getBearerToken());

        String psuId = AuthUtils.psuId(middlewareAuth);
        String redirectUrl = paymentService.resolveRedirectUrl(encryptedPaymentId, authorisationId, isOauth2Integrated, psuId, middlewareAuth.getBearerToken(), authConfirmationCode);
        PaymentAuthorizeResponse authResponse = workflow.getAuthResponse();
        authResponse.setRedirectUrl(redirectUrl);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    public ResponseEntity<List<AccountDetailsTO>> getAccountList() {
        return ResponseEntity.ok(getListOfAccounts());
    }

    private List<AccountDetailsTO> getListOfAccounts() {
        try {
            authInterceptor.setAccessToken(middlewareAuth.getBearerToken().getAccess_token());
            return accountRestClient.getListOfAccounts().getBody();
        } finally {
            authInterceptor.setAccessToken(null);
        }
    }

}

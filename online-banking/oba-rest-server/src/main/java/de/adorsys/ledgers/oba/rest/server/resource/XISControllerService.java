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

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PaymentWorkflow;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.CmsAspspConsentDataService;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.psd2.sandbox.auth.SecurityConstant;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.EnumSet;
import java.util.Optional;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.*;
import static de.adorsys.psd2.consent.aspsp.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;
@Slf4j
@Service
@RequiredArgsConstructor
public class XISControllerService {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ResponseUtils responseUtils;
    private final CommonPaymentService paymentService;
    private final MiddlewareAuthentication middlewareAuth;
    private final CmsAspspConsentDataService consentDataService;
    private final CmsPsuAisClient cmsPsuAisClient;
    private final CmsPsuPisClient cmsPsuPisClient;

    @Value("${online-banking.sca.loginpage:http://localhost:4400/}")
    private String loginPage;

    /**
     * The purpose of this protocol step is to parse the redirect link and start
     * the user agent.
     * <p>
     * The user agent is defined by providing the URL read from the property online-banking.sca.loginpage.
     * <p>
     * A 302 redirect will be performed to that URL by default. But if the target user agent does not
     * which for a redirect, it can set the NO_REDIRECT_HEADER_PARAM to true/on.
     *
     * @param redirectId         redirectId
     * @param consentType        consentType
     * @param encryptedConsentId encryptedConsentId
     * @param response           Servlet Response
     * @return AuthorizeResponse
     */
    public ResponseEntity<AuthorizeResponse> auth(String redirectId, ConsentType consentType, String encryptedConsentId, HttpServletResponse response) {

        // This auth response carries information we want to passe directly to the calling user agent.
        // In this case:
        // - The encrypted consent id used to identify the consent.
        // - The redirectId use to identify this redirect instance.
        // We would like the user agent to return with both information so we can match them again the
        // one we stored in the consent cookie.
        AuthorizeResponse authResponse = new AuthorizeResponse();
        // 1. Store redirect link in a cookie
        authResponse.setEncryptedConsentId(encryptedConsentId);
        authResponse.setAuthorisationId(redirectId);
        String token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
            .filter(t -> StringUtils.startsWithIgnoreCase(t, SecurityConstant.BEARER_TOKEN_PREFIX))
            .map(t -> StringUtils.substringAfter(t, SecurityConstant.BEARER_TOKEN_PREFIX))
                           .orElse(null);

        if (StringUtils.isNotBlank(token)) {
            response.addHeader(HttpHeaders.AUTHORIZATION, token);
        }

        // This is the link we are expecting from the loaded agent.
        String uriString = UriComponentsBuilder.fromUriString(loginPage)
                               .queryParam("encryptedConsentId", authResponse.getEncryptedConsentId())
                               .queryParam("authorisationId", authResponse.getAuthorisationId())
                               .build().toUriString();

        // This header tels is we shall send back a 302 or a 200 back to the user agent.
        // Header shall be set by the user agent.
        response.addHeader("Location", uriString);
        return ResponseEntity.ok(authResponse);
    }

    public ResponseEntity<PaymentAuthorizeResponse> resolvePaymentWorkflow(PaymentWorkflow workflow) {
        ScaStatusTO scaStatusTO = workflow.scaStatus();
        if (EnumSet.of(PSUIDENTIFIED, FINALISED, EXEMPTED, PSUAUTHENTICATED, SCAMETHODSELECTED).contains(scaStatusTO)) {
            responseUtils.addAccessTokenHeader(response, workflow.bearerToken().getAccess_token());
            return ResponseEntity.ok(workflow.getAuthResponse());
        }// failed Message. No repeat.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public ResponseEntity<PaymentAuthorizeResponse> selectScaMethod(String encryptedPaymentId, String authorisationId, String scaMethodId) {
        PaymentWorkflow workflow = paymentService.selectScaForPayment(encryptedPaymentId, authorisationId, scaMethodId, AuthUtils.psuId(middlewareAuth), middlewareAuth.getBearerToken());
        responseUtils.addAccessTokenHeader(response, workflow.bearerToken().getAccess_token());
        return ResponseEntity.ok(workflow.getAuthResponse());
    }

    public void checkFailedCount(String encryptedId) {
        if (consentDataService.isFailedLogin(encryptedId)) {
            throw ObaException.builder()
                .devMessage("You have exceeded maximum login attempts for current Authorization!")
                .obaErrorCode(ObaErrorCode.AUTH_EXPIRED)
                .build();
        }
    }

    public void resolveFailedLoginAttempt(String encryptedId, String id, String login, String authId, OpTypeTO opType) {
        int attemptsLeft = consentDataService.updateLoginFailedCount(encryptedId);
        if (attemptsLeft < 1) {
            if (opType == OpTypeTO.CONSENT) {
                failConsentAuthorization(id, login, authId);
            } else {
                failPaymentAuthorisation(id, login, authId);
            }
        }
        String msg = attemptsLeft > 0
                         ? String.format("You have %s attempts left", attemptsLeft)
                         : "You've exceeded login attempts limit for current session. Please open new Authorization session";
        throw ObaException.builder()
                  .devMessage(String.format("Login Failed!%n %s", msg))
                  .obaErrorCode(ObaErrorCode.LOGIN_FAILED)
                  .build();
    }

    private void failConsentAuthorization(String id, String login, String authId) {
        cmsPsuAisClient.updateAuthorisationStatus(id,
                                                  "FAILED", authId, login, null, null, null,
                                                  DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, null));
    }

    private void failPaymentAuthorisation(String id, String login, String authId) {
        cmsPsuPisClient.updateAuthorisationStatus(login,
                                                  null, null, null, id, authId, "FAILED",
                                                  DEFAULT_SERVICE_INSTANCE_ID, new AuthenticationDataHolder(null, null));

    }
}

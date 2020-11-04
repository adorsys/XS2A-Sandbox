package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.rest.api.resource.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.*;
import de.adorsys.ledgers.oba.service.api.domain.exception.InvalidConsentException;
import de.adorsys.ledgers.oba.service.api.service.CommonPaymentService;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import static de.adorsys.ledgers.oba.rest.server.auth.oba.SecurityConstant.BEARER_TOKEN_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class XISControllerService {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ConsentReferencePolicy referencePolicy;
    private final ResponseUtils responseUtils;
    private final KeycloakTokenService tokenService;
    private final CommonPaymentService paymentService;
    private final ObaMiddlewareAuthentication middlewareAuth;

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
        try {
            ConsentReference consentReference = referencePolicy.fromURL(redirectId, consentType, encryptedConsentId);
            authResponse.setEncryptedConsentId(encryptedConsentId);
            authResponse.setAuthorisationId(redirectId);
            String token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                               .filter(t -> StringUtils.startsWithIgnoreCase(t, BEARER_TOKEN_PREFIX))
                               .map(t -> StringUtils.substringAfter(t, BEARER_TOKEN_PREFIX))
                               .orElse(null);
            // 2. Set cookies
            AccessTokenTO tokenTO = Optional.ofNullable(token).map(tokenService::validate)
                                        .map(BearerTokenTO::getAccessTokenObject)
                                        .orElse(null);
            responseUtils.setCookies(response, consentReference, token, tokenTO);
            if (StringUtils.isNotBlank(token)) {
                response.addHeader(HttpHeaders.AUTHORIZATION, token);
            }
        } catch (InvalidConsentException e) {
            log.info(e.getMessage());
            responseUtils.removeCookies(response);
            return responseUtils.unknownCredentials(authResponse, response);
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
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        }// failed Message. No repeat. Delete cookies.
        responseUtils.removeCookies(response);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public ResponseEntity<PaymentAuthorizeResponse> selectScaMethod(String encryptedPaymentId, String authorisationId, String scaMethodId, String consentAndaccessTokenCookieString) {
        PaymentWorkflow workflow;
        try {
            String consentCookie = responseUtils.consentCookie(consentAndaccessTokenCookieString);
            workflow = paymentService.selectScaForPayment(encryptedPaymentId, authorisationId, scaMethodId, consentCookie, AuthUtils.psuId(middlewareAuth), middlewareAuth.getBearerToken());
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
        } catch (PaymentAuthorizeException p) {
            return p.getError();
        }
        return ResponseEntity.ok(workflow.getAuthResponse());
    }
}

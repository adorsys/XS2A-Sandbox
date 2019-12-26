package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.*;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.domain.exception.InvalidConsentException;
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.EnumSet;
import java.util.Optional;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.*;
import static de.adorsys.ledgers.oba.rest.server.auth.oba.SecurityConstant.BEARER_TOKEN_PREFIX;
import static de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode.LOGIN_FAILED;

@Slf4j
public abstract class AbstractXISController {
    private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";

    @Autowired
    protected AspspConsentDataClient aspspConsentDataClient;
    @Autowired
    protected TokenStorageService tokenStorageService;
    @Autowired
    protected TokenAuthenticationService tokenAuthenticationService;
    @Autowired
    protected AuthRequestInterceptor authInterceptor;

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected ObaMiddlewareAuthentication middlewareAuth;
    @Autowired
    protected UserMgmtRestClient userMgmtRestClient;

    @Value("${online-banking.sca.loginpage:http://localhost:4400/}")
    private String loginPage;

    @Autowired
    protected ConsentReferencePolicy referencePolicy;

    @Autowired
    protected ResponseUtils responseUtils;

    public abstract String getBasePath();

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
    protected ResponseEntity<AuthorizeResponse> auth(String redirectId, ConsentType consentType, String encryptedConsentId, HttpServletResponse response) {

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
            AccessTokenTO tokenTO = Optional.ofNullable(token).map(t -> userMgmtRestClient.validate(t).getBody())
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

    //TODO consider refactoring
    protected ResponseEntity<SCALoginResponseTO> performLoginForConsent(String login, String pin, String operationId, String authId, OpTypeTO operationType) {
        Cookie cookie = WebUtils.getCookie(request, ACCESS_TOKEN_COOKIE);
        String token = cookie != null
                           ? cookie.getValue()
                           : null;
        return performLoginForConsent(login, pin, token, operationId, authId, operationType);
    }

    private ResponseEntity<SCALoginResponseTO> performLoginForConsent(String login, String pin, String token, String operationId, String authId, OpTypeTO operationType) {
        if (StringUtils.isNotBlank(token)) {
            authInterceptor.setAccessToken(token);
            return userMgmtRestClient.authoriseForConsent(operationId, authId, operationType);
        } else if (StringUtils.isNotBlank(login) || StringUtils.isNotBlank(pin)) {
            return userMgmtRestClient.authoriseForConsent(login, pin, operationId, authId, operationType);
        }
        throw AuthorizationException.builder()
                  .errorCode(LOGIN_FAILED)
                  .devMessage("Login or pin is missing.")
                  .build();
    }

    protected ResponseEntity<PaymentAuthorizeResponse> resolvePaymentWorkflow(PaymentWorkflow workflow) {
        ScaStatusTO scaStatusTO = workflow.scaStatus();
        if (EnumSet.of(PSUIDENTIFIED, FINALISED, EXEMPTED, PSUAUTHENTICATED, SCAMETHODSELECTED).contains(scaStatusTO)) {
            responseUtils.setCookies(response, workflow.getConsentReference(), workflow.bearerToken().getAccess_token(), workflow.bearerToken().getAccessTokenObject());
            return ResponseEntity.ok(workflow.getAuthResponse());
        }// failed Message. No repeat. Delete cookies.
        responseUtils.removeCookies(response);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

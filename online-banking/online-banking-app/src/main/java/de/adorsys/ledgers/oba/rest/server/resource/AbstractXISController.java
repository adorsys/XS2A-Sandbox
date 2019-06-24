package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.service.TokenStorageService;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public abstract class AbstractXISController {

    @Autowired
    protected AspspConsentDataClient aspspConsentDataClient;

    @Autowired
    protected TokenStorageService tokenStorageService;

    @Autowired
    protected AuthRequestInterceptor authInterceptor;

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected MiddlewareAuthentication auth;

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
     * @param redirectId
     * @param consentType
     * @param encryptedConsentId
     * @param request
     * @param response
     * @return
     */
    protected ResponseEntity<AuthorizeResponse> auth(
        String redirectId,
        ConsentType consentType,
        String encryptedConsentId,
        HttpServletRequest request,
        HttpServletResponse response) {

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
            // 2. Set cookies
            responseUtils.setCookies(response, consentReference, null, null);
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
//		String noRedirect = request.getHeader("X-NO-REDIRECT");
        response.addHeader("Location", uriString);
        return ResponseEntity.ok(authResponse);
    }
}

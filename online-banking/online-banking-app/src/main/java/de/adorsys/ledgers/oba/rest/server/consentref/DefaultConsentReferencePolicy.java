package de.adorsys.ledgers.oba.rest.server.consentref;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentType;
import de.adorsys.ledgers.oba.rest.api.consentref.InvalidConsentException;
import de.adorsys.ledgers.util.Ids;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.util.Date;

@Slf4j
public class DefaultConsentReferencePolicy implements ConsentReferencePolicy {
    private static final String CONSENT_TYPE_JWT_CLAIM_NAME = "consent-type";
    private static final String REDIRECT_ID_JWT_CLAIM_NAME = "redirect-id";
    private static final String ENC_CONSENT_ID_JWT_CLAIM_NAME = "enc-consent-id";
    private static final String AUTH_ID_JWT_CLAIM_NAME = "auth-id";

    @Value("${online-banking.sca.jwt.hs256.secret}")
    private String hmacSecret;

    @Override
    public ConsentReference fromURL(String redirectId, ConsentType consentType, String encryptedConsentId) {
        ConsentReference cr = new ConsentReference();
        cr.setRedirectId(redirectId);
        cr.setConsentType(consentType);
        cr.setEncryptedConsentId(encryptedConsentId);
        String cookieString = toClaim(cr);
        cr.setCookieString(cookieString);
        return cr;
    }


    @Override
    public ConsentReference fromRequest(String encryptedConsentId, String authorizationId, String cookieString, boolean strict) throws InvalidConsentException {
        return verifyParseJWT(encryptedConsentId, authorizationId, cookieString, strict);
    }

    private String toClaim(ConsentReference ref) {
        Date now = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                                     // Can be used for CSRF check.
                                     .jwtID(Ids.id())
                                     .claim(REDIRECT_ID_JWT_CLAIM_NAME, ref.getRedirectId())
                                     .claim(CONSENT_TYPE_JWT_CLAIM_NAME, ref.getConsentType().name())
                                     .claim(ENC_CONSENT_ID_JWT_CLAIM_NAME, ref.getEncryptedConsentId())
                                     .claim(AUTH_ID_JWT_CLAIM_NAME, ref.getAuthorizationId())
                                     .expirationTime(DateUtils.addSeconds(now, 300)).issueTime(now)
                                     .build();
        return signJWT(claimsSet);
    }

    private String signJWT(JWTClaimsSet claimsSet) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).keyID(Ids.id()).build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        try {
            signedJWT.sign(new MACSigner(hmacSecret));
        } catch (JOSEException e) {
            throw new IllegalStateException("Error signing user token", e);
        }
        return signedJWT.serialize();
    }

    /*
     * Verify the consent jwt. If strict is true, the jwt must contain the encryptedConsentId and the authorizationId.
     */
    @SuppressWarnings("PMD")
    private ConsentReference verifyParseJWT(String encryptedConsentId, String authorizationId, String cookieString, boolean strict) throws InvalidConsentException {
        Date refTime = new Date();
        try {
            SignedJWT jwt = SignedJWT.parse(cookieString);
            JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();

            // Validate xsrf
            Object authorizationIdClaim = jwtClaimsSet.getClaim(AUTH_ID_JWT_CLAIM_NAME);
            if (strict && authorizationIdClaim == null) {
                throw invalidConsent(String.format("Wrong jwt. CSRF allert. Missing claim %s for jwt with redirectId %s", AUTH_ID_JWT_CLAIM_NAME, jwtClaimsSet.getClaim(REDIRECT_ID_JWT_CLAIM_NAME)));
            }

            if (authorizationIdClaim != null && !StringUtils.equalsIgnoreCase(authorizationIdClaim.toString(), authorizationId)) {
                throw invalidConsent(String.format("Wrong jwt. CSRF allert. Wrong %s for token with redirectId %s", AUTH_ID_JWT_CLAIM_NAME, jwtClaimsSet.getClaim(REDIRECT_ID_JWT_CLAIM_NAME)));
            }

            Object encryptedConsentIdClaim = jwtClaimsSet.getClaim(ENC_CONSENT_ID_JWT_CLAIM_NAME);
            if (encryptedConsentIdClaim == null || !StringUtils.equalsIgnoreCase(encryptedConsentIdClaim.toString(), encryptedConsentId)) {
                throw invalidConsent(String.format("Wrong jwt. CSRF allert. Wrong %s for token with redirectId %s", ENC_CONSENT_ID_JWT_CLAIM_NAME, jwtClaimsSet.getClaim(REDIRECT_ID_JWT_CLAIM_NAME)));
            }

            JWSHeader header = jwt.getHeader();
            // CHeck algorithm
            if (!JWSAlgorithm.HS256.equals(header.getAlgorithm())) {
                throw invalidConsent(String.format("Wrong jws algo for token with subject : %s", jwtClaimsSet.getSubject()));
            }

            // CHeck expiration
            if (jwtClaimsSet.getExpirationTime() == null || jwtClaimsSet.getExpirationTime().before(refTime)) {
                throw invalidConsent(String.format(
                    "Token with subject %s is expired at %s and reference time is %s : ", jwtClaimsSet.getSubject(),
                    jwtClaimsSet.getExpirationTime(), refTime));
            }

            // check signature.
            boolean verified = jwt.verify(new MACVerifier(hmacSecret));
            if (!verified) {
                throw invalidConsent(String.format("Could not verify signature of token with subject %s: ", jwtClaimsSet.getSubject()));
            }

            return consentReference(encryptedConsentId, authorizationId, jwtClaimsSet);

        } catch (ParseException | JOSEException e) {
            // If we can not parse the token, we log the error and return false.
            throw invalidConsent(e.getMessage());
        }
    }


    private ConsentReference consentReference(String encryptedConsentId, String authorizationId, JWTClaimsSet jwtClaimsSet) {
        ConsentReference cr = new ConsentReference();
        cr.setConsentType(ConsentType.valueOf(jwtClaimsSet.getClaim(CONSENT_TYPE_JWT_CLAIM_NAME).toString()));
        cr.setRedirectId(jwtClaimsSet.getClaim(REDIRECT_ID_JWT_CLAIM_NAME).toString());
        cr.setEncryptedConsentId(encryptedConsentId);
        cr.setAuthorizationId(authorizationId);
        cr.setCookieString(toClaim(cr));

        return cr;
    }

    private InvalidConsentException invalidConsent(String message) {
        log.warn(message);
        return new InvalidConsentException(message);
    }
}

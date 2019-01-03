package de.adorsys.ledgers.oba.consentref;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class DefaultConsentReferencePolicy implements ConsentReferencePolicy {
    private static final String CONSENT_TYPE_JWT_CLAIM_NAME = "consent-type";

	private static final String REDIRECT_ID_JWT_CLAIM_NAME = "redirect-id";
	private static final String ENC_CONSENT_ID_JWT_CLAIM_NAME = "enc-consent-id";

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsentReferencePolicy.class);

	@Value("${online-banking.sca.jwt.hs256.secret}")
	private String hmacSecret;

	@Override
	public ConsentReference fromURL(String redirectId, ConsentType consentType, String encryptedConsentId) throws InvalidConsentException{
		ConsentReference cr = new ConsentReference();
		cr.setRedirectId(redirectId);
		cr.setScaId(Ids.id());
		cr.setConsentType(consentType);
		cr.setEncryptedConsentId(encryptedConsentId);
		String cookieString = toClaim(cr);
		cr.setCookieString(cookieString);
		return cr;
	}


	@Override
	public ConsentReference fromRequest(String scaId, String cookieString) throws InvalidConsentException {
		return verifyParseJWT(scaId, cookieString);
	}

	private String toClaim(ConsentReference ref) {
		Date now = new Date();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				// Can be used for CSRF check.
				.jwtID(ref.getScaId())
				.claim(REDIRECT_ID_JWT_CLAIM_NAME, ref.getRedirectId())
				.claim(CONSENT_TYPE_JWT_CLAIM_NAME, ref.getConsentType().name())
				.claim(ENC_CONSENT_ID_JWT_CLAIM_NAME, ref.getEncryptedConsentId())
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

	private ConsentReference verifyParseJWT(String scaId, String cookieString) {
		Date refTime = new Date();
		try {
			SignedJWT jwt = SignedJWT.parse(cookieString);
			JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
			
			// Validate xsrf
			if (!StringUtils.equalsIgnoreCase(scaId, jwtClaimsSet.getJWTID())) {
				logger.warn("Wrong jwt. CSRF allert. for token with subject : " + jwtClaimsSet.getSubject());
				return null;
			}

			JWSHeader header = jwt.getHeader();
			// CHeck algorithm
			if (!JWSAlgorithm.HS256.equals(header.getAlgorithm())) {
				logger.warn("Wrong jws algo for token with subject : " + jwtClaimsSet.getSubject());
				return null;
			}

			// CHeck expiration
			if (jwtClaimsSet.getExpirationTime() == null || jwtClaimsSet.getExpirationTime().before(refTime)) {
				logger.warn(String.format(
						"Token with subject %s is expired at %s and reference time is % : " + jwtClaimsSet.getSubject(),
						jwtClaimsSet.getExpirationTime(), refTime));
				return null;
			}

			// check signature.
			boolean verified = jwt.verify(new MACVerifier(hmacSecret));
			if (!verified) {
				logger.warn("Could not verify signature of token with subject : " + jwtClaimsSet.getSubject());
				return null;
			}

			ConsentReference cr = new ConsentReference();
			cr.setConsentType(ConsentType.valueOf(jwtClaimsSet.getClaim(CONSENT_TYPE_JWT_CLAIM_NAME).toString()));
			cr.setCookieString(cookieString);
			cr.setRedirectId(jwtClaimsSet.getClaim(REDIRECT_ID_JWT_CLAIM_NAME).toString());
			cr.setEncryptedConsentId(jwtClaimsSet.getClaim(ENC_CONSENT_ID_JWT_CLAIM_NAME).toString());
			cr.setScaId(scaId);
			
			return cr;

		} catch (ParseException e) {
			// If we can not parse the token, we log the error and return false.
			logger.warn(e.getMessage());
			return null;
		} catch (JOSEException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
}

package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.rest.client.ObaAisApiClient;
import de.adorsys.ledgers.oba.rest.client.ObaScaApiClient;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.service.api.domain.PIISConsentCreateResponse;
import de.adorsys.ledgers.xs2a.client.FundsConfirmationApiClient;
import de.adorsys.psd2.model.Amount;
import de.adorsys.psd2.model.ConfirmationOfFunds;
import de.adorsys.psd2.model.InlineResponse2003;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

public class PiisHelper {
    private static final String TPP_AUTHORISATION_NUMBER = "PSDDE-FAKENCA-87B2AC";
    private final String digest = null;
    private final String signature = null;
    private final byte[] tpPSignatureCertificate = null;

    private final CookiesUtils cu = new CookiesUtils();

    private final String PSU_ID;
    private final String iban;
    private final FundsConfirmationApiClient fundsConfirmationApiClient;
    private final ObaAisApiClient obaAisApiClient;
    private final ObaScaApiClient obaScaApiClient;

    public PiisHelper(String pSU_ID, String iban, ObaAisApiClient obaAisApiClient,
                      FundsConfirmationApiClient fundsConfirmationApiClient,
                      ObaScaApiClient obaScaApiClient) {
        PSU_ID = pSU_ID;
        this.iban = iban;
        this.obaAisApiClient = obaAisApiClient;
        this.fundsConfirmationApiClient = fundsConfirmationApiClient;
        this.obaScaApiClient = obaScaApiClient;
    }

    public ResponseEntity<PIISConsentCreateResponse> createPiisConsent(ResponseEntity<AuthorizeResponse> authResponse) {

        CreatePiisConsentRequestTO aisConsentTO = dedicatedConsent();

        Assert.assertNotNull(authResponse);
        Assert.assertTrue(authResponse.getStatusCode().is2xxSuccessful());
        List<String> cookieStrings = authResponse.getHeaders().get("Set-Cookie");
        String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
        Assert.assertNotNull(accessTokenCookieString);

        ResponseEntity<PIISConsentCreateResponse> grantPiisConsentResponseWrapper = obaAisApiClient.grantPiisConsent(cu.resetCookies(cookieStrings), aisConsentTO);
        Assert.assertNotNull(grantPiisConsentResponseWrapper);
        Assert.assertEquals(HttpStatus.OK, grantPiisConsentResponseWrapper.getStatusCode());

        return grantPiisConsentResponseWrapper;
    }

    public ResponseEntity<AuthorizeResponse> login() {
        return obaScaApiClient.login(PSU_ID, "12345");
    }

    public ResponseEntity<AuthorizeResponse> checkAccessToken(ResponseEntity<AuthorizeResponse> authResponseWrapper) {
        Assert.assertNotNull(authResponseWrapper);
        Assert.assertTrue(authResponseWrapper.getStatusCode().is2xxSuccessful());
        List<String> cookieStrings = authResponseWrapper.getHeaders().get("Set-Cookie");
        String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
        Assert.assertNotNull(accessTokenCookieString);
        return authResponseWrapper;
    }

    public ResponseEntity<AuthorizeResponse> choseScaMethod(
        ResponseEntity<AuthorizeResponse> authResponseWrapper) {
        Assert.assertNotNull(authResponseWrapper);
        Assert.assertTrue(authResponseWrapper.getStatusCode().is2xxSuccessful());
        List<String> cookieStrings = authResponseWrapper.getHeaders().get("Set-Cookie");
        String consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
        Assert.assertNotNull(consentCookieString);
        String accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
        Assert.assertNotNull(accessTokenCookieString);

        AuthorizeResponse consentAuthorizeResponse = authResponseWrapper.getBody();
        ScaUserDataTO scaUserDataTO = consentAuthorizeResponse.getScaMethods().iterator().next();
        ResponseEntity<AuthorizeResponse> selectMethodResponseWrapper = obaScaApiClient.selectMethod(consentAuthorizeResponse.getEncryptedConsentId(),
            consentAuthorizeResponse.getAuthorisationId(), scaUserDataTO.getId(), cu.resetCookies(cookieStrings));

        Assert.assertNotNull(selectMethodResponseWrapper);
        Assert.assertTrue(selectMethodResponseWrapper.getStatusCode().is2xxSuccessful());
        cookieStrings = selectMethodResponseWrapper.getHeaders().get("Set-Cookie");
        consentCookieString = cu.readCookie(cookieStrings, "CONSENT");
        Assert.assertNotNull(consentCookieString);
        accessTokenCookieString = cu.readCookie(cookieStrings, "ACCESS_TOKEN");
        Assert.assertNotNull(accessTokenCookieString);

        return selectMethodResponseWrapper;
    }

    private CreatePiisConsentRequestTO dedicatedConsent() {
        CreatePiisConsentRequestTO consents = new CreatePiisConsentRequestTO();
        consents.setTppAuthorisationNumber(TPP_AUTHORISATION_NUMBER);
        AccountReference reference = new AccountReference();
        reference.setIban(iban);
        reference.setCurrency(Currency.getInstance("EUR"));
        consents.setAccount(reference);
        consents.setValidUntil(LocalDate.of(2021, 11, 30));
        return consents;
    }

    public ResponseEntity<InlineResponse2003> confOfFund() {
        UUID xRequestID = UUID.randomUUID();
        @Valid
        ConfirmationOfFunds cof = new ConfirmationOfFunds();
        de.adorsys.psd2.model.AccountReference account = new de.adorsys.psd2.model.AccountReference();
        account.setIban(iban);
        account.setCurrency("EUR");
        cof.setAccount(account);

        Amount instructedAmount = new Amount().amount("5").currency("EUR");
        cof.setInstructedAmount(instructedAmount);

        // hmmm
        cof.setCardNumber(iban);
        cof.setPayee("Francis");
        return fundsConfirmationApiClient.checkAvailabilityOfFunds(cof, xRequestID, "authorisationId", digest, signature, tpPSignatureCertificate);
    }

}

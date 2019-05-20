package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.ScaStatus;
import feign.FeignException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public class ConsentRedirectUnknownUser extends AbstractConsentRedirect {
    @Autowired
    private ObjectMapper mapper;

    @Override
    protected String getPsuId() {
        return "user.unknown";
    }

    @Override
    protected String getIban() {
        return "DE80760700240271232400";
    }

    @Test
    public void authenticating_a_user_for_consent_with_wrong_user_name_must_return_401() throws JsonParseException, JsonMappingException, IOException {

        ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createDedicatedConsent();

        ConsentsResponse201 consents = createConsentResp.getBody();
        // Login User
        Assert.assertNotNull(consents);
        ConsentStatus consentStatus = consents.getConsentStatus();
        Assert.assertNotNull(consentStatus);
        Assert.assertEquals(ConsentStatus.RECEIVED, consentStatus);

        try {
            ResponseEntity<ConsentAuthorizeResponse> loginResponseWrapper = consentHelper.login(createConsentResp);
            Assert.fail("Expecting a 401");
            // TODO I have no way to check if the cookie is available. I can see it in the 
            // traces. But i would be nice if we could test this condition applies.
        } catch (FeignException f) {
            Assert.assertEquals(401, f.status());
            ConsentAuthorizeResponse authorizeResponse = mapper.readValue(f.content(), ConsentAuthorizeResponse.class);
            consentHelper.checkConsentScaStatusFromXS2A(authorizeResponse.getEncryptedConsentId(),
                authorizeResponse.getAuthorisationId(), ScaStatus.PSUIDENTIFIED);
        }
    }
}

package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import de.adorsys.ledgers.oba.rest.client.ObaAisApiClient;
import de.adorsys.ledgers.xs2a.client.AccountApiClient;
import de.adorsys.ledgers.xs2a.client.ConsentApiClient;
import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public abstract class AbstractConsentRedirect {

    @Autowired
    private ConsentApiClient consentApi;
    @Autowired
    private ObaAisApiClient obaAisApiClient;
    @Autowired
    private AccountApiClient accountApi;

    protected ConsentHelper consentHelper;

    @Before
    public void before() {
        consentHelper = new ConsentHelper(getPsuId(), getIban(), consentApi, obaAisApiClient, accountApi, getPsuPassword());
    }

    protected String getPsuPassword() {
        return "12345";
    }

    protected abstract String getIban();

    protected abstract String getPsuId();
}

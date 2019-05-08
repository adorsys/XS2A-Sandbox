package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.adorsys.ledgers.xs2a.api.client.AccountApiClient;
import de.adorsys.ledgers.xs2a.api.client.ConsentApiClient;
import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public abstract class AbstractConsentEmbedded {

	@Autowired
	private ConsentApiClient consentApi;
	@Autowired
	private AccountApiClient accountApi;

	protected ConsentHelper consentHelper;
	
	@Before
	public void beforeClass() {
		consentHelper = new ConsentHelper(accountApi, consentApi, getPsuId(), getIban());
	}

	protected abstract String getIban();

	protected abstract String getPsuId();
}

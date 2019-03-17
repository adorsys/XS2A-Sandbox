package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.adorsys.ledgers.oba.rest.client.ObaAisApiClient;
import de.adorsys.ledgers.oba.rest.client.ObaScaApiClient;
import de.adorsys.ledgers.xs2a.api.client.FundsConfirmationApiClient;
import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public abstract class AbstractPiis {

	@Autowired
	private ObaAisApiClient obaAisApiClient;
	@Autowired
	private FundsConfirmationApiClient fundsConfirmationApiClient;
	@Autowired
	private ObaScaApiClient obaScaApiClient;

	protected PiisHelper cifHelper;
	
	@Before
	public void beforeClass() {
		cifHelper = new PiisHelper(getPsuId(), getIban(), obaAisApiClient, fundsConfirmationApiClient, obaScaApiClient);
	}

	protected abstract String getIban();

	protected abstract String getPsuId();
}

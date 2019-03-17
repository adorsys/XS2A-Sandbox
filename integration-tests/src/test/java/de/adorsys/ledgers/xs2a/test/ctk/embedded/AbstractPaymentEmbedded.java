package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import de.adorsys.ledgers.xs2a.api.client.PaymentApiClient;
import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public abstract class AbstractPaymentEmbedded {
	private final YAMLMapper ymlMapper = new YAMLMapper();
	private final String paymentService = "payments";
	private final String paymentProduct = "sepa-credit-transfers";

	@Autowired
	private PaymentApiClient paymentApi;
	
	protected PaymentExecutionHelper paymentInitService;
	
	@Before
	public void beforeClass() {
		PaymentCase paymentCase = LoadPayment.loadPayment(getClass(), getClass().getSimpleName() + ".yml", ymlMapper);
		paymentInitService = new PaymentExecutionHelper(paymentApi, paymentCase, paymentService, paymentProduct);
	}
}

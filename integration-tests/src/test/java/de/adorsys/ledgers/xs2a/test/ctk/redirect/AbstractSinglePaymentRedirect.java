package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import de.adorsys.ledgers.oba.rest.client.ObaPisApiClient;
import de.adorsys.ledgers.xs2a.api.client.PaymentApiClient;
import de.adorsys.ledgers.xs2a.test.ctk.StarterApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StarterApplication.class)
public abstract class AbstractSinglePaymentRedirect {
	private final YAMLMapper ymlMapper = new YAMLMapper();
	private final String paymentService = "payments";
	private final String paymentProduct = "sepa-credit-transfers";

	@Autowired
	private PaymentApiClient paymentApi;
	@Autowired
	private ObaPisApiClient obaPisApiClient;

	protected PaymentExecutionHelper paymentInitService;

	@Before
	public void before() {
		PaymentCase paymentCase = LoadPayment.loadPayment(getClass(),getClass().getSimpleName() + ".yml", ymlMapper);
		paymentInitService = new PaymentExecutionHelper(paymentApi, obaPisApiClient, paymentCase, paymentService, paymentProduct);
	}
}

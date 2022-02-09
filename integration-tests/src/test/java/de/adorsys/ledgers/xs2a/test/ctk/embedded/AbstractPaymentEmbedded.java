/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import de.adorsys.ledgers.xs2a.client.PaymentApiClient;
import de.adorsys.psd2.model.PaymentInitiationJson;
import de.adorsys.psd2.model.PurposeCode;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

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

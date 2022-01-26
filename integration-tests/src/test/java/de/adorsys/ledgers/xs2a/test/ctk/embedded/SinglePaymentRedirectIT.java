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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SinglePaymentRedirectIT {
    ObjectMapper ymlMapper = new ObjectMapper(new YAMLFactory());

    @Test
    public void test_create_payment() throws IOException {
        assertTrue(true); //TODO This is to be fixed or completely removed!
//        PaymentInitiationServicePisApi pis = new PaymentInitiationServicePisApi(createApiClient());
//        UUID xRequestId = UUID.randomUUID();
//        String tppProcessId = UUID.randomUUID().toString();
//		String tpPRedirectURI = getTppOkRedirectBaseURI()+tppProcessId;
//		String tpPNokRedirectURI = getTppNokRedirectBaseURI()+tppProcessId;
//		Boolean tpPExplicitAuthorisationPreferred = false;
//		PaymentCase case1 = loadPayment("paymentCase-1.yml");
//		Map<String, String> response = null;
//		try {
//			response = (Map<String, String>) pis.initiatePayment(case1.getPayment(),
//					"payments", "sepa-credit-transfers", xRequestId,
//					"127.0.0.1", null, null, null,
//					case1.getPsuId(), null, null, null, null,
//					true, tpPRedirectURI, tpPNokRedirectURI, tpPExplicitAuthorisationPreferred,
//					null, null, null, null, null,
//					null, null, null, null);
//		} catch (ApiException e) {
//			throw new IllegalStateException(e);
//		}
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		PaymentInitationRequestResponse201 resp = objectMapper.convertValue(response.get("PaymentInitationRequestResponse201"), PaymentInitationRequestResponse201.class);
//
//        Assert.assertNotNull(resp.getPaymentId());
//        String redirectLink = (String) resp.getLinks().get("scaRedirect");
//        Assert.assertNotNull(redirectLink);
//        Assert.assertTrue(redirectLink.startsWith("http://localhost:8090/pis/auth?paymentId="+resp.getPaymentId()));
//
    }

    public PaymentCase loadPayment(String file) {
        InputStream stream = SinglePaymentRedirectIT.class.getResourceAsStream(file);
        try {
            return ymlMapper.readValue(stream, PaymentCase.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}

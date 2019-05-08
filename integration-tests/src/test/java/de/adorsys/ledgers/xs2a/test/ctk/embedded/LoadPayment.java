package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class LoadPayment {

	public static PaymentCase loadPayment(Class<?> location, String file, YAMLMapper ymlMapper) {
		InputStream stream = location.getResourceAsStream(file);
		try {
			return ymlMapper.readValue(stream, PaymentCase.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}

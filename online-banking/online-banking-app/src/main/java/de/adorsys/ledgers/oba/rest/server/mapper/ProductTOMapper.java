package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentProductTO;

public class ProductTOMapper {
	
    @SuppressWarnings("PMD.ShortMethodName")
	public static PaymentProductTO map(String paymentProduct) {
		return PaymentProductTO.getByValue(paymentProduct).orElseThrow(() -> new IllegalStateException(String.format("Missing payment product with value %s", paymentProduct)));
	}

}

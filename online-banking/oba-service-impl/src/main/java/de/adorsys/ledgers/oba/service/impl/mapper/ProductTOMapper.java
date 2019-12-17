package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentProductTO;

public class ProductTOMapper {
    public static PaymentProductTO mapToPaymentProduct(String paymentProduct) {
        return PaymentProductTO.getByValue(paymentProduct).orElseThrow(() -> new IllegalStateException(String.format("Missing payment product with value %s", paymentProduct)));
    }
}

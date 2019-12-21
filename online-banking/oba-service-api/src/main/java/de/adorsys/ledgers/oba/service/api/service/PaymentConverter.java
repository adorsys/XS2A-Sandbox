package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;

public interface PaymentConverter {
    Object convertPayment(PaymentTypeTO paymentType, CmsPaymentResponse paymentResponse);
}

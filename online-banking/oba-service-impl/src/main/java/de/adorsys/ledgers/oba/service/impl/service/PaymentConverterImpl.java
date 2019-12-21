package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.oba.service.api.service.PaymentConverter;
import de.adorsys.ledgers.oba.service.impl.mapper.PaymentMapper;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentConverterImpl implements PaymentConverter {
    private final PaymentMapper paymentMapper;

    @Override
    public Object convertPayment(PaymentTypeTO paymentType, CmsPaymentResponse paymentResponse) {
        switch (paymentType) {
            case SINGLE:
                return paymentMapper.toPayment((CmsSinglePayment) paymentResponse.getPayment());
            case BULK:
                return paymentMapper.toPayment((CmsBulkPayment) paymentResponse.getPayment());
            case PERIODIC:
                return paymentMapper.toPayment((CmsPeriodicPayment) paymentResponse.getPayment());
            default:
                throw new IllegalArgumentException(String.format("Payment type %s not supported.", paymentType.name()));
        }
    }

}

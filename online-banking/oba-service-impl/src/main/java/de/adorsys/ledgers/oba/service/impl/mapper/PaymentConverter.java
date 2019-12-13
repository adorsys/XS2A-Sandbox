package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentConverter {
    private final SinglePaymentMapper singlePaymentMapper;
    private final PeriodicPaymentMapper periodicPaymentMapper;
    private final BulkPaymentMapper bulkPaymentMapper;

    public Object convertPayment(PaymentTypeTO paymentType, CmsPaymentResponse paymentResponse) {
        switch (paymentType) {
            case SINGLE:
                return singlePaymentMapper.toPayment((CmsSinglePayment) paymentResponse.getPayment());
            case BULK:
                return bulkPaymentMapper.toPayment((CmsBulkPayment) paymentResponse.getPayment());
            case PERIODIC:
                return periodicPaymentMapper.toPayment((CmsPeriodicPayment) paymentResponse.getPayment());
            default:
                throw new IllegalArgumentException(String.format("Payment type %s not supported.", paymentType.name()));
        }
    }

}

package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.rest.server.resource.ResponseUtils;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class PaymentConverter {
    private final SinglePaymentMapper singlePaymentMapper;
    private final PeriodicPaymentMapper periodicPaymentMapper;
    private final BulkPaymentMapper bulkPaymentMapper;
    private final ResponseUtils responseUtils;

    public Object convertPayment(HttpServletResponse response, PaymentTypeTO paymentType,
                                 CmsPaymentResponse paymentResponse) throws PaymentAuthorizeException {
        switch (paymentType) {
            case SINGLE:
                return singlePaymentMapper.toPayment((CmsSinglePayment) paymentResponse.getPayment());
            case BULK:
                return bulkPaymentMapper.toPayment((CmsBulkPayment) paymentResponse.getPayment());
            case PERIODIC:
                return periodicPaymentMapper.toPayment((CmsPeriodicPayment) paymentResponse.getPayment());
            default:
                throw new PaymentAuthorizeException(responseUtils.badRequest(new PaymentAuthorizeResponse(), String.format("Payment type %s not supported.", paymentType.name()), response));
        }
    }

}

package de.adorsys.ledgers.oba.mapper;

import org.mapstruct.Mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, TimeMapper.class})
public interface SinglePaymentMapper {
    SinglePaymentTO toPayment(CmsSinglePayment payment);
    
}

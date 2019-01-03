package de.adorsys.ledgers.oba.mapper;

import org.mapstruct.Mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.PeriodicPaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, TimeMapper.class})
public interface PeriodicPaymentMapper {
    PeriodicPaymentTO toPayment(CmsPeriodicPayment payment);
}

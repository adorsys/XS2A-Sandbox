package de.adorsys.ledgers.oba.mapper;

import org.mapstruct.Mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.BulkPaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, TimeMapper.class})
public interface BulkPaymentMapper {
    BulkPaymentTO toPayment(CmsBulkPayment payment);
}

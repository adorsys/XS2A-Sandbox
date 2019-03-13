package de.adorsys.ledgers.oba.rest.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.adorsys.ledgers.middleware.api.domain.payment.BulkPaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, TimeMapper.class})
public interface BulkPaymentMapper {
	@Mapping(target="paymentProduct", expression="java(de.adorsys.ledgers.oba.rest.server.mapper.ProductTOMapper.map(payment.getPaymentProduct()))")
    BulkPaymentTO toPayment(CmsBulkPayment payment);
}

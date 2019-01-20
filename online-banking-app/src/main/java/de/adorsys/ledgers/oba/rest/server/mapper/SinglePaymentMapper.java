package de.adorsys.ledgers.oba.rest.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, TimeMapper.class})
public interface SinglePaymentMapper {
	
	@Mapping(target="paymentProduct", expression="java(de.adorsys.ledgers.oba.rest.server.mapper.ProductTOMapper.map(payment.getPaymentProduct()))")
    SinglePaymentTO toPayment(CmsSinglePayment payment);
    
}

package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TimeMapper.class})
public interface SinglePaymentMapper {

    @Mapping(target = "paymentProduct", expression = "java(de.adorsys.ledgers.oba.service.impl.mapper.ProductTOMapper.mapToPaymentProduct(payment.getPaymentProduct()))")
    SinglePaymentTO toPayment(CmsSinglePayment payment);

}

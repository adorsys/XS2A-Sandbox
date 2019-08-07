package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.BulkPaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TimeMapper.class})
public interface BulkPaymentMapper {
    @Mapping(target = "paymentProduct", expression = "java(de.adorsys.ledgers.oba.rest.server.mapper.ProductTOMapper.mapToPaymentProduct(payment.getPaymentProduct()))")
    BulkPaymentTO toPayment(CmsBulkPayment payment);
}

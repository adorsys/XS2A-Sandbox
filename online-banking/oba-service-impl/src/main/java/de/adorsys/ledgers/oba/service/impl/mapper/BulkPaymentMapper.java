package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.BulkPaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TimeMapper.class, ProductTOMapper.class})
public interface BulkPaymentMapper {
    BulkPaymentTO toPayment(CmsBulkPayment payment);
}

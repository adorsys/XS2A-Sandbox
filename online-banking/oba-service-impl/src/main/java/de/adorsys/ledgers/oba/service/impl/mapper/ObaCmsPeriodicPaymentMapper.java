package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.oba.service.api.domain.ObaCmsPeriodicPayment;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObaCmsPeriodicPaymentMapper {
    ObaCmsPeriodicPayment toObaPeriodicPayment(CmsPeriodicPayment cmsPeriodicPayment);
}

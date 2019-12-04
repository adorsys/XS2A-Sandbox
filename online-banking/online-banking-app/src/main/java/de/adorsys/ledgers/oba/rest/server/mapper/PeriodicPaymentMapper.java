package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.FrequencyCodeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PeriodicPaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.xs2a.core.pis.FrequencyCode;
import de.adorsys.psd2.xs2a.core.pis.PisDayOfExecution;
import de.adorsys.psd2.xs2a.core.pis.PisExecutionRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {TimeMapper.class})
public interface PeriodicPaymentMapper {

    @Mappings({
        @Mapping(target = "executionRule", expression = "java(mapPisExecutionRule(payment.getExecutionRule()))"),
        @Mapping(target = "frequency", expression = "java(mapFrequencyCode(payment.getFrequency()))"),
        @Mapping(target = "dayOfExecution", expression = "java(mapPisDayOfExecution(payment.getDayOfExecution()))"),
        @Mapping(target = "paymentProduct", expression = "java(de.adorsys.ledgers.oba.rest.server.mapper.ProductTOMapper.mapToPaymentProduct(payment.getPaymentProduct()))")
    })
    PeriodicPaymentTO toPayment(CmsPeriodicPayment payment);

    default String mapPisExecutionRule(PisExecutionRule rule) {
        return rule == null
                   ? null
                   : rule.getValue();
    }

    default int mapPisDayOfExecution(PisDayOfExecution day) {
        return day == null
                   ? 1
                   : Integer.parseInt(day.getValue());
    }

    //TODO extends Ledgers FrequencyCodeTO according to xs2a one and remove this method https://git.adorsys.de/adorsys/xs2a/ledgers/issues/234
    default FrequencyCodeTO mapFrequencyCode(FrequencyCode frequencyCode) {
        return frequencyCode == null
                   ? null
                   : FrequencyCodeTO.valueOf(frequencyCode.name());
    }
}

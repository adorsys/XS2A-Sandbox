package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.middleware.api.domain.payment.BulkPaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentProductTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PeriodicPaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import de.adorsys.psd2.xs2a.core.pis.PisDayOfExecution;
import de.adorsys.psd2.xs2a.core.pis.PisExecutionRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {TimeMapper.class})
public interface PaymentMapper {

    @Mapping(target = "paymentProduct", expression = "java(mapToPaymentProduct(payment.getPaymentProduct()))")
    SinglePaymentTO toPayment(CmsSinglePayment payment);

    @Mappings({
        @Mapping(target = "executionRule", expression = "java(mapPisExecutionRule(payment.getExecutionRule()))"),
        @Mapping(target = "dayOfExecution", expression = "java(mapPisDayOfExecution(payment.getDayOfExecution()))"),
        @Mapping(target = "paymentProduct", expression = "java(mapToPaymentProduct(payment.getPaymentProduct()))")
    })
    PeriodicPaymentTO toPayment(CmsPeriodicPayment payment);

    @Mapping(target = "paymentProduct", expression = "java(mapToPaymentProduct(payment.getPaymentProduct()))")
    BulkPaymentTO toPayment(CmsBulkPayment payment);

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

    default PaymentProductTO mapToPaymentProduct(String paymentProduct) {
        return PaymentProductTO.getByValue(paymentProduct).orElseThrow(() -> new IllegalStateException(String.format("Missing payment product with value %s", paymentProduct)));
    }
}

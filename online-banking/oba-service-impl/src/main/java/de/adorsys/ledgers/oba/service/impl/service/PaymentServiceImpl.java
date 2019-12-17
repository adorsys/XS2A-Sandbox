package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.ObaCmsPeriodicPayment;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.PaymentService;
import de.adorsys.ledgers.oba.service.impl.mapper.ObaCmsPeriodicPaymentMapper;
import de.adorsys.psd2.consent.api.pis.CmsPayment;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.aspsp.api.pis.CmsAspspPisExportService;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String RESPONSE_ERROR = "Error in response from CMS, please contact admin.";
    private static final String GET_PAYMENTS_ERROR_MSG = "Failed to retrieve periodic payments for user: %s, code: %s, message: %s";
    private static final String DEFAULT_SERVICE_INSTANCE_ID = "UNDEFINED";

    private final CmsAspspPisExportService cmsAspspPisExportService;
    private final ObaCmsPeriodicPaymentMapper obaCmsPeriodicPaymentMapper;

    @Override
    public List<ObaCmsPeriodicPayment> getPeriodicPayments(String userLogin) {
        try {
            Optional<Collection<CmsPayment>> optionalPayments = Optional.ofNullable(cmsAspspPisExportService.exportPaymentsByPsu(new PsuIdData(userLogin, null, null, null), null, null, DEFAULT_SERVICE_INSTANCE_ID));

            return optionalPayments.map(payments -> payments.stream()
                                                        .filter(p -> p.getPaymentType().equals(PaymentType.PERIODIC))
                                                        .map(p -> obaCmsPeriodicPaymentMapper.toObaPeriodicPayment((CmsPeriodicPayment) p))
                                                        .collect(Collectors.toList()))
                       .orElse(Collections.emptyList());
        } catch (FeignException e) {
            String msg = format(GET_PAYMENTS_ERROR_MSG, userLogin, e.status(), e.getMessage());
            log.error(msg);
            throw ObaException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .obaErrorCode(ObaErrorCode.PIS_BAD_REQUEST).build();
        }

    }
}

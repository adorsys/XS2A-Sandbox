package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.ObaCmsPeriodicPayment;
import de.adorsys.ledgers.oba.service.impl.mapper.ObaCmsPeriodicPaymentMapper;
import de.adorsys.psd2.consent.api.pis.CmsPayment;
import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.consent.aspsp.api.pis.CmsAspspPisExportService;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceImplTest {

    private static final String USER = "user";
    private static final String WRONG_USER = "wrong user";

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private CmsAspspPisExportService cmsAspspPisExportService;

    @Mock
    private ObaCmsPeriodicPaymentMapper obaCmsPeriodicPaymentMapper;

    @Test
    public void getPeriodicPaymentsSuccess() {
        //given
        ObaCmsPeriodicPayment expectedResult = getObaCmsPeriodicPayment();
        CmsPayment cmsPeriodicPayment = getCmsPeriodicPayment();
        when(cmsAspspPisExportService.exportPaymentsByPsu(any(PsuIdData.class), isNull(), isNull(), any(String.class)))
            .thenReturn(Collections.singleton(cmsPeriodicPayment));
        when(obaCmsPeriodicPaymentMapper.toObaPeriodicPayment(any(CmsPeriodicPayment.class)))
            .thenReturn(expectedResult);

        //when
        List<ObaCmsPeriodicPayment> actualResult = paymentService.getPeriodicPayments(USER);

        //then
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult.get(0)).isEqualToComparingFieldByFieldRecursively(expectedResult);
    }

    @Test
    public void getPeriodicPaymentsWrongUser() {
        //given
        when(cmsAspspPisExportService.exportPaymentsByPsu(any(PsuIdData.class), isNull(), isNull(), any(String.class)))
            .thenReturn(Collections.emptyList());

        //when
        List<ObaCmsPeriodicPayment> actualResult = paymentService.getPeriodicPayments(WRONG_USER);

        //then
        assertThat(actualResult).isEmpty();
    }

    private CmsPayment getCmsPeriodicPayment() {
        return new CmsPeriodicPayment(null);
    }

    private ObaCmsPeriodicPayment getObaCmsPeriodicPayment() {
        return new ObaCmsPeriodicPayment();
    }
}

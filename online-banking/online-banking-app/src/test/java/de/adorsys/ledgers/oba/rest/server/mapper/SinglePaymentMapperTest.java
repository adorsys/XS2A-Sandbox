package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.general.AddressTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentProductTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.oba.service.impl.mapper.PaymentMapper;
import de.adorsys.ledgers.oba.service.impl.mapper.TimeMapper;
import de.adorsys.psd2.consent.api.CmsAddress;
import de.adorsys.psd2.consent.api.ais.CmsAccountReference;
import de.adorsys.psd2.consent.api.pis.CmsAmount;
import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SinglePaymentMapperTest {
    private static final String PAYMENT_ID = "QWERTY";
    private static final String END_TO_EN_ID = "YTREWQ";
    private static final String CREDITOR_AGENT = "Agent";
    private static final String CREDITOR_NAME = "Steve Jobs";
    private static final String REMITTANCE = "Here we have some priceless information on the payment";
    private static final LocalDate REQUESTED_DATE = LocalDate.of(2019, 7, 3);
    private static final LocalTime REQUESTED_TIME = LocalTime.of(21, 20);
    private static final String IBAN = "DE12345";
    private static final String BBAN = "33654";
    private static final String PAN = "3333 5556 3333 2222";
    private static final String MASKED_PAN = "**** **** **** 2222";
    private static final String MSISDN = "+38050413228*";
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final String STREET = "Buhaker str";
    private static final String BLD_NR = "13";
    private static final String CITY = "Nernberg";
    private static final String POSTAL_CODE = "04310";
    private static final String COUNTRY = "Germany";
    private static final BigDecimal AMOUNT = BigDecimal.TEN;

    @InjectMocks
    private PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Mock
    private TimeMapper timeMapper;

    @Test
    public void mapToPayment() {
        when(timeMapper.mapTime(any())).thenReturn(REQUESTED_TIME);
        SinglePaymentTO expected = getSinglePaymentTO();
        SinglePaymentTO result = mapper.toPayment(getCmsSinglePayment());
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    private CmsSinglePayment getCmsSinglePayment() {
        CmsSinglePayment payment = new CmsSinglePayment(PaymentProductTO.INSTANT_SEPA.getValue());
        payment.setPaymentId(PAYMENT_ID);
        payment.setPaymentProduct(PaymentProductTO.INSTANT_SEPA.getValue());
        payment.setPsuIdDatas(new ArrayList<>());
        payment.setTppInfo(new TppInfo());
        payment.setCreationTimestamp(null);
        payment.setStatusChangeTimestamp(null);

        payment.setEndToEndIdentification(END_TO_EN_ID);
        payment.setDebtorAccount(getCmsAccountReference());
        payment.setInstructedAmount(getCmsAmount());
        payment.setCreditorAccount(getCmsAccountReference());
        payment.setCreditorAgent(CREDITOR_AGENT);
        payment.setCreditorName(CREDITOR_NAME);
        payment.setCreditorAddress(getCmsAddress());
        payment.setRemittanceInformationUnstructured(REMITTANCE);
        payment.setPaymentStatus(TransactionStatus.ACCP);
        payment.setRequestedExecutionDate(REQUESTED_DATE);
        payment.setRequestedExecutionTime(OffsetDateTime.of(REQUESTED_DATE, REQUESTED_TIME, ZoneOffset.UTC));
        payment.setUltimateDebtor(null);
        payment.setUltimateCreditor(null);
        payment.setRemittanceInformationStructured(null);
        return payment;
    }

    private CmsAddress getCmsAddress() {
        CmsAddress address = new CmsAddress();
        address.setStreet(STREET);
        address.setBuildingNumber(BLD_NR);
        address.setCity(CITY);
        address.setPostalCode(POSTAL_CODE);
        address.setCountry(COUNTRY);
        return address;
    }

    private CmsAmount getCmsAmount() {
        return new CmsAmount(CURRENCY, AMOUNT);
    }

    private CmsAccountReference getCmsAccountReference() {
        return new CmsAccountReference(null, IBAN, BBAN, PAN, MASKED_PAN, MSISDN, CURRENCY);
    }

    private SinglePaymentTO getSinglePaymentTO() {
        return new SinglePaymentTO(PAYMENT_ID, END_TO_EN_ID, getReferenceTO(), getAmountTO(), getReferenceTO(), CREDITOR_AGENT, CREDITOR_NAME, getAddressTO(), REMITTANCE, TransactionStatusTO.ACCP, PaymentProductTO.INSTANT_SEPA, REQUESTED_DATE, REQUESTED_TIME);
    }

    private AmountTO getAmountTO() {
        return new AmountTO(CURRENCY, AMOUNT);
    }

    private AddressTO getAddressTO() {
        return new AddressTO(STREET, BLD_NR, CITY, POSTAL_CODE, COUNTRY);
    }

    private AccountReferenceTO getReferenceTO() {
        return new AccountReferenceTO(IBAN, BBAN, PAN, MASKED_PAN, MSISDN, CURRENCY);
    }
}

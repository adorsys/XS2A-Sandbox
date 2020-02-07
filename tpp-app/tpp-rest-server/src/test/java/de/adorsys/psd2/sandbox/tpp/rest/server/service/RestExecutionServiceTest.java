package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.ledgers.middleware.api.domain.account.AccountBalanceTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.middleware.api.domain.general.AddressTO;
import de.adorsys.ledgers.middleware.api.domain.payment.*;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.mappers.PaymentMapperTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.BalanceMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO.SINGLE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestExecutionServiceTest {
    private static final String TPP_ID = "DE_12345678";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String USER_IBAN = "DE89000000115555555555";

    @InjectMocks
    private RestExecutionService executionService;
    @Mock
    private DataRestClient dataRestClient;
    @Mock
    private BalanceMapper balanceMapper;
    @Mock
    private PaymentMapperTO paymentTOMapper;

    @Test
    public void updateLedgers() {
        //given
        when(paymentTOMapper.getMapper()).thenReturn(new ObjectMapper().registerModule(new JavaTimeModule()));
        when(paymentTOMapper.toAbstractPayment(any(), any(), any())).thenReturn(getPaymentTO());
        when(balanceMapper.toAccountBalanceTO(any())).thenReturn(getAccountBalanceTO());

        //when
        executionService.updateLedgers(getPayload(getSinglePmt()));
        verify(balanceMapper, times(1)).toAccountBalanceTO(new AccountBalance());
    }

    @Test(expected = NullPointerException.class)
    public void updateLedgers_getMapperNull() {
        //when
        executionService.updateLedgers(getPayload(new PaymentTO()));
    }

    private AccountBalanceTO getAccountBalanceTO() {
        return new AccountBalanceTO(new AmountTO(), null, LocalDateTime.now(), LocalDate.now(), "lastCommittedTransaction", USER_IBAN);
    }

    private PaymentTO getPaymentTO() {
        PaymentTO paymentTO = new PaymentTO();
        paymentTO.setAccountId(ACCOUNT_ID);
        return paymentTO;
    }

    private DataPayload getPayload(PaymentTO singlePaymentTO) {
        List<UserTO> users = Collections.singletonList(new UserTO("login", "email", "pin"));
        List<AccountDetailsTO> accounts = Collections.singletonList(new AccountDetailsTO());
        List<AccountBalance> balances = Collections.singletonList(new AccountBalance());
        List<PaymentTO> payments = Collections.singletonList(singlePaymentTO);
        return new DataPayload(users, accounts, balances, payments, false, TPP_ID, new HashMap<>());
    }

    private PaymentTO getSinglePmt() {
        PaymentTO payment = new PaymentTO();
        payment.setPaymentType(SINGLE);
        payment.setPaymentProduct("sepa-credit-transfers");
        payment.setRequestedExecutionDate(LocalDate.parse("2019-12-12"));
        payment.setDebtorAccount(new AccountReferenceTO("DE40500105178578796457", null, null, null, null, Currency.getInstance("USD")));
        payment.setTargets(getTargets());
        return payment;
    }

    private List<PaymentTargetTO> getTargets() {
        PaymentTargetTO target = new PaymentTargetTO();
        target.setEndToEndIdentification("WBG-123456789");
        target.setInstructedAmount(new AmountTO(Currency.getInstance("CHF"), new BigDecimal("1.00")));
        target.setCreditorAccount(new AccountReferenceTO("DE40500105178578796457", null, null, null, null, Currency.getInstance("EUR")));
        target.setCreditorAgent("AAAADEBBXXX");
        target.setCreditorAddress(new AddressTO("WBG Straße", "56", "Nürnberg", "90543", "DE", null, null));
        target.setCreditorName("WBG");
        target.setRemittanceInformationUnstructured("Ref. Number WBG-1222");
        ArrayList<PaymentTargetTO> targets = new ArrayList<>();
        targets.add(target);
        return targets;
    }
}

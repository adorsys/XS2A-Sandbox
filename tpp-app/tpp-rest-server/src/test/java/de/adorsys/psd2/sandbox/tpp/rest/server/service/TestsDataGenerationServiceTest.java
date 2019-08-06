package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.config.IbanGenerationConfigProperties;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.utils.IbanGenerator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestsDataGenerationServiceTest {
    private static final String TPP_ID = "11111111";
    private static final String IBAN_PRIMARY = "DE04760501011111111100";
    private static final String IBAN_LAST = "DE47760501011111111199";
    private static final String COUNTRY_CODE = "DE";
    private static final String BANK_CODE = "76050101";

    @InjectMocks
    private IbanGenerationService generationService;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;
    @Mock
    private IbanGenerationConfigProperties ibanGenerationConfigProperties;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        ibanGenerationConfigProperties = new IbanGenerationConfigProperties();
        ibanGenerationConfigProperties.getBankCode().setNisp(BANK_CODE);
        ibanGenerationConfigProperties.getBankCode().setRandom(BANK_CODE);
        ibanGenerationConfigProperties.setCountryCode(COUNTRY_CODE);
        generationService = new IbanGenerationService(ibanGenerationConfigProperties, userMgmtRestClient);
    }

    @Test
    public void generateRandomIban_empty_access() {
        when(userMgmtRestClient.getUser()).thenReturn(getUser(Collections.emptyList()));

        String iban = generationService.generateRandomIban();
        assertTrue(StringUtils.isNotEmpty(iban));
        assertTrue(StringUtils.equals(iban, IBAN_PRIMARY));
    }

    @Test
    public void generateRandomIban_last_iban_available() {
        when(userMgmtRestClient.getUser()).thenReturn(getUser(getAccountAccess(false)));
        String iban = generationService.generateRandomIban();
        assertTrue(StringUtils.isNotEmpty(iban));
        assertTrue(StringUtils.equals(iban, IBAN_LAST));
    }

    @Test(expected = TppException.class)
    public void generateRandomIban_no_ibans_left() {
        when(userMgmtRestClient.getUser()).thenReturn(getUser(getAccountAccess(true)));
        generationService.generateRandomIban();
    }

    @Test
    public void generateNispIban() {
        String s = generationService.generateIbanForNisp(getPayload(), "00", TPP_ID);
        assertTrue(StringUtils.isNotBlank(s) && s.equals(IBAN_PRIMARY));
    }

    private DataPayload getPayload() {
        List<UserTO> users = Collections.singletonList(new UserTO("login", "email", "pin"));
        List<AccountDetailsTO> accounts = Collections.singletonList(new AccountDetailsTO());
        List<AccountBalance> balances = Collections.singletonList(new AccountBalance());
        List<SinglePaymentTO> payments = Collections.singletonList(new SinglePaymentTO());
        return new DataPayload(users, accounts, balances, payments, false, TPP_ID, new HashMap<>());
    }

    private List<AccountAccessTO> getAccountAccess(boolean isCompleted) {
        List<AccountAccessTO> access = IntStream.range(0, 100)
                                           .mapToObj(i -> IbanGenerator.generateIban(COUNTRY_CODE, TPP_ID, BANK_CODE, String.format("%02d", i)))
                                           .map(this::buildOwnerAccess)
                                           .collect(Collectors.toList());
        if (!isCompleted) {
            access.remove(access.size() - 1);
        }
        return access;
    }

    private AccountAccessTO buildOwnerAccess(String iban) {
        AccountAccessTO access = new AccountAccessTO();
        access.setAccessType(AccessTypeTO.OWNER);
        access.setIban(iban);
        return access;
    }

    private ResponseEntity<UserTO> getUser(List<AccountAccessTO> access) {
        UserTO user = new UserTO("test", "test@test.com", "test");
        user.setBranch(TPP_ID);
        user.setAccountAccesses(access);
        return ResponseEntity.ok(user);
    }
}

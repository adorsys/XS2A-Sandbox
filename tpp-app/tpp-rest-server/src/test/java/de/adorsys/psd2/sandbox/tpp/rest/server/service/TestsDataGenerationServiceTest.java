package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTypeTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.BankCodeStructure;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.IBANValidator;
import org.iban4j.CountryCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestsDataGenerationServiceTest {
    private static final String TPP_ID = "DE_12345678";
    private static final String USER_IBAN = "DE89000000115555555555";
    private static final String USER_ID = "QWERTY";
    static final char word = 'A';
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final UserTypeTO USER_TYPE = UserTypeTO.FAKE;

    @InjectMocks
    private IbanGenerationService generationService;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;

    @Test
    public void generateIban() {
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(new UserTO(null, null, null, null, null, Collections.EMPTY_LIST, null, TPP_ID, USER_TYPE)));
        String iban = generationService.generateNextIban();
        boolean isIbanValid = IBANValidator.getInstance().isValid(iban);
        assertTrue(isIbanValid);
    }

    //TODO write correct test for countries with different character type
//    @Test
//    public void validateIbansForDifferentCountries_CharacterTypeN() {
//        List<CountryCode> countryCodes = generationService.getSupportedCountryCodes().stream()
//                                             .map(c -> generationService.getBankCodeStructure(c))
//                                             .filter(BankCodeStructure::isCharacterType)
//                                             .map(BankCodeStructure::getCountryCode).collect(Collectors.toList());
//
//        boolean result = countryCodes.stream()
//                             .map(code -> String.format(code + "_" + "%0" + generationService.getBankCodeStructure(code).getLength() + "d", 01))
//                             .map(i -> when(userMgmtRestClient.getUser())
//                                           .thenReturn(ResponseEntity.ok(new UserTO(null, null, null, null, null, Collections.EMPTY_LIST, null, i))))
//                             .map(i -> generationService.generateNextIban())
//                             .allMatch(i -> IBANValidator.getInstance().isValid(i));
//
//        assertTrue(result);
//    }
//
//    @Test
//    public void validateIbansForDifferentCountries_CharacterTypeCAndA() {
//        boolean result = generationService.getSupportedCountryCodes().stream()
//                         .map(c -> generationService.getBankCodeStructure(c))
//                         .filter(BankCodeStructure::isNotCharacterType)
//                         .map(b -> String.format(b.getCountryCode() + "_" + StringUtils.repeat(word, b.getLength())))
//                         .map(i -> when(userMgmtRestClient.getUser())
//                                       .thenReturn(ResponseEntity.ok(new UserTO(null, null, null, null, null, Collections.EMPTY_LIST, null, i))))
//                         .map(h -> generationService.generateNextIban())
//                         .allMatch(i -> IBANValidator.getInstance().isValid(i));
//
//        assertTrue(result);
//    }

    @Test
    public void generateNispIban() {
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(new UserTO(null, null, null, null, null, getAccountAccess(), null, TPP_ID, USER_TYPE)));
        String s = generationService.generateIbanForNisp(getPayload(), "00");
        assertTrue(StringUtils.isNotBlank(s));
    }

    private DataPayload getPayload() {
        List<UserTO> users = Collections.singletonList(new UserTO("login", "email", "pin", USER_TYPE));
        List<AccountDetailsTO> accounts = Collections.singletonList(new AccountDetailsTO());
        List<AccountBalance> balances = Collections.singletonList(new AccountBalance());
        List<SinglePaymentTO> payments = Collections.singletonList(new SinglePaymentTO());
        return new DataPayload(users, accounts, balances, payments, false, TPP_ID, new HashMap<>());
    }

    private List<AccountAccessTO> getAccountAccess() {
        AccountAccessTO accountAccess = new AccountAccessTO();
        accountAccess.setCurrency(CURRENCY);
        accountAccess.setAccessType(AccessTypeTO.OWNER);
        accountAccess.setScaWeight(50);
        accountAccess.setIban(USER_IBAN);
        accountAccess.setId(USER_ID);
        return Collections.singletonList(accountAccess);
    }
}

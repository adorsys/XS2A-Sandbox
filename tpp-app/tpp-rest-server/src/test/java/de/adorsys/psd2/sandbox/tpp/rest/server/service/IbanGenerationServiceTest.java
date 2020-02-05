package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
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
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.iban4j.bban.BbanStructureEntry.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IbanGenerationServiceTest {
    private static final String TPP_ID = "DE_12345678";
    private static final String USER_IBAN = "DE89000000115555555555";
    private static final String USER_ID = "QWERTY";
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final CountryCode COUNTRY_CODE = CountryCode.DE;
    static final char word = 'A';

    @InjectMocks
    private IbanGenerationService generationService;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;

    @Test
    public void generateIban() {
        //given
        getNewUserTO(TPP_ID, Collections.EMPTY_LIST);

        //when
        String iban = generationService.generateNextIban();

        //then
        boolean isIbanValid = IBANValidator.getInstance().isValid(iban);
        assertTrue(isIbanValid);
    }

    @Test
    public void validateIbansForDifferentCountries_CharacterTypeN() {
        //when
        List<String> ibans = generationService.getSupportedCountryCodes().stream()
                                 .map(c -> generationService.getBankCodeStructure(c))
                                 .filter(BankCodeStructure::isCharacterType)
                                 .map(code -> String.format(code.getCountryCode() + "_" + "%0" + code.getLength() + "d", 01))
                                 .map(i -> getNewUserTO(i, Collections.EMPTY_LIST))
                                 .map(i -> generationService.generateNextIban())
                                 .collect(Collectors.toList());

        //then
        assertTrue(isIbansValid(ibans));
    }

    @Test
    public void validateIbansForDifferentCountries_CharacterTypeCAndA() {
        //when
        List<String> ibans = generationService.getSupportedCountryCodes().stream()
                                 .map(c -> generationService.getBankCodeStructure(c))
                                 .filter(BankCodeStructure::isNotCharacterType)
                                 .map(b -> String.format(b.getCountryCode() + "_" + StringUtils.repeat(word, b.getLength())))
                                 .map(i -> getNewUserTO(i, Collections.EMPTY_LIST))
                                 .map(h -> generationService.generateNextIban())
                                 .collect(Collectors.toList());

        //then
        assertTrue(isIbansValid(ibans));
    }

    @Test
    public void generateNispIban() {
        //given
        getNewUserTO(TPP_ID, getAccountAccess());

        //when
        String s = generationService.generateIbanForNisp(getPayload(), "00");

        //then
        assertTrue(StringUtils.isNotBlank(s));
    }

    @Test
    public void generateNispIban_ibanExist() {
        //given
        getNewUserTO(TPP_ID, getAccountAccess());

        //when
        String s = generationService.generateIbanForNisp(getPayloadWithGeneratedIban(), USER_IBAN);

        //then
        assertTrue(StringUtils.isNotBlank(s));
    }

    @Test
    public void getSupportedCountryCodes() {
        //when
        List<CountryCode> codes = generationService.getSupportedCountryCodes();

        //then
        assertFalse(codes.isEmpty());
    }

    @Test
    public void getCountryCodes() {
        //when
        Map<CountryCode, String> codes = generationService.getCountryCodes();

        //then
        assertFalse(codes.isEmpty());
    }

    @Test
    public void getBankCodeStructure() {
        //when
        BankCodeStructure bankCodeStructure = generationService.getBankCodeStructure(COUNTRY_CODE);

        //then
        assertEquals(8, bankCodeStructure.getLength());
        assertEquals(bankCodeStructure.getType(), EntryCharacterType.n);
    }

    private DataPayload getPayload() {
        List<UserTO> users = Collections.singletonList(new UserTO("login", "email", "pin"));
        List<AccountDetailsTO> accounts = Collections.singletonList(new AccountDetailsTO());
        List<AccountBalance> balances = Collections.singletonList(new AccountBalance());
        List<SinglePaymentTO> payments = Collections.singletonList(new SinglePaymentTO());
        return new DataPayload(users, accounts, balances, payments, false, TPP_ID, new HashMap<>());
    }

    private DataPayload getPayloadWithGeneratedIban() {
        DataPayload dataPayload = getPayload();
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put(USER_IBAN, "value");
        dataPayload.setGeneratedIbans(hashMap);
        return dataPayload;
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

    private OngoingStubbing<ResponseEntity<UserTO>> getNewUserTO(String branch, List accountAccess) {
        return when(userMgmtRestClient.getUser())
                   .thenReturn(ResponseEntity.ok(new UserTO(null, null, null, null, null, accountAccess, null, branch)));
    }

    private boolean isIbansValid(List<String> ibans) {
        return ibans.stream().allMatch(i -> {
            if (!IBANValidator.getInstance().isValid(i)) {
                System.out.println(i + " is not valid");
                return false;
            }
            return true;
        });
    }
}

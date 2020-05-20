package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.BankCodeStructure;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.IBANValidator;
import org.iban4j.CountryCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.iban4j.bban.BbanStructureEntry.EntryCharacterType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IbanGenerationServiceTest {
    private static final String TPP_ID = "DE_12345678";
    private static final String USER_IBAN = "DE89000000115555555555";
    private static final String USER_ID = "QWERTY";
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final CountryCode COUNTRY_CODE = CountryCode.DE;
    private static final char word = 'A';

    @InjectMocks
    private IbanGenerationService generationService;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;

    @Test
    void generateIban() {
        // Given
        stubGetByIdCall(TPP_ID, Collections.EMPTY_LIST, UserRoleTO.STAFF);

        // When
        String iban = generationService.generateNextIban(TPP_ID);

        // Then
        boolean isIbanValid = IBANValidator.getInstance().isValid(iban);
        assertTrue(isIbanValid);
    }

    @Test
    void validateIbansForDifferentCountries_CharacterTypeN() {
        // When
        List<String> ibans = generationService.getCountryCodes().keySet().stream()
                                 .map(c -> generationService.getBankCodeStructure(c))
                                 .filter(BankCodeStructure::isCharacterType)
                                 .map(code -> String.format(code.getCountryCode() + "_" + "%0" + code.getLength() + "d", 01))
                                 .map(i -> stubGetByIdCall(i, Collections.EMPTY_LIST, UserRoleTO.STAFF))
                                 .map(i -> generationService.generateNextIban(TPP_ID))
                                 .collect(Collectors.toList());

        // Then
        assertTrue(isIbansValid(ibans));
    }

    @Test
    void validateIbansForDifferentCountries_CharacterTypeCAndA() {

        // When
        List<String> ibans = generationService.getCountryCodes().keySet().stream()
                                 .map(c -> generationService.getBankCodeStructure(c))
                                 .filter(BankCodeStructure::isNotCharacterType)
                                 .map(b -> b.getCountryCode() + "_" + StringUtils.repeat(word, b.getLength()))
                                 .map(i -> stubGetByIdCall(i, Collections.EMPTY_LIST, UserRoleTO.STAFF))
                                 .map(h -> generationService.generateNextIban(TPP_ID))
                                 .collect(Collectors.toList());

        // Then
        assertTrue(isIbansValid(ibans));
    }

    @Test
    void generateNispIban() {
        // Given
        stubGetSelfCall(TPP_ID, getAccountAccess(), UserRoleTO.STAFF);

        // When
        String s = generationService.generateIbanForNisp(getPayload(), "00");

        // Then
        assertTrue(StringUtils.isNotBlank(s));
    }

    @Test
    void generateNispIban_ibanExist() {
        // When
        String s = generationService.generateIbanForNisp(getPayloadWithGeneratedIban(), USER_IBAN);

        // Then
        assertTrue(StringUtils.isNotBlank(s));
    }

    @Test
    void getCountryCodes() {
        // When
        Map<CountryCode, String> codes = generationService.getCountryCodes();

        // Then
        assertFalse(codes.isEmpty());
    }

    @Test
    void getBankCodeStructure() {
        // When
        BankCodeStructure bankCodeStructure = generationService.getBankCodeStructure(COUNTRY_CODE);

        // Then
        assertEquals(8, bankCodeStructure.getLength());
        assertEquals(bankCodeStructure.getType(), EntryCharacterType.n);
    }

    private DataPayload getPayload() {
        List<UserTO> users = Collections.singletonList(new UserTO("login", "email", "pin"));
        List<AccountDetailsTO> accounts = Collections.singletonList(new AccountDetailsTO());
        List<AccountBalance> balances = Collections.singletonList(new AccountBalance());
        List<PaymentTO> payments = Collections.singletonList(new PaymentTO());
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

    private OngoingStubbing<ResponseEntity<UserTO>> stubGetSelfCall(String branch, List<AccountAccessTO> accountAccess, UserRoleTO role) {
        return when(userMgmtRestClient.getUser())
                   .thenReturn(ResponseEntity.ok(new UserTO(null, null, null, null, null, accountAccess, Collections.singletonList(role), branch, false, false)));
    }

    private OngoingStubbing<ResponseEntity<UserTO>> stubGetByIdCall(String branch, List<AccountAccessTO> accountAccess, UserRoleTO role) {
        return when(userMgmtRestClient.getUserById(anyString()))
                   .thenReturn(ResponseEntity.ok(new UserTO(null, null, null, null, null, accountAccess, Collections.singletonList(role), branch, false, false)));
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

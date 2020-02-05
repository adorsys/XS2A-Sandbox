package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.IbanGenerationService;
import org.iban4j.CountryCode;
import org.iban4j.bban.BbanStructureEntry.EntryCharacterType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.iban4j.CountryCode.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TppControllerTest {
    private static final String USER_ID = "USER_ID";
    private static final String EMAIL = "EMAIL";
    private static final String LOGIN = "LOGIN";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final List<CountryCode> COUNTRY_CODES = Arrays.asList(AD, AL, AT, BE, BG);

    @InjectMocks
    private TppController tppController;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserMgmtStaffRestClient userMgmtStaffRestClient;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;
    @Mock
    private DataRestClient dataRestClient;
    @Mock
    private IbanGenerationService ibanGenerationService;

    @Test
    public void register() {
        //given
        when(userMapper.toUserTO(any())).thenReturn(getUserTO());
        when(userMgmtStaffRestClient.register(USER_ID, getUserTO())).thenReturn(ResponseEntity.ok(getUserTO()));

        //when
        ResponseEntity<Void> response = tppController.register(getUser());

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void remove() {
        //given
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUserTO()));
        when(dataRestClient.branch(any())).thenAnswer(i -> ResponseEntity.ok().build());

        //when
        ResponseEntity<Void> response = tppController.remove();

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void transactions() {
        //given
        when(dataRestClient.account(any())).thenAnswer(i -> ResponseEntity.ok().build());

        //when
        ResponseEntity<Void> response = tppController.transactions(ACCOUNT_ID);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    public void getBankCodeStructure() {
        //given
        when(ibanGenerationService.getBankCodeStructure(any())).thenReturn(getBankCode());

        //when
        ResponseEntity<BankCodeStructure> response = tppController.getBankCodeStructure("DE");

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(8, Objects.requireNonNull(response.getBody()).getLength());
        assertEquals(EntryCharacterType.n, response.getBody().getType());
    }

    @Test
    public void getSupportedCountryCodes() {
        //given
        when(ibanGenerationService.getCountryCodes()).thenReturn(getSuppCountryCodes());

        //when
        ResponseEntity<Map<CountryCode, String>> response = tppController.getSupportedCountryCodes();

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(Objects.requireNonNull(response.getBody()).size(), COUNTRY_CODES.size());
    }

    @Test
    public void getCurrencies() {
        //given
        when(dataRestClient.currencies()).thenReturn(ResponseEntity.ok(getSupportedCurrencies()));

        //when
        ResponseEntity<Set<Currency>> response = tppController.getCurrencies();

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(Objects.requireNonNull(response.getBody()).size(), getSupportedCurrencies().size());
    }


    private Set<Currency> getSupportedCurrencies() {
        Set<Currency> currencies = new HashSet<>();
        currencies.add(Currency.getInstance("EUR"));
        currencies.add(Currency.getInstance("USD"));
        return currencies;
    }

    private UserTO getUserTO() {
        return new UserTO(USER_ID, LOGIN, EMAIL, "pin", Collections.singletonList(new ScaUserDataTO()), Collections.singletonList(new AccountAccessTO()),
                          Collections.singletonList(UserRoleTO.CUSTOMER), "branch");
    }

    private User getUser() {
        return new User(USER_ID, EMAIL, LOGIN, "pin", Collections.singletonList(new ScaUserData()), Collections.singletonList(UserRole.CUSTOMER), Collections.singletonList(new AccountAccess()));
    }

    private BankCodeStructure getBankCode() {
        return new BankCodeStructure(CountryCode.DE);
    }

    private Map<CountryCode, String> getSuppCountryCodes() {
        Map<CountryCode, String> codes = new HashMap<>();
        COUNTRY_CODES.forEach(c -> codes.put(c, c.getName()));
        return codes;
    }
}

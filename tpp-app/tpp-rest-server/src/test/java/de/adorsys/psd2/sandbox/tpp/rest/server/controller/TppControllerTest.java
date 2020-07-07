package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.general.BbanStructure;
import de.adorsys.ledgers.middleware.api.domain.general.RevertRequestTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.CmsDbNativeService;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.IbanGenerationService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.RestExecutionService;
import org.iban4j.CountryCode;
import org.iban4j.bban.BbanStructureEntry.EntryCharacterType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.iban4j.CountryCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TppControllerTest {
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
    @Mock
    private CmsDbNativeService cmsDbNativeService;
    @Mock
    private RestExecutionService restExecutionService;

    @Test
    void register() {
        // Given
        UserTO userTO = getUserTO();
        when(userMapper.toUserTO(any())).thenReturn(userTO);
        when(userMgmtStaffRestClient.register(USER_ID, userTO)).thenReturn(ResponseEntity.ok(userTO));

        // When
        ResponseEntity<Void> response = tppController.register(getUser());

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void remove() {
        // Given
        when(userMgmtRestClient.getUser())
            .thenReturn(ResponseEntity.ok(getUserTO()));
        when(userMgmtStaffRestClient.getBranchUserLogins())
            .thenReturn(ResponseEntity.ok(Arrays.asList("anton.brueckner", "max.musterman")));

        when(dataRestClient.branch(any())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = tppController.remove();

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void revert() {
        // When
        ResponseEntity<Void> response = tppController.revert(getRevertRequest());

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void transactions() {
        // Given
        when(dataRestClient.account(any())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = tppController.transactions(ACCOUNT_ID);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getBankCodeStructure() {
        // Given
        when(ibanGenerationService.getBankCodeStructure(any())).thenReturn(getBankCode());

        // When
        ResponseEntity<BankCodeStructure> response = tppController.getBankCodeStructure("DE");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(8, Objects.requireNonNull(response.getBody()).getLength());
        assertEquals(EntryCharacterType.n, response.getBody().getType());
    }

    @Test
    void getSupportedCountryCodes() {
        // Given
        when(ibanGenerationService.getCountryCodes()).thenReturn(getSuppCountryCodes());

        // When
        ResponseEntity<Map<CountryCode, String>> response = tppController.getSupportedCountryCodes();

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(Objects.requireNonNull(response.getBody()).size(), COUNTRY_CODES.size());
    }

    @Test
    void getCurrencies() {
        // Given
        when(dataRestClient.currencies()).thenReturn(ResponseEntity.ok(getSupportedCurrencies()));

        // When
        ResponseEntity<Set<Currency>> response = tppController.getCurrencies();

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(Objects.requireNonNull(response.getBody()).size(), getSupportedCurrencies().size());
    }

    @Test
    void getRandomTppId() {
        when(dataRestClient.branchId(any())).thenReturn(ResponseEntity.ok("DE_123"));
        ResponseEntity<String> result = tppController.getRandomTppId("DE");
        ArgumentCaptor<BbanStructure> captor = ArgumentCaptor.forClass(BbanStructure.class);
        verify(dataRestClient, times(1)).branchId(captor.capture());
        BbanStructure value = captor.getValue();
        assertEquals("DE", value.getCountryPrefix());
        assertEquals(8, value.getLength());
        assertEquals(BbanStructure.EntryType.N, value.getEntryType());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void user() {
        when(dataRestClient.user(anyString())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = tppController.user(USER_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void account() {
        when(dataRestClient.depositAccount(anyString())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = tppController.account(ACCOUNT_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    private Set<Currency> getSupportedCurrencies() {
        Set<Currency> currencies = new HashSet<>();
        currencies.add(Currency.getInstance("EUR"));
        currencies.add(Currency.getInstance("USD"));
        return currencies;
    }

    private UserTO getUserTO() {
        return new UserTO(USER_ID, LOGIN, EMAIL, "pin", Collections.singletonList(new ScaUserDataTO()), Collections.singletonList(new AccountAccessTO()),
                          Collections.singletonList(UserRoleTO.CUSTOMER), "branch", false, false);
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

    private RevertRequestTO getRevertRequest() {
        RevertRequestTO revertRequestTO = new RevertRequestTO();
        revertRequestTO.setBranchId("DE-12345");
        revertRequestTO.setRecoveryPointId(111L);

        return revertRequestTO;
    }

}

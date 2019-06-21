package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.utils.IbanGenerator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestsDataGenerationServiceTest {
    private static final String TPP_ID = "11111111";
    private static final String IBAN_PRIMARY = "DE04760700241111111100";
    private static final String IBAN_LAST = "DE47760700241111111199";

    @InjectMocks
    private IbanGenerationService generationService;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;


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

    private List<AccountAccessTO> getAccountAccess(boolean isCompleted) {
        List<AccountAccessTO> access = IntStream.range(0, 100)
                                           .mapToObj(i -> IbanGenerator.generateRandomIban(TPP_ID, i))
                                           .map(iban -> new AccountAccessTO(iban, AccessTypeTO.OWNER, null))
                                           .collect(Collectors.toList());
        if (!isCompleted) {
            access.remove(access.size() - 1);
        }
        return access;
    }

    private ResponseEntity<UserTO> getUser(List<AccountAccessTO> access) {
        UserTO user = new UserTO("test", "test@test.com", "test");
        user.setBranch(TPP_ID);
        user.setAccountAccesses(access);
        return ResponseEntity.ok(user);
    }
}

package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import de.adorsys.ledgers.xs2a.test.ctk.embedded.LinkResolver;
import de.adorsys.psd2.model.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ConsentRedirectOneScaApp extends AbstractConsentRedirect {

    public static WebDriver driver;

    @BeforeClass
    public static void initializationMethod() {
        WebDriverManager.chromedriver().setup();
    }

    @Override
    protected String getPsuId() {
        return "anton.brueckner";
    }

    @Override
    protected String getIban() {
        return "DE80760700240271232400";
    }

    @Test
    public void test_initiate_dedicated_consent() {

        // ============= INITIATE CONSENT =======================//
        ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createConsent(emptyConsent());
        consentHelper.checkConsentStatus(createConsentResp, ConsentStatus.RECEIVED);

        // ============= IDENTIFY PSU =======================//
        ConsentsResponse201 consentsResponse201 = createConsentResp.getBody();
        String scaRedirectLink = LinkResolver.getLink(consentsResponse201.getLinks(), "scaRedirect");
        driver = new ChromeDriver();
        driver.get(scaRedirectLink);
    }

    private Consents emptyConsent() {
        Consents consents = new Consents();
        AccountAccess access = new AccountAccess();
        List<AccountReference> accounts = Collections.emptyList();//Arrays.asList(accountRef);
        access.setAccounts(accounts);
        access.setBalances(accounts);
        access.setTransactions(accounts);
        consents.setAccess(access);
        consents.setFrequencyPerDay(4);
        consents.setRecurringIndicator(true);
        consents.setValidUntil(LocalDate.of(2021, 11, 30));
        return consents;
    }

}

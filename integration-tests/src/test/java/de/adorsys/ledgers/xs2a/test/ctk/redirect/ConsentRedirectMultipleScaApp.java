package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.http.ResponseEntity;

public class ConsentRedirectMultipleScaApp extends AbstractConsentRedirect {
	
	public static WebDriver driver;
	
	@BeforeClass
	public static void initializationMethod() {
		WebDriverManager.chromedriver().setup();		
	}

	@Override
	protected String getPsuId() {
		return "max.musterman";
	}
	@Override
	protected String getIban() {
		return "DE38760700240320465700";
	}
	
	@Ignore
	@Test
	public void test_initiate_dedicated_consent() {
		
		// ============= INITIATE CONSENT =======================//
		ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createDedicatedConsent();
		consentHelper.checkConsentStatus(createConsentResp, ConsentStatus.RECEIVED);
		
		// ============= IDENTIFY PSU =======================//
		ConsentsResponse201 consentsResponse201 = createConsentResp.getBody();
		String scaRedirectLink = consentHelper.getScaRedirect(consentsResponse201.getLinks());
		driver = new ChromeDriver();
		driver.get(scaRedirectLink);
	}
}

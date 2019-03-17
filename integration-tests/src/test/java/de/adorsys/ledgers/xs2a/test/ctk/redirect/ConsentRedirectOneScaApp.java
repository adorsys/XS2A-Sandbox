package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.http.ResponseEntity;

import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentsResponse201;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ConsentRedirectOneScaApp extends AbstractConsentRedirect {
	
	public static WebDriver driver;
	
	@BeforeClass
	public static void initializationMethod() {
		WebDriverManager.chromedriver().setup();		
//		ChromeOptions chromeOptions = new ChromeOptions();
//		chromeOptions.addArguments("--start-maximized");
//		driver = new ChromeDriver(chromeOptions);
	}
	
//	@AfterClass
//	public static void afterMethod() {
//		driver.close();
//	}	
	
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
		ResponseEntity<ConsentsResponse201> createConsentResp = consentHelper.createDedicatedConsent();
		consentHelper.checkConsentStatus(createConsentResp, ConsentStatus.RECEIVED);
		
		// ============= IDENTIFY PSU =======================//
		ConsentsResponse201 consentsResponse201 = createConsentResp.getBody();
		String scaRedirectLink = consentHelper.getScaRedirect(consentsResponse201.getLinks());
		driver = new ChromeDriver();
		driver.get(scaRedirectLink);
	}
}

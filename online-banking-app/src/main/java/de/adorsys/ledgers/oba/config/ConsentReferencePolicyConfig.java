package de.adorsys.ledgers.oba.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.adorsys.ledgers.oba.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.consentref.DefaultConsentReferencePolicy;

@Configuration
public class ConsentReferencePolicyConfig {
	
	@Bean
	public ConsentReferencePolicy consentReferencePolicy() {
		return new DefaultConsentReferencePolicy();
	}
}

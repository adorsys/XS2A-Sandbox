package de.adorsys.ledgers.oba.rest.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.rest.server.consentref.DefaultConsentReferencePolicy;

@Configuration
public class ConsentReferencePolicyConfig {
	
	@Bean
	public ConsentReferencePolicy consentReferencePolicy() {
		return new DefaultConsentReferencePolicy();
	}
}

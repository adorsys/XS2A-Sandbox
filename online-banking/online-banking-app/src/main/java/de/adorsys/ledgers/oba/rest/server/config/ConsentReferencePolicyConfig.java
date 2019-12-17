package de.adorsys.ledgers.oba.rest.server.config;

import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.adorsys.ledgers.oba.service.impl.service.DefaultConsentReferencePolicy;

@Configuration
public class ConsentReferencePolicyConfig {

	@Bean
	public ConsentReferencePolicy consentReferencePolicy() {
		return new DefaultConsentReferencePolicy();
	}
}

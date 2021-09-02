package de.adorsys.psd2.sandbox.admin.rest.server.auth;

import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {
	@Bean
	public AuthRequestInterceptor getClientAuth() {
		return new AuthRequestInterceptor();
	}
}

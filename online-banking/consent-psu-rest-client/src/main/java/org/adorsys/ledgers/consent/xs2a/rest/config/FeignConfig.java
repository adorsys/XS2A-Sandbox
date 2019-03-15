package org.adorsys.ledgers.consent.xs2a.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
	
	@Bean
	public NullHeaderInterceptor nullHeaderInterceptor() {
		return new NullHeaderInterceptor();
	}
}

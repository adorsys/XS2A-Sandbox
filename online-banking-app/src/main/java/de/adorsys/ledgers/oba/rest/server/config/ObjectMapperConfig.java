package de.adorsys.ledgers.oba.rest.server.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.adorsys.ledgers.oba.rest.server.mapper.CmsPaymentDeserializer;
import de.adorsys.psd2.consent.api.pis.CmsPayment;

@Configuration
public class ObjectMapperConfig {

	@Autowired
	private ObjectMapper objectMapper;
	
	@PostConstruct
	public void postConstruct() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(CmsPayment.class, new CmsPaymentDeserializer(objectMapper));
		objectMapper.registerModule(module);		
	}
}

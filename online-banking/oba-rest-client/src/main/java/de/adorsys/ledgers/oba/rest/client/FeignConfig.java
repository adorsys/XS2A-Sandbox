package de.adorsys.ledgers.oba.rest.client;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.adorsys.ledgers.oba.rest.utils.NullHeaderInterceptor;
import feign.codec.Decoder;

@Configuration
public class FeignConfig {
	
	@Bean
	public Decoder feignDecoder() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		objectMapper.registerModule(new JavaTimeModule());
		HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
		ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
		return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
	}
	
	@Bean
	public NullHeaderInterceptor nullHeaderInterceptor() {
		return new NullHeaderInterceptor();
	}
}

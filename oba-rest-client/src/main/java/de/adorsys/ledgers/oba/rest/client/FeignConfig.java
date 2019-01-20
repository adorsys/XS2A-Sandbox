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

import de.adorsys.ledgers.oba.rest.utils.NullHeaderInterceptor;
import feign.codec.Decoder;

@Configuration
public class FeignConfig {

	@Bean
	public Decoder feignDecoder() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(mapper);
		ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
		return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
	}
	
	@Bean
	public NullHeaderInterceptor nullHeaderInterceptor() {
		return new NullHeaderInterceptor();
	}
}

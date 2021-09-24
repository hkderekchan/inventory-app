package com.mysite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableTransactionManagement
public class Config {

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				// apply com.mysite.service.Page annotations during serialization/deserialization
				.addMixIn(org.springframework.data.domain.Page.class, com.mysite.service.Page.class);
	}
	
}

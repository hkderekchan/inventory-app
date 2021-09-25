package com.mysite.rest.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.InventoryApplication;

@SpringBootTest(classes = InventoryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractIntegrationTest {

	@LocalServerPort
	private int port;

	protected TestRestTemplate restTemplate = new TestRestTemplate();

	protected HttpHeaders headers = new HttpHeaders();

	@BeforeEach
	public void setup() {
		restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
	}
	
	protected String read(String file) throws IOException {
		final File resource = new ClassPathResource(file).getFile();
		return new String(Files.readAllBytes(resource.toPath()));
	}

	protected <T> T readValue(final ResponseEntity<String> response, Class<T> clazz)
			throws JsonProcessingException, JsonMappingException {
		return readValue(response.getBody(), clazz);
	}

	protected <T> T readValue(String value, Class<T> clazz) throws JsonProcessingException, JsonMappingException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(value, clazz);
	}
	
	protected String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}

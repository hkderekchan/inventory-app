package com.mysite.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mysite.rest.request.CreateCategoryRequest;
import com.mysite.rest.response.CreateCategoryResponse;

public class CategoryControllerIntegrationTest extends AbstractIntegrationTest{

	@Test
	public void shouldListCategories() throws Exception {

		final HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		final ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/api/v1/categories"), HttpMethod.GET,
				entity, String.class);
		final String categories = read("get-categories-resp.json");
		JSONAssert.assertEquals(categories, responseEntity.getBody(), true);
	}

	@Test
	public void shouldCreateTwoLevelCategories() throws Exception {

		final CreateCategoryRequest req1 = new CreateCategoryRequest();
		req1.setName("Food");
		final ResponseEntity<String> responseEntity1 = invokeCreateCategoryEndpoint(req1);
		assertEquals(HttpStatus.CREATED, responseEntity1.getStatusCode());
		
		final CreateCategoryResponse resp1 = readValue(responseEntity1, CreateCategoryResponse.class);
		final CreateCategoryRequest req2 = new CreateCategoryRequest();
		req2.setName("Snack");
		req2.setParent(resp1.getId());
		final ResponseEntity<String> responseEntity2 = invokeCreateCategoryEndpoint(req2);
		assertEquals(HttpStatus.CREATED, responseEntity2.getStatusCode());
	}
	
	@Test
	public void shouldDeleteCategory() throws Exception {

		final HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		final ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/v1/categories/1"),
				HttpMethod.DELETE, entity, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	private ResponseEntity<String> invokeCreateCategoryEndpoint(final CreateCategoryRequest req) {
		final HttpEntity<CreateCategoryRequest> entity = new HttpEntity<CreateCategoryRequest>(req, headers);
		return restTemplate.exchange(createURLWithPort("/api/v1/categories"), HttpMethod.POST,
				entity, String.class);
	}

}

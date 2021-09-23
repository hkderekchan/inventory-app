package com.mysite.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mysite.rest.request.CreateInventoryRequest;

public class InventoryResourceTest extends AbstractIntegrationTest{

	@Test
	public void shouldListInventory() throws Exception {

		final HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		final ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/v1/inventory?pageIndex=0"), HttpMethod.GET,
				entity, String.class);
		final String categories = read("get-inventory-resp.json");
		JSONAssert.assertEquals(categories, response.getBody(), false);
	}

	@Test
	public void shouldCreateInventory() throws Exception {

		final CreateInventoryRequest req1 = inventory("Ice-cream", 5, 3);
		final ResponseEntity<String> resp1 = invokeCreateInventoryEndpoint(req1);
		assertEquals(HttpStatus.OK, resp1.getStatusCode());
		
		// allow to create on 1st level category
		final CreateInventoryRequest req2 = inventory("Candies", 10, 1);
		final ResponseEntity<String> resp2 = invokeCreateInventoryEndpoint(req2);
		assertEquals(HttpStatus.OK, resp2.getStatusCode());
	}

	@Test
	// TODO not yet done
	public void shouldUpdateInventory() throws Exception {

		final HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		final ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/v1/categories/1"),
				HttpMethod.DELETE, entity, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	// TODO not yet done
	public void shouldDeleteInventory() throws Exception {

		final HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		final ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/v1/categories/1"),
				HttpMethod.DELETE, entity, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	private CreateInventoryRequest inventory(final String name, final int quantity, final int categoryId) {
		final CreateInventoryRequest req = new CreateInventoryRequest();
		req.setName(name);
		req.setQuantity(quantity);
		req.setCategoryId(categoryId);
		return req;
	}
	
	private ResponseEntity<String> invokeCreateInventoryEndpoint(final CreateInventoryRequest req) {
		final HttpEntity<CreateInventoryRequest> entity = new HttpEntity<CreateInventoryRequest>(req, headers);
		return restTemplate.exchange(createURLWithPort("/api/v1/inventory"), HttpMethod.POST,
				entity, String.class);
	}

}

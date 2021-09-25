package com.mysite.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mysite.rest.request.CreateInventoryRequest;
import com.mysite.rest.request.UpdateInventoryRequest;

@TestMethodOrder(OrderAnnotation.class)
public class InventoryControllerIntegrationTest extends AbstractIntegrationTest{

	@Test
	@Order(1)
	public void shouldListInventory() throws Exception {

		final String expected = read("get-inventory-resp.json");
		JSONAssert.assertEquals(expected, getInventoryList(), false);
	}

	@Test
	@Order(2)
	public void shouldUpdateInventory() throws Exception {

		final UpdateInventoryRequest req = new UpdateInventoryRequest();
		req.setQuantity(88);
		final HttpEntity<UpdateInventoryRequest> entity = new HttpEntity<UpdateInventoryRequest>(req, headers);
		final ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/api/v1/inventory/1"),
				HttpMethod.PATCH, entity, String.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		
		final String expected = read("get-inventory-resp-after-update.json");
		JSONAssert.assertEquals(expected, getInventoryList(), false);
	}

	@Test
	@Order(3)
	public void shouldCreateInventory() throws Exception {

		final CreateInventoryRequest req = inventory("Ice-cream", 5, 2, 3);
		final ResponseEntity<String> responseEntity = invokeCreateInventoryEndpoint(req);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		
		final String inventory = getInventoryList();
		assertTrue(inventory.contains(req.getName()));
	}

	@Test
	@Order(4)
	public void shouldDeleteInventory() throws Exception {

		final HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		final ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/api/v1/inventory/1"),
				HttpMethod.DELETE, entity, String.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		
		final String expected = "{}";
		JSONAssert.assertEquals(expected, getInventoryList(), false);
	}

	private String getInventoryList() {
		final HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		final ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/api/v1/inventory?pageIndex=0"), HttpMethod.GET,
				entity, String.class);
		return responseEntity.getBody();
	}

	private CreateInventoryRequest inventory(final String name, final int quantity, final int categoryId, final int subCategoryId) {
		final CreateInventoryRequest req = new CreateInventoryRequest();
		req.setName(name);
		req.setQuantity(quantity);
		req.setCategoryId(categoryId);
		req.setSubCategoryId(subCategoryId);
		return req;
	}
	
	private ResponseEntity<String> invokeCreateInventoryEndpoint(final CreateInventoryRequest req) {
		final HttpEntity<CreateInventoryRequest> entity = new HttpEntity<CreateInventoryRequest>(req, headers);
		return restTemplate.exchange(createURLWithPort("/api/v1/inventory"), HttpMethod.POST,
				entity, String.class);
	}

}

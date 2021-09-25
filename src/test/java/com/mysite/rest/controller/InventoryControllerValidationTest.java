package com.mysite.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.mysite.rest.request.CreateInventoryRequest;
import com.mysite.rest.request.UpdateInventoryRequest;
import com.mysite.service.InventoryService;

@WebMvcTest({ InventoryController.class })
public class InventoryControllerValidationTest extends AbstractValidationTest {

	@MockBean
	private InventoryService inventoryService;
	
	@Test
	public void createInventoryValidation() throws Exception {
		
		final MockHttpServletRequestBuilder reqBldr = post("/api/v1/inventory")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr);
		
		final MockHttpServletRequestBuilder reqBldr2 = post("/api/v1/inventory")
				.content(mapper.writeValueAsString(new CreateInventoryRequest()))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr2);
		
		final MockHttpServletRequestBuilder reqBldr3 = post("/api/v1/inventory")
				.content(mapper.writeValueAsString(createInventoryReq("??", 0, 2, 3)))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr3);
		
		final MockHttpServletRequestBuilder reqBldr4 = post("/api/v1/inventory")
				.content(mapper.writeValueAsString(createInventoryReq("valid name", -1, 2, 3)))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr4);
		
		final MockHttpServletRequestBuilder reqBldr5 = post("/api/v1/inventory")
				.content(mapper.writeValueAsString(createInventoryReq("valid name", 0, -1, 3)))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr5);
		
		final MockHttpServletRequestBuilder reqBldr6 = post("/api/v1/inventory")
				.content(mapper.writeValueAsString(createInventoryReq("valid name", 0, 2, -1)))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr6);
	}
	
	private CreateInventoryRequest createInventoryReq(final String name, final int quantity, final int categoryId, final int subCategoryId) {
		final CreateInventoryRequest req = new CreateInventoryRequest();
		req.setName(name);
		req.setQuantity(quantity);
		req.setCategoryId(categoryId);
		req.setSubCategoryId(subCategoryId);
		return req;
	}
	
	@Test
	public void listInventoryValidation() throws Exception {
		
		final MockHttpServletRequestBuilder reqBldr1 = get("/api/v1/inventory")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr1);
		
		final MockHttpServletRequestBuilder reqBldr2 = get("/api/v1/inventory?pageIndex=a")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr2);

		final MockHttpServletRequestBuilder reqBldr3 = get("/api/v1/inventory?pageIndex=0&categoryId=a")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr3);
	}
	
	@Test
	public void updateInventoryValidation() throws Exception {
		
		final MockHttpServletRequestBuilder reqBldr1 = patch("/api/v1/inventory")
				.contentType(MediaType.APPLICATION_JSON);
		assertResponseStatus(reqBldr1, HttpStatus.METHOD_NOT_ALLOWED.value());

		final MockHttpServletRequestBuilder reqBldr2 = patch("/api/v1/inventory/0")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr2);
		
		final MockHttpServletRequestBuilder reqBldr3 = patch("/api/v1/inventory/1")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr3);
		
		final UpdateInventoryRequest updtReq = new UpdateInventoryRequest();
		final MockHttpServletRequestBuilder reqBldr4 = patch("/api/v1/inventory/1")
				.content(mapper.writeValueAsString(updtReq))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr4);
	}
	
	@Test
	public void deleteInventoryValidation() throws Exception {

		final MockHttpServletRequestBuilder reqBldr1 = delete("/api/v1/inventory")
				.contentType(MediaType.APPLICATION_JSON);
		assertResponseStatus(reqBldr1, HttpStatus.METHOD_NOT_ALLOWED.value());

		final MockHttpServletRequestBuilder reqBldr2 = delete("/api/v1/inventory/a")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr2);
		
		final MockHttpServletRequestBuilder reqBldr3 = delete("/api/v1/inventory/0")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr3);
	}
	
}

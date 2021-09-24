package com.mysite.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.mysite.rest.request.CreateInventoryRequest;
import com.mysite.rest.request.UpdateInventoryRequest;
import com.mysite.service.InventoryService;

@WebMvcTest({ InventoryResource.class })
public class InventoryResourceValidationTest extends AbstractValidationTest {

	@MockBean
	private InventoryService inventoryService;
	
	@Test
	public void createInventoryValidation() throws Exception {
		
		final MockHttpServletRequestBuilder req = post("/api/v1/inventory")
				.content(mapper.writeValueAsString(new CreateInventoryRequest()))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(req);		
	}
	
	@Test
	public void listInventoryValidation() throws Exception {
		
		final MockHttpServletRequestBuilder req1 = get("/api/v1/inventory?pageIndex=a")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(req1);
		
		final MockHttpServletRequestBuilder req2 = get("/api/v1/inventory?pageIndex=1&categoryId=0")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(req2);
	}
	
	@Test
	public void updateInventoryValidation() throws Exception {
		
		final UpdateInventoryRequest updtReq = new UpdateInventoryRequest();
		final MockHttpServletRequestBuilder req1 = patch("/api/v1/inventory/1")
				.content(mapper.writeValueAsString(updtReq))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(req1);
		
		updtReq.setQuantity(15);
		final MockHttpServletRequestBuilder req2 = patch("/api/v1/inventory/0")
				.content(mapper.writeValueAsString(updtReq))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(req2);
	}
	
	@Test
	public void deleteInventoryValidation() throws Exception {

		final MockHttpServletRequestBuilder req = delete("/api/v1/inventory/0")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(req);		
	}
	
}

package com.mysite.rest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.rest.request.CreateCategoryRequest;
import com.mysite.rest.request.CreateInventoryRequest;
import com.mysite.service.CategoryService;
import com.mysite.service.InventoryService;

@WebMvcTest({ CategoryResource.class, InventoryResource.class })
public class DefaultExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CategoryService categoryService;
	
	@MockBean
	private InventoryService inventoryService;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Test
	public void methodArgNotValidExceptionHandling() throws Exception {

		final MockHttpServletRequestBuilder req = post("/api/v1/categories")
				.content(mapper.writeValueAsString(new CreateCategoryRequest()))
				.contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(req).andExpect(status().isBadRequest())
			.andDo(print()).andExpect(content().string(containsString("api.error")));
	}

	@Test
	public void validationExceptionHandling() throws Exception {

		final CreateInventoryRequest req = new CreateInventoryRequest();
		req.setName("wallet");
		req.setQuantity(99);
		req.setCategoryId(1);
		req.setSubCategoryId(3);
		doThrow(new ValidationException("test msg")).when(inventoryService).createInventory(any(CreateInventoryRequest.class));
		
		final MockHttpServletRequestBuilder reqBldr = post("/api/v1/inventory")
				.content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(reqBldr).andExpect(status().isBadRequest())
			.andDo(print()).andExpect(content().string(containsString("test msg")));
	}
	
	@Test
	public void otherExceptionHandling() throws Exception {

		doThrow(new RuntimeException("some other exception")).when(categoryService).deleteCategory(anyInt());
		
		final MockHttpServletRequestBuilder reqBldr = delete("/api/v1/categories/99")
				.contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(reqBldr).andExpect(status().is5xxServerError())
			.andDo(print()).andExpect(content().string(containsString("api.error")));
	}
	
}

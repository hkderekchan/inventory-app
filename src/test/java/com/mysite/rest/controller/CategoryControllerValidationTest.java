package com.mysite.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.mysite.rest.controller.CategoryController;
import com.mysite.rest.request.CreateCategoryRequest;
import com.mysite.service.CategoryService;

@WebMvcTest({ CategoryController.class })
public class CategoryControllerValidationTest extends AbstractValidationTest {

	@MockBean
	private CategoryService categoryService;
	
	@Test
	public void createCategoryValidation() throws Exception {
		
		final MockHttpServletRequestBuilder reqBldr1 = post("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr1);
		
		final CreateCategoryRequest req = new CreateCategoryRequest();
		final MockHttpServletRequestBuilder reqBldr2 = post("/api/v1/categories")
				.content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr2);
		
		req.setName("??");
		final MockHttpServletRequestBuilder reqBldr3 = post("/api/v1/categories")
				.content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr3);
		
		req.setName("valid name");
		req.setParent(-1);
		final MockHttpServletRequestBuilder reqBldr4 = post("/api/v1/categories")
				.content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr4);
	}
	
	@Test
	public void deleteCategoryValidation() throws Exception {

		final MockHttpServletRequestBuilder reqBldr1 = delete("/api/v1/categories")
				.contentType(MediaType.APPLICATION_JSON);
		assertResponseStatus(reqBldr1, HttpStatus.METHOD_NOT_ALLOWED.value());
		
		final MockHttpServletRequestBuilder reqBldr2 = delete("/api/v1/categories/0")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr2);
		
		final MockHttpServletRequestBuilder reqBldr3 = delete("/api/v1/categories/a")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(reqBldr3);
	}
	
}

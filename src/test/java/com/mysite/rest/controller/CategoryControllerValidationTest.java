package com.mysite.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
		
		final MockHttpServletRequestBuilder req = post("/api/v1/categories")
				.content(mapper.writeValueAsString(new CreateCategoryRequest()))
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(req);		
	}
	
	@Test
	public void deleteCategoryValidation() throws Exception {

		final MockHttpServletRequestBuilder req = delete("/api/v1/categories/0")
				.contentType(MediaType.APPLICATION_JSON);
		assertBadRequest(req);		
	}
	
}

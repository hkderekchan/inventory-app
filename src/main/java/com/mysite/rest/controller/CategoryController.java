package com.mysite.rest.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.rest.request.CreateCategoryRequest;
import com.mysite.rest.response.CreateCategoryResponse;
import com.mysite.rest.response.ListCategoriesResponse;
import com.mysite.service.CategoryService;

@RestController
@RequestMapping({
	"/api/v1"
})
@Validated
public class CategoryController {

	@Autowired
	private CategoryService service;
	
	@PostMapping("/categories")
	@ResponseStatus(HttpStatus.CREATED)
	public CreateCategoryResponse createCategory(@Valid @RequestBody final CreateCategoryRequest req) {
		final int id = service.createCategory(req);
		return new CreateCategoryResponse(id);
	}
	
	@GetMapping("/categories")
	public ListCategoriesResponse listCategories(){
		return new ListCategoriesResponse(service.groupCategories());
	}
	
	@DeleteMapping("/categories/{id}")
	public void deleteCategory(@PathVariable @Positive @NotNull final Integer id) {
		service.deleteCategory(id);
	}
	
}

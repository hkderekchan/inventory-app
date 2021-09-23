package com.mysite.rest;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.entity.Category;
import com.mysite.rest.request.CreateCategoryRequest;
import com.mysite.rest.response.CreateInventoryResponse;
import com.mysite.service.CategoryService;

@RestController
@RequestMapping({
	"/api/v1"
})
public class CategoryResource {

	@Autowired
	private CategoryService service;
	
	@PostMapping("/categories")
	public CreateInventoryResponse createCategory(@Valid @RequestBody final CreateCategoryRequest req) {
		final long id = service.createCategory(req);
		return new CreateInventoryResponse(id);
	}
	
	@GetMapping("/categories")
	public List<Category> listCategories(){
		return service.groupCategories();
	}
	
	@DeleteMapping("/categories/{id}")
	public void deleteInventory(@PathVariable @Positive @NotNull final Integer id) {
		service.deleteCategory(id);
	}
	
}

package com.mysite.rest.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.entity.Inventory;
import com.mysite.rest.request.CreateInventoryRequest;
import com.mysite.rest.request.UpdateInventoryRequest;
import com.mysite.rest.response.CreateInventoryResponse;
import com.mysite.service.InventoryService;

@RestController
@RequestMapping({
	"/api/v1"
})
@Validated
public class InventoryResource {

	@Autowired
	private InventoryService service;
	
	@PostMapping("/inventory")
	@ResponseStatus(HttpStatus.CREATED)
	public CreateInventoryResponse createInventory(@Valid @RequestBody final CreateInventoryRequest req) {
		final long id = service.createInventory(req);
		return new CreateInventoryResponse(id);
	}
	
	@GetMapping("/inventory")
	public Page<Inventory> listInventory(@RequestParam @PositiveOrZero final Integer pageIndex, 
			@RequestParam(required=false) @Positive final Integer categoryId){
		return service.listInventory(pageIndex, categoryId);
	}
	
	@PatchMapping("/inventory/{id}")
	// currently only updating quantity, but it's better to maintain a generic interface
	public void updateInventory(@PathVariable @Positive @NotNull final Long id, 
			@Valid @RequestBody final UpdateInventoryRequest req) {
		service.updateInventoryQuantity(id, req.getQuantity());
	}

	@DeleteMapping("/inventory/{id}")
	public void deleteInventory(@PathVariable @Positive @NotNull final Long id) {
		service.deleteInventory(id);
	}
	
}

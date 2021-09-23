package com.mysite.service;

import java.util.List;
import java.util.Optional;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.entity.Category;
import com.mysite.entity.Inventory;
import com.mysite.repository.CategoryRepository;
import com.mysite.repository.InventoryRepository;
import com.mysite.rest.request.CreateInventoryRequest;

@Service
public class InventoryService {

	private static final int DEFAULT_PAGESIZE = 20;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Transactional
	public long createInventory(final CreateInventoryRequest req) {
		this.validateCreateInventoryInputs(req);
		final Inventory inventory = convertToEntity(req);
		return this.inventoryRepository.save(inventory).getId();
	}

	private Inventory convertToEntity(final CreateInventoryRequest req) {
		final Inventory inventory = new Inventory();
		inventory.setName(req.getName());
		inventory.setQuantity(req.getQuantity());
		final Category subCategory = new Category();
		subCategory.setId(req.getSubCategoryId());
		inventory.setSubCategory(subCategory);
		return inventory;
	}

	private void validateCreateInventoryInputs(final CreateInventoryRequest req) {
		if (req == null) {
			throw new ValidationException("missing inventory details");
		}
		// TODO add assumption to README for this valid characters
		this.validateInventoryCategory(req.getCategoryId());
		this.validateInventoryCategory(req.getSubCategoryId());
		final Optional<Category> subCategory = this.categoryRepository
				.findById(req.getSubCategoryId());
		if (!subCategory.isPresent() || (req.getCategoryId() != subCategory.get().getParent().getId())) {
			throw new ValidationException("invalid category/subcategory pair");
		}
	}

	private void validateInventoryCategory(final Integer categoryId) {
		if (categoryId == null) {
			throw new ValidationException("invalid inventory category");
		}
	}

	@Transactional(readOnly = true)
	public List<Inventory> listInventory(final int pageIndex,
			final Integer categoryId) {
		// TODO add to README for sortBy & pageIndex, and default page size
		// TODO dont need to prefetch category name, coz ui should get these reference data, add test for this
		final Pageable paging = PageRequest.of(pageIndex, DEFAULT_PAGESIZE,
				Sort.by("name", "id"));
		final Page<Inventory> page = this.inventoryRepository.findByCategory(
				categoryId == null ? 0 : categoryId, paging);
		return page.toList();
	}

	@Transactional
	public void updateInventoryQuantity(final long inventoryId,
			final int quantity) {
		if (inventoryId <= 0) {
			throw new ValidationException("invalid inventory");
		}
		final Optional<Inventory> inventory = this.inventoryRepository
				.findById(inventoryId);
		if (!inventory.isPresent()) {
			throw new ValidationException("inventory not exist");
		}
		final Inventory update = inventory.get();
		update.setQuantity(quantity);
		this.inventoryRepository.save(update);
	}

	@Transactional
	public void deleteInventory(final long inventoryId) {
		if (inventoryId <= 0) {
			throw new ValidationException("invalid inventory");
		}
		this.inventoryRepository.deleteById(inventoryId);
	}

}

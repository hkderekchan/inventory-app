package com.mysite.service;

import java.util.Optional;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
	@CacheEvict(value = "inventory", allEntries = true)
	public long createInventory(final CreateInventoryRequest req) {
		this.validateCreateInventoryInputs(req);
		final var inventory = convertToEntity(req);
		return this.inventoryRepository.save(inventory).getId();
	}

	private Inventory convertToEntity(final CreateInventoryRequest req) {
		final var inventory = new Inventory();
		inventory.setName(req.getName());
		inventory.setQuantity(req.getQuantity());
		final var subCategory = new Category();
		subCategory.setId(req.getSubCategoryId());
		inventory.setSubCategory(subCategory);
		return inventory;
	}

	private void validateCreateInventoryInputs(final CreateInventoryRequest req) {
		if (req == null) {
			throw new ValidationException("missing inventory details");
		}
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
	@Cacheable("inventory")
	public Page<Inventory> listInventory(final int pageIndex,
			final Integer categoryId) {
		final var paging = PageRequest.of(pageIndex, DEFAULT_PAGESIZE,
				Sort.by("name", "id"));
		return this.inventoryRepository.findByCategory(
				categoryId == null ? 0 : categoryId, paging);
	}

	@Transactional
	@CacheEvict(value = "inventory", allEntries = true)
	public void updateInventoryQuantity(final long inventoryId,
			final int quantity) {
		if (inventoryId <= 0) {
			throw new ValidationException("invalid inventory");
		}
		final var inventory = this.inventoryRepository
				.findById(inventoryId);
		if (!inventory.isPresent()) {
			throw new ValidationException("inventory not exist");
		}
		final var update = inventory.get();
		update.setQuantity(quantity);
		this.inventoryRepository.save(update);
	}

	@Transactional
	@CacheEvict(value = "inventory", allEntries = true)
	public void deleteInventory(final long inventoryId) {
		if (inventoryId <= 0) {
			throw new ValidationException("invalid inventory");
		}
		this.inventoryRepository.deleteById(inventoryId);
	}

}

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

@Service
public class InventoryService {

	private static final int DEFAULT_PAGESIZE = 20;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Transactional
	public long createInventory(final Inventory inventory) {
		this.validateCreateInventoryInputs(inventory);
		return this.inventoryRepository.save(inventory).getId();
	}

	private void validateCreateInventoryInputs(final Inventory inventory) {
		if (inventory == null) {
			throw new ValidationException("missing inventory details");
		}
		// TODO add assumption to README for this valid characters
		this.validateInventoryCategory(inventory.getCategory());
		this.validateInventoryCategory(inventory.getSubCategory());
		final Optional<Category> subCategory = this.categoryRepository
				.findById(inventory.getSubCategory().getId());
		if (!subCategory.isPresent() || (inventory.getCategory().getId() != subCategory.get().getParent()
				.getId())) {
			throw new ValidationException("invalid category/subcategory pair");
		}
	}

	private void validateInventoryCategory(final Category category) {
		if ((category == null) || (category.getId() == null)) {
			throw new ValidationException("invalid inventory category");
		}
	}

	@Transactional(readOnly = true)
	public List<Inventory> listInventory(final int pageIndex) {
		return this.listInventory(pageIndex, null);
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

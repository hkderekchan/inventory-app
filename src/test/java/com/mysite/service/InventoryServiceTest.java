package com.mysite.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;

import com.mysite.entity.Category;
import com.mysite.entity.Inventory;
import com.mysite.repository.CategoryRepository;
import com.mysite.repository.InventoryRepository;

public class InventoryServiceTest {

	@InjectMocks
	private InventoryService service;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private InventoryRepository inventoryRepository;

	@Test
	public void shouldFailToCreateInventoryIfNull() {
		try {
			this.service.createInventory(null);
			fail("should fail with null inventory");
		} catch (final ValidationException ex) {
			assertEquals("missing inventory details", ex.getMessage());
		}
	}

	@Test
	public void shouldFailToCreateInventoryIfInvalidCategory() {

		final Inventory inventory = new Inventory();
		try {
			this.service.createInventory(inventory);
			fail("should fail with missing category");
		} catch (final ValidationException ex) {
			assertEquals("invalid inventory category", ex.getMessage());
		}

		try {
			inventory.setCategory(new Category());
			inventory.setSubCategory(new Category());
			this.service.createInventory(inventory);
			fail("should fail with missing category");
		} catch (final ValidationException ex) {
			assertEquals("invalid inventory category", ex.getMessage());
		}

		try {
			inventory.setCategory(this.category(1));
			final Category subCategory = this.category(2);
			subCategory.setParent(this.category(3));
			inventory.setSubCategory(subCategory);
			when(this.categoryRepository
					.findById(eq(2))).thenReturn(Optional.empty());
			this.service.createInventory(inventory);
			fail("should fail with invalid category/subcategory pair");
		} catch (final ValidationException ex) {
			assertEquals("invalid category/subcategory pair", ex.getMessage());
		}

		try {
			inventory.setCategory(this.category(1));
			final Category subCategory = this.category(2);
			subCategory.setParent(this.category(3));
			inventory.setSubCategory(subCategory);
			when(this.categoryRepository
					.findById(eq(2))).thenReturn(Optional.of(subCategory));
			this.service.createInventory(inventory);
			fail("should fail with invalid category/subcategory pair");
		} catch (final ValidationException ex) {
			assertEquals("invalid category/subcategory pair", ex.getMessage());
		}

	}

	private Category category(final int id) {
		final Category category = new Category();
		category.setId(id);
		return category;
	}

	@Test
	public void shouldCreateInventory() {

		final Category category = this.category(1);
		final Category subCategory = this.category(2);
		subCategory.setParent(category);

		final Inventory inventory = new Inventory();
		inventory.setCategory(category);
		inventory.setSubCategory(subCategory);
		when(this.categoryRepository.findById(eq(2)))
		.thenReturn(Optional.of(subCategory));
		// mock successful save
		final Inventory saved = new Inventory();
		BeanUtils.copyProperties(inventory, saved);
		saved.setId(99L);
		when(this.inventoryRepository.save(any(Inventory.class)))
		.thenReturn(saved);
		this.service.createInventory(inventory);

		verify(this.inventoryRepository, times(1)).save(any(Inventory.class));
	}

	@Test
	public void shouldFindInventoryByCategory() {

	}

	@Test
	public void shouldUpdateInventoryQuantity() {

	}

	@Test
	public void shouldDeleteInventory() {

	}

	@Test
	public void shouldCreateCategory() {

	}

	@Test
	public void shouldListCategory() {

	}

	@Test
	public void shouldDeleteCategory() {

	}

}

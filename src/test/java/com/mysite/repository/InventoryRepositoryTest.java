package com.mysite.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mysite.entity.Category;
import com.mysite.entity.Inventory;

// TODO prepare schema.sql + data.sql
@DataJpaTest
public class InventoryRepositoryTest extends AbstractJpaTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	private Category createTopLevelCategory() {
		return this.createTopLevelCategory("Clothes");
	}

	private Category createTopLevelCategory(final String name) {
		final Category category = new Category();
		category.setName(name);
		return this.categoryRepository.save(category);
	}

	private Category createTwoLevelCategory() {
		return this.createSecondLevelCategory("Shorts",
				this.createTopLevelCategory());
	}

	private Category createSecondLevelCategory(final String name,
			final Category parentCategory) {
		final Category category = new Category();
		category.setName(name);
		category.setParent(parentCategory);
		return this.categoryRepository.save(category);
	}

	private Inventory createInventory(final String name,
			final Category category) {
		final Inventory inventory = new Inventory();
		inventory.setSubCategory(category);
		inventory.setName(name);
		inventory.setQuantity(0);
		return this.inventoryRepository.save(inventory);
	}

	@Test
	public void shouldSaveInventory() {

		final Inventory saved = this.createInventory("Paul Smith Shorts",
				this.createTwoLevelCategory());
		this.flushAndClear();
		assertNotNull(this.inventoryRepository.getById(saved.getId()));
	}

	@Test
	public void shouldFindByCategory() {
		final Category topLevelCategory = this.createTopLevelCategory();
		final List<Category> subCategories = new ArrayList<>(5);
		for (int i = 0; i < 4; i++) {
			final Category subCategory = this.createSecondLevelCategory(
					"subCategory-" + i, topLevelCategory);
			subCategories.add(subCategory);
			for (int j = 9; j > 0; j--) {
				this.createInventory(subCategory.getName() + "-inventory-" + j,
						subCategory);
			}
		}
		this.flushAndClear();

		final Pageable paging = PageRequest.of(1, 7, Sort.by("name"));
		List<Inventory> list = null;

		list = this.inventoryRepository.findByCategory(0, paging).toList();
		assertEquals(7, list.size());
		assertEquals("subCategory-0-inventory-8", list.get(0).getName());
		assertEquals("subCategory-1-inventory-5", list.get(6).getName());

		list = this.inventoryRepository
				.findByCategory(subCategories.get(0).getId(), paging).toList();
		assertEquals(2, list.size());
		assertEquals("subCategory-0-inventory-8", list.get(0).getName());
		assertEquals("subCategory-0-inventory-9", list.get(1).getName());
	}

	@Test
	public void shouldUpdateQuantity() {
		final Inventory saved = this.createInventory("Paul Smith Shorts", this.createTwoLevelCategory());
		this.flushAndClear();

		final Inventory update = this.inventoryRepository.findById(saved.getId()).get();
		assertEquals(0, update.getQuantity().intValue());
		update.setQuantity(3);
		this.inventoryRepository.save(update);
		this.flushAndClear();

		final Inventory updated = this.inventoryRepository.findById(saved.getId()).get();
		assertEquals(3, updated.getQuantity().intValue());
	}

	@Test
	public void shouldDeleteInventory() {
		final Inventory saved = this.createInventory("Paul Smith Shorts",
				this.createTwoLevelCategory());
		this.flushAndClear();
		assertEquals(1, this.inventoryRepository.findAll().size());

		this.inventoryRepository.deleteById(saved.getId());
		this.flushAndClear();
		assertEquals(0, this.inventoryRepository.findAll().size());
	}

	@Test
	public void shouldFailToSaveForInvalidName() {

		final Category category = this.createTwoLevelCategory();
		this.flushAndClear();

		try {
			final Inventory inventory = inventory(category, null, 0);
			this.inventoryRepository.save(inventory);
			this.flushAndClear();
			fail("missing name, shouldnt pass");
		} catch (final ConstraintViolationException ex) {
			this.expectConstraint(ex, "name", "constraints.NotBlank");
		}

		try {
			final Inventory inventory = inventory(category, "??", 0);
			this.inventoryRepository.save(inventory);
			this.flushAndClear();
			fail("invalid pattern, shouldnt pass");
		} catch (final ConstraintViolationException ex) {
			this.expectConstraint(ex, "name", "constraints.Pattern");
		}
	}

	@Test
	public void shouldFailToSaveForInvalidQuantity() {

		final Category category = this.createTwoLevelCategory();
		this.flushAndClear();


		try {
			final Inventory inventory = inventory(category, "Paul Smith Shorts", null);
			this.inventoryRepository.save(inventory);
			this.flushAndClear();
			fail("missing quantity, shouldnt pass");
		} catch (final ConstraintViolationException ex) {
			this.expectConstraint(ex, "quantity", "constraints.NotNull");
		}

		try {
			final Inventory inventory = inventory(category, "Paul Smith Shorts", null);
			inventory.setQuantity(-1);
			this.inventoryRepository.save(inventory);
			this.flushAndClear();
			fail("invalid quantity, shouldnt pass");
		} catch (final ConstraintViolationException ex) {
			this.expectConstraint(ex, "quantity", "constraints.PositiveOrZero");
		}

	}

	private Inventory inventory(final Category category, final String name, Integer quantity) {
		final Inventory inventory = new Inventory();
		inventory.setSubCategory(category);
		inventory.setName(name);
		inventory.setQuantity(quantity);
		return inventory;
	}

}

package com.mysite.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mysite.entity.Category;
import com.mysite.entity.Inventory;
import com.mysite.repository.CategoryRepository;
import com.mysite.repository.InventoryRepository;

// TODO check test coverage
@ExtendWith(SpringExtension.class)
public class InventoryServiceTest {

	@InjectMocks
	private InventoryService service;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private InventoryRepository inventoryRepository;

	@Captor
	private ArgumentCaptor<Integer> integerCaptor;
	
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
		return this.category(id, null, null);
	}

	private Category category(final int id, final String name, final Integer parentId) {
		final Category category = new Category();
		category.setId(id);
		category.setName(name);
		if(parentId != null) {
			final Category parentCategory = new Category();
			parentCategory.setId(parentId);
			category.setParent(parentCategory);
		}
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

		final Page<Inventory> page = Mockito.mock(Page.class);
		when(this.inventoryRepository.findByCategory(
				anyInt(), any(Pageable.class))).thenReturn(page);
		
		this.service.listInventory(3);
		verify(this.inventoryRepository, times(1)).findByCategory(integerCaptor.capture(),  any(Pageable.class));
		assertEquals(0, integerCaptor.getValue(), "no category specified, default is 0");
		
		this.service.listInventory(3, 5);
		verify(this.inventoryRepository, times(2)).findByCategory(integerCaptor.capture(),  any(Pageable.class));
		assertEquals(5, integerCaptor.getValue());
	}

	@Test
	public void shouldUpdateInventoryQuantity() {

		try {
			service.updateInventoryQuantity(0, 3);
			fail("shouldnt pass, invalid inventory");
		}catch(ValidationException ex) {
			assertEquals("invalid inventory", ex.getMessage());
		}
		
		try {
			when(this.inventoryRepository.findById(eq(1L))).thenReturn(Optional.empty());
			service.updateInventoryQuantity(1, 3);
			fail("shouldnt pass, inventory not exist");
		}catch(ValidationException ex) {
			assertEquals("inventory not exist", ex.getMessage());
		}
		
		final Inventory inventory = Mockito.mock(Inventory.class);		
		when(this.inventoryRepository.findById(eq(1L))).thenReturn(Optional.of(inventory));
		service.updateInventoryQuantity(1, 3);
		
		verify(inventory, times(1)).setQuantity(eq(3));
		verify(inventoryRepository, times(1)).save(eq(inventory));
	}

	@Test
	public void shouldDeleteInventory() {

		try {
			service.deleteInventory(-1);
			fail("shouldnt pass, invalid inventory");
		}catch(ValidationException ex) {
			assertEquals("invalid inventory", ex.getMessage());
		}

		service.deleteInventory(33);
		verify(inventoryRepository, times(1)).deleteById(eq(33L));
	}

	@Test
	public void shouldCreateCategory() {
		
		try {
			service.createCategory(null);
			fail("shouldnt pass, missing category");
		}catch(ValidationException ex) {
			assertEquals("missing category", ex.getMessage());
		}
		
		final Category category = new Category();
		category.setName("Clothes");
		final Category saved = new Category();
		BeanUtils.copyProperties(category, saved);
		saved.setId(99);
		when(categoryRepository.save(category)).thenReturn(saved);
		service.createCategory(category);
		verify(categoryRepository, times(1)).save(category);
	}

	@Test
	public void shouldListCategory() {

		final List<Category> categories = new ArrayList<>();
		categories.add(category(3, "A-1", 1));
		categories.add(category(1, "A-2", null));
		categories.add(category(5, "B-1", 2));
		categories.add(category(2, "B-2", null));
		categories.add(category(4, "B-3", 2));

		when(categoryRepository.findAll(any(Sort.class))).thenReturn(categories);
		
		final List<Category> topLevelCategories = service.groupCategories();
		assertEquals(2, topLevelCategories.size());
		final Category firstTopLevel = topLevelCategories.get(0);
		final Category secondTopLevel = topLevelCategories.get(1);
		assertName("A-2", firstTopLevel);
		assertName("B-2", secondTopLevel);

		final List<Category> firstTopLevelSubCategories = (List<Category>)firstTopLevel.getSubCategories();
		final List<Category> secondTopLevelSubCategories = (List<Category>)secondTopLevel.getSubCategories();
		assertName("A-1", firstTopLevelSubCategories.get(0));
		assertName("B-1", secondTopLevelSubCategories.get(0));
		assertName("B-3", secondTopLevelSubCategories.get(1));
	}

	private void assertName(final String expected, final Category category) {
		assertEquals(expected, category.getName());
	}

	@Test
	public void shouldDeleteCategory() {

		try {
			service.deleteCategory(-2);
			fail("shouldnt pass, invalid category");
		}catch(ValidationException ex) {
			assertEquals("invalid category", ex.getMessage());
		}
		
		service.deleteCategory(3);
		verify(this.categoryRepository, times(1)).deleteById(3);
	}

}

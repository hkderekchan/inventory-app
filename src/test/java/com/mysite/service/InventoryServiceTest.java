package com.mysite.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mysite.entity.Category;
import com.mysite.entity.Inventory;
import com.mysite.repository.CategoryRepository;
import com.mysite.repository.InventoryRepository;
import com.mysite.rest.request.CreateInventoryRequest;

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

	public void shouldFailToCreateInventoryIfInvalidCategory() {

		final CreateInventoryRequest req = new CreateInventoryRequest();
		try {
			this.service.createInventory(req);
			fail("should fail with missing category");
		} catch (final ValidationException ex) {
			assertEquals("invalid inventory category", ex.getMessage());
		}

		try {
			req.setCategoryId(1);;
			req.setSubCategoryId(2);
			when(this.categoryRepository
					.findById(eq(2))).thenReturn(Optional.empty());
			this.service.createInventory(req);
			fail("should fail with invalid category/subcategory pair");
		} catch (final ValidationException ex) {
			assertEquals("invalid category/subcategory pair", ex.getMessage());
		}

		try {
			final Category subCategory = this.category(2);
			subCategory.setParent(this.category(3));
			when(this.categoryRepository
					.findById(eq(2))).thenReturn(Optional.of(subCategory));
			this.service.createInventory(req);
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

		final CreateInventoryRequest req = new CreateInventoryRequest();
		req.setCategoryId(1);
		req.setSubCategoryId(2);

		when(this.categoryRepository.findById(eq(2))).thenReturn(Optional.of(subCategory));
		// mock successful save
		final Inventory saved = new Inventory();
		saved.setCategory(category);
		saved.setSubCategory(subCategory);
		saved.setId(99L);
		when(this.inventoryRepository.save(any(Inventory.class))).thenReturn(saved);
		this.service.createInventory(req);

		verify(this.inventoryRepository, times(1)).save(any(Inventory.class));
	}

	@Test
	public void shouldFindInventoryByCategory() {

		final Page<Inventory> page = Mockito.mock(Page.class);
		when(this.inventoryRepository.findByCategory(
				anyInt(), any(Pageable.class))).thenReturn(page);
		
		this.service.listInventory(3, null);
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

}

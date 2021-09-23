package com.mysite.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mysite.entity.Category;
import com.mysite.repository.CategoryRepository;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

	@InjectMocks
	private CategoryService service;

	@Mock
	private CategoryRepository categoryRepository;

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

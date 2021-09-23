package com.mysite.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import com.mysite.entity.Category;

// TODO prepare raml
@DataJpaTest
public class CategoryRepositoryTest extends AbstractJpaTest {

	@Autowired
	private CategoryRepository categoryRepository;

	private Category createTopLevelCategory() {
		return this.createTopLevelCategory("Clothes");
	}

	private Category createTopLevelCategory(final String name) {
		final Category category = new Category();
		category.setName(name);
		final Category saved = this.categoryRepository.save(category);
		this.flushAndClear();
		return this.categoryRepository.getById(saved.getId());
	}

	private Category createTwoLevelCategory() {
		final Category category = new Category();
		category.setName("Shorts");
		category.setParent(this.createTopLevelCategory());
		final Category saved = this.categoryRepository.save(category);
		this.flushAndClear();
		return this.categoryRepository.getById(saved.getId());
	}

	@Test
	public void shouldSaveTopLevelCategory() {
		final Category category = this.createTopLevelCategory();
		assertNotNull(category);
	}

	@Test
	public void shouldSaveSecondLevelCategory() {
		final Category secondLevelCategory = this.createTwoLevelCategory();
		assertNotNull(secondLevelCategory);
		this.assertRelationships(secondLevelCategory.getParent().getId(),
				secondLevelCategory.getId());
	}

	private void assertRelationships(final int firstLevelId,
			final int secondLevelId) {

		// check from parent
		final Category firstLevelCategory = this.categoryRepository
				.getById(firstLevelId);
		final Collection<Category> categories = firstLevelCategory
				.getSubCategories();
		assertEquals(1, categories.size());
		assertEquals(secondLevelId,
				categories.iterator().next().getId().longValue());

		// check from subCategory
		final Category secondLevelCategory = this.categoryRepository
				.getById(secondLevelId);
		assertEquals(firstLevelId,
				secondLevelCategory.getParent().getId().longValue());
	}

	@Test
	public void shouldSortByName() {
		this.createTopLevelCategory("Food");
		this.createTwoLevelCategory();
		final List<Category> categories = this.categoryRepository
				.findAll(Sort.by("name"));
		assertEquals(3, categories.size());
		assertEquals("Clothes", categories.get(0).getName());
		assertEquals("Food", categories.get(1).getName());
		assertEquals("Shorts", categories.get(2).getName());
	}

	@Test
	public void shouldFailToPersistForInvalidName() {

		final Category category = new Category();
		try {
			this.categoryRepository.save(category);
			this.flushAndClear();
			fail("missing name, shouldnt pass");
		} catch (final ConstraintViolationException ex) {
			this.expectConstraint(ex, "name", "constraints.NotBlank");
		}

		try {
			category.setName("??");
			this.categoryRepository.save(category);
			this.flushAndClear();
			fail("invalid pattern, shouldnt pass");
		} catch (final ConstraintViolationException ex) {
			this.expectConstraint(ex, "name", "constraints.Pattern");
		}
	}

	@Test
	public void shouldDeleteCategory() {
		final Category category1 = this.createTwoLevelCategory();
		final Category category2 = this.createTopLevelCategory("Food");
		this.assertCategoryCount(3);

		this.categoryRepository.deleteById(category1.getId());
		this.flushAndClear();
		this.assertCategoryCount(2);

		this.categoryRepository.deleteById(category2.getId());
		this.flushAndClear();
		this.assertCategoryCount(1);
	}

	private void assertCategoryCount(final int expected) {
		final List<Category> categories = this.categoryRepository.findAll();
		assertEquals(expected, categories.size());
	}

}

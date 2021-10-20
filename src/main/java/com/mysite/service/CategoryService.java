package com.mysite.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.LinkedListMultimap;
import com.mysite.entity.Category;
import com.mysite.repository.CategoryRepository;
import com.mysite.rest.request.CreateCategoryRequest;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional
	@CacheEvict(value = "categories", allEntries = true)
	public int createCategory(final CreateCategoryRequest req) {
		if(req == null){
			throw new ValidationException("missing category");
		}
		final var category = convertToEntity(req);
		return this.categoryRepository.save(category).getId();
	}

	private Category convertToEntity(final CreateCategoryRequest req) {
		final var category = new Category();
		category.setName(req.getName());
		final var parentId = req.getParent();
		if(parentId != null) {
			final var parent = new Category();
			parent.setId(parentId);
			category.setParent(parent);
		}
		return category;
	}

	@Transactional(readOnly = true)
	@Cacheable("categories")
	// return list of top level categories, and having the whole category tree initialized
	public List<Category> groupCategories() {
		// fetch all categories and build the tree programmatically, instead of relying on JPA to fetch the children;
		// this is to avoid issuing multiple queries to db, that should be even slower
		final var categories = this.categoryRepository
				.findAll(Sort.by("name"));
		final var topLevelCategories = new ArrayList<Category>(
				categories.size() / 2);
		// index the categories in map
		final var categoryMap = new HashMap<Integer, Category>();
		final var subCategoryMap = LinkedListMultimap.<Integer, Category>create();
		for (final Category category : categories) {
			categoryMap.put(category.getId(), category);
			if (category.getParent() != null) {
				subCategoryMap.put(category.getParent().getId(), category);
			} else {
				topLevelCategories.add(category);
			}
		}
		// build the relationship
		for (final var entry : subCategoryMap.asMap().entrySet()) {
			categoryMap.get(entry.getKey()).setSubCategories(entry.getValue());
		}
		return topLevelCategories;
	}

	@Transactional
	@CacheEvict(value = "categories", allEntries = true)
	public void deleteCategory(final int categoryId) {
		if(categoryId <= 0) {
			throw new ValidationException("invalid category");
		}
		this.categoryRepository.deleteById(categoryId);
	}

}

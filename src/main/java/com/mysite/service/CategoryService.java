package com.mysite.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		final Category category = convertToEntity(req);
		return this.categoryRepository.save(category).getId();
	}

	private Category convertToEntity(final CreateCategoryRequest req) {
		final Category category = new Category();
		category.setName(req.getName());
		final Integer parentId = req.getParent();
		if(parentId != null) {
			final Category parent = new Category();
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
		final List<Category> categories = this.categoryRepository
				.findAll(Sort.by("name"));
		final List<Category> topLevelCategories = new ArrayList<>(
				categories.size() / 2);
		// index the categories in map
		final Map<Integer, Category> categoryMap = new HashMap<>();
		final LinkedListMultimap<Integer, Category> subCategoryMap = LinkedListMultimap.create();
		for (final Category category : categories) {
			categoryMap.put(category.getId(), category);
			if (category.getParent() != null) {
				subCategoryMap.put(category.getParent().getId(), category);
			} else {
				topLevelCategories.add(category);
			}
		}
		// build the relationship
		for (final Entry<Integer,Collection<Category>> entry : subCategoryMap.asMap().entrySet()) {
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

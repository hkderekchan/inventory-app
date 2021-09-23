package com.mysite.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.mysite.entity.Category;
import com.mysite.repository.CategoryRepository;
import com.mysite.rest.request.CreateCategoryRequest;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional
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
	// return list of top level categories, and having the whole category tree initialized
	public List<Category> groupCategories() {
		// TODO add assumption, wont be more than a hundred for all levels

		// fetch all categories and build the tree programmatically, instead of relying on JPA to fetch the children;
		// this is to avoid issuing multiple queries to db, that should be even slower
		final List<Category> categories = this.categoryRepository
				.findAll(Sort.by("name"));
		final List<Category> topLevelCategories = new ArrayList<>(
				categories.size() / 2);
		final Multimap<Integer, Category> groupByParent = LinkedListMultimap
				.create();
		for (final Category category : categories) {
			if (category.getParent() == null) {
				topLevelCategories.add(category);
			} else {
				groupByParent.put(category.getParent().getId(), category);
			}
		}
		// TDDO add to readme; db schema supports multi level categories but here, we assume 2 levels first
		// TODO make it support multi level properly
		for (final Category topLevelCategory : topLevelCategories) {
			topLevelCategory.setSubCategories(
					groupByParent.get(topLevelCategory.getId()));
		}
		return topLevelCategories;
	}

	@Transactional
	public void deleteCategory(final int categoryId) {
		if(categoryId <= 0) {
			throw new ValidationException("invalid category");
		}
		this.categoryRepository.deleteById(categoryId);
	}

}

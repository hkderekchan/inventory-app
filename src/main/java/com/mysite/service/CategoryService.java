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

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional
	public long createCategory(final Category category) {
		if(category == null){
			throw new ValidationException("missing category");
		}
		return this.categoryRepository.save(category).getId();
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

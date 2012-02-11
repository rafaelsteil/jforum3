/*
 * Copyright (c) JForum Team. All rights reserved.
 *
 * The software in this package is published under the terms of the LGPL
 * license a copy of which has been included with this distribution in the
 * license.txt file.
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.services;

import java.util.List;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Category;
import net.jforum.repository.CategoryRepository;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class CategoryService {
	private CategoryRepository repository;

	public CategoryService(CategoryRepository repository) {
		this.repository = repository;
	}

	/**
	 * Adds a new category
	 * @param category
	 * @throws ValidationException if the instance is not good for saving
	 */
	public void add(Category category) {
		this.applyCommonConstraints(category);

		if (category.getId() > 0) {
			throw new ValidationException("This appears to be an existing category (id > 0). Please use update() instead");
		}

		this.repository.add(category);
	}

	/**
	 * Deletes on or more categories
	 * @param ids
	 */
	public void delete(int... ids) {
		if (ids != null) {
			for (int id : ids) {
				Category c = this.repository.get(id);
				this.repository.remove(c);
			}
		}
	}

	/**
	 * Updates an existing category
	 * @param category
	 */
	public void update(Category category) {
		this.applyCommonConstraints(category);

		if (category.getId() == 0) {
			throw new ValidationException("update() expects a category with an existing id");
		}

		this.repository.update(category);
	}

	/**
	 * Changes the category order one level up
	 * @param categoryId
	 */
	public void upCategoryOrder(int categoryId) {
		this.processOrdering(true, categoryId);
	}

	/**
	 * Changes the category order one level down
	 * @param categoryId
	 */
	public void downCategoryOrder(int categoryId) {
		this.processOrdering(false, categoryId);
	}

	/**
	 * Changes the order of the specified category, adding it
	 * one level or one level down
	 * @param up if true, sets the category one level up. If false, one level down
	 * @param categoryId the id of the category to change
	 */
	private void processOrdering(boolean up, int categoryId) {
		Category toChange = this.repository.get(categoryId);
		List<Category> categories = this.repository.getAllCategories();

		int index = categories.indexOf(toChange);

		if (index > -1 && (up && index > 0) || (!up && index + 1 < categories.size())) {
			Category otherCategory = up ? categories.get(index - 1) : categories.get(index + 1);

			int oldOrder = toChange.getDisplayOrder();

			toChange.setDisplayOrder(otherCategory.getDisplayOrder());
			otherCategory.setDisplayOrder(oldOrder);

			this.repository.update(toChange);
			this.repository.update(otherCategory);
		}
	}

	private void applyCommonConstraints(Category c) {
		if (c == null) {
			throw new NullPointerException("Cannot save a null category");
		}

		if (StringUtils.isEmpty(c.getName())) {
			throw new ValidationException("Category name cannot be blank or null");
		}
	}
}

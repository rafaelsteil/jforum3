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
package net.jforum.controllers;

import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.Category;
import net.jforum.repository.CategoryRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.CategoryService;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.CATEGORIES_ADMIN)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class CategoryAdminController {
	private CategoryRepository categoryRepository;
	private CategoryService categoryService;
	private final Result result;

	public CategoryAdminController(CategoryRepository categoryRepository,
			CategoryService categoryService, Result result) {
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
		this.result = result;
	}

	public void add() {

	}

	/**
	 * Add a new category
	 *
	 * @param category
	 */
	public void addSave(Category category) {
		this.categoryService.add(category);
		this.result.redirectTo(this).list();
	}

	/**
	 * Edit an existing category
	 *
	 * @param categoryId
	 */
	public void edit(int categoryId) {
		this.result.include("category", this.categoryRepository.get(categoryId));
		this.result.forwardTo(this).add();
	}

	/**
	 * Saves the information of an existing category
	 *
	 * @param category
	 */
	public void editSave(Category category) {
		this.categoryService.update(category);
		this.result.redirectTo(this).list();
	}

	/**
	 * Removes a list of categories
	 */
	public void delete(int... categoriesId) {
		this.categoryService.delete(categoriesId);
		this.result.redirectTo(this).list();
	}

	/**
	 * List all existing categories
	 */
	public void list() {
		this.result.include("categories", this.categoryRepository.getAllCategories());
	}

	/**
	 * Changes the order of the specified category, adding it one level up.
	 *
	 * @param categoryId
	 *            the id of the category to change
	 */
	public void up(int categoryId) {
		this.categoryService.upCategoryOrder(categoryId);
		this.result.redirectTo(this).list();
	}

	/**
	 * Changes the order of the specified category, adding it one level down.
	 *
	 * @param categoryId
	 *            the id of the category to change
	 */
	public void down(int categoryId) {
		this.categoryService.downCategoryOrder(categoryId);
		this.result.redirectTo(this).list();
	}
}

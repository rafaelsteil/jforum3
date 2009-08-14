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
package net.jforum.actions;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Category;
import net.jforum.repository.CategoryRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.CategoryService;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.CATEGORIES_ADMIN)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class CategoryAdminActions {
	private CategoryRepository categoryRepository;
	private CategoryService categoryService;
	private ViewPropertyBag propertyBag;
	private ViewService viewService;

	public CategoryAdminActions(CategoryRepository categoryRepository, CategoryService categoryService,
		ViewPropertyBag propertyBag, ViewService viewService) {
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
		this.propertyBag = propertyBag;
		this.viewService = viewService;
	}

	public void add() {

	}

	/**
	 * Add a new category
	 * @param category
	 */
	public void addSave(@Parameter(key = "category") Category category) {
		this.categoryService.add(category);
		this.viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Edit an existing category
	 * @param categoryId
	 */
	public void edit(@Parameter(key = "categoryId") int categoryId) {
		this.propertyBag.put("category", this.categoryRepository.get(categoryId));
		this.viewService.renderView(Actions.ADD);
	}

	/**
	 * Saves the information of an existing category
	 * @param category
	 */
	public void editSave(@Parameter(key = "category") Category category) {
		this.categoryService.update(category);
		this.viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Removes a list of categories
	 */
	public void delete(@Parameter(key = "categoriesId") int... categoriesId) {
		this.categoryService.delete(categoriesId);
		this.viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * List all existing categories
	 */
	public void list() {
		this.propertyBag.put("categories", this.categoryRepository.getAllCategories());
	}

	/**
	 * Changes the order of the specified category, adding it one level up.
	 * @param categoryId the id of the category to change
	 */
	public void up(@Parameter(key = "categoryId") int categoryId) {
		this.categoryService.upCategoryOrder(categoryId);
		this.viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Changes the order of the specified category, adding it one level down.
	 * @param categoryId the id of the category to change
	 */
	public void down(@Parameter(key = "categoryId") int categoryId) {
		this.categoryService.downCategoryOrder(categoryId);
		this.viewService.redirectToAction(Actions.LIST);
	}
}

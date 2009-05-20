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
import net.jforum.actions.interceptors.ExtensibleInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Forum;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.ForumService;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.FORUMS_ADMIN)
@InterceptedBy({ActionSecurityInterceptor.class, ExtensibleInterceptor.class})
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class ForumAdminActions {
	private CategoryRepository categoryRepository;
	private ForumRepository forumRepository;
	private ForumService forumService;
	private ViewPropertyBag propertyBag;
	private ViewService viewService;
	private SessionManager sessionManager;

	public ForumAdminActions(ForumService service, ForumRepository forumRepository, CategoryRepository categoryRepository,
		ViewPropertyBag propertyBag, ViewService viewService, SessionManager sessionManager) {
		forumService = service;
		this.categoryRepository = categoryRepository;
		this.forumRepository = forumRepository;
		this.propertyBag = propertyBag;
		this.sessionManager = sessionManager;
		this.viewService = viewService;
	}

	/**
	 * List all existing forums.
	 * In fact, this method act on top of all categories,
	 * where the forums are retrieved from.
	 */
	public void list() {
		propertyBag.put("categories", categoryRepository.getAllCategories());
	}

	/**
	 * Removes a list of forums
	 */
	public void delete(@Parameter(key = "forumsId") int... forumsId) {
		RoleManager roleManager = sessionManager.getUserSession().getRoleManager();

		if (roleManager.isAdministrator()) {
			forumService.delete(forumsId);
		}

		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Shows the page to add a new forum
	 */
	public void add() {
		propertyBag.put("categories", categoryRepository.getAllCategories());
	}

	/**
	 * Saves a new forum
	 * @param forum
	 */
	public void addSave(@Parameter(key = "forum") Forum forum) {
		RoleManager roleManager = sessionManager.getUserSession().getRoleManager();

		if (roleManager.isAdministrator() || roleManager.isCategoryAllowed(forum.getCategory())) {
			forumService.add(forum);
			propertyBag.put("forum", forum);
		}

		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Shows the page to edit a forum
	 * @param forumId
	 */
	public void edit(@Parameter(key = "forumId") int forumId) {
		propertyBag.put("forum", forumRepository.get(forumId));
		propertyBag.put("categories", categoryRepository.getAllCategories());
		viewService.renderView(Actions.ADD);
	}

	/**
	 * Updates the data of an existing forum
	 * @param forum
	 */
	public void editSave(@Parameter(key = "forum") Forum forum) {
		RoleManager roleManager = sessionManager.getUserSession().getRoleManager();

		if (roleManager.isAdministrator() || roleManager.isCategoryAllowed(forum.getCategory())) {
			forumService.update(forum);
		}

		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Changes the order of the specified category, adding it one level up.
	 * @param forumId the id of the category to change
	 */
	public void up(@Parameter(key = "forumId") int forumId) {
		forumService.upForumOrder(forumId);
		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Changes the order of the specified category, adding it one level down.
	 * @param forumId the id of the category to change
	 */
	public void down(@Parameter(key = "forumId") int forumId) {
		forumService.downForumOrder(forumId);
		viewService.redirectToAction(Actions.LIST);
	}
}

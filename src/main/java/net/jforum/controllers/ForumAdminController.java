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
import net.jforum.entities.Forum;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.ForumService;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.FORUMS_ADMIN)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class ForumAdminController {
	private CategoryRepository categoryRepository;
	private ForumRepository forumRepository;
	private ForumService forumService;
	private final Result result;
	private final UserSession userSession;

	public ForumAdminController(ForumService service, ForumRepository forumRepository,
			CategoryRepository categoryRepository, Result result, UserSession userSession) {
		this.forumService = service;
		this.categoryRepository = categoryRepository;
		this.forumRepository = forumRepository;
		this.result = result;
		this.userSession = userSession;
	}

	/**
	 * List all existing forums. In fact, this method act on top of all
	 * categories, where the forums are retrieved from.
	 */
	public void list() {
		this.result.include("categories",
			this.categoryRepository.getAllCategories());
	}

	/**
	 * Removes a list of forums
	 */
	public void delete(int... forumsId) {
		RoleManager roleManager = this.userSession.getRoleManager();

		if (roleManager.isAdministrator()) {
			this.forumService.delete(forumsId);
		}

		this.result.redirectTo(this).list();
	}

	/**
	 * Shows the page to add a new forum
	 */
	public void add() {
		this.result.include("categories", this.categoryRepository.getAllCategories());
	}

	/**
	 * Saves a new forum
	 *
	 * @param forum
	 */
	public void addSave(Forum forum) {
		RoleManager roleManager = this.userSession.getRoleManager();

		if (roleManager.isAdministrator() || roleManager.isCategoryAllowed(forum.getCategory().getId())) {
			this.forumService.add(forum);
			this.result.include("forum", forum);
		}

		this.result.redirectTo(this).list();
	}

	/**
	 * Shows the page to edit a forum
	 *
	 * @param forumId
	 */
	public void edit(int forumId) {
		RoleManager roleManager = this.userSession.getRoleManager();

		if (!roleManager.getCanModerateForum(forumId)) {
			this.result.redirectTo(this).list();
		}
		else {
			this.result.include("forum", this.forumRepository.get(forumId));
			this.result.include("categories", this.categoryRepository.getAllCategories());
			this.result.forwardTo(this).add();
		}
	}

	/**
	 * Updates the data of an existing forum
	 *
	 * @param forum
	 */
	public void editSave(Forum forum) {
		RoleManager roleManager = this.userSession
				.getRoleManager();

		if (roleManager.isAdministrator()
				|| roleManager.getCanModerateForum(forum.getId())) {
			this.forumService.update(forum);
		}

		this.result.redirectTo(this).list();
	}

	/**
	 * Changes the order of the specified category, adding it one level up.
	 *
	 * @param forumId
	 *            the id of the category to change
	 */
	public void up(int forumId) {
		RoleManager roleManager = this.userSession.getRoleManager();

		if (roleManager.getCanModerateForum(forumId)) {
			this.forumService.upForumOrder(forumId);
		}

		this.result.redirectTo(this).list();
	}

	/**
	 * Changes the order of the specified category, adding it one level down.
	 *
	 * @param forumId
	 *            the id of the category to change
	 */
	public void down(int forumId) {
		RoleManager roleManager = this.userSession.getRoleManager();

		if (roleManager.getCanModerateForum(forumId)) {
			this.forumService.downForumOrder(forumId);
		}

		this.result.redirectTo(this).list();
	}
}

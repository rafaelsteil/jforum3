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

import java.util.List;

import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.UserService;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.USERS_ADMIN)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class UserAdminController {
	private UserRepository userRepository;
	private GroupRepository groupRepository;
	private JForumConfig config;
	private UserService userService;
	private final Result result;
	private final UserSession userSession;

	public UserAdminController(UserRepository repository, GroupRepository groupRepository, JForumConfig config,
			UserService userService, Result result, UserSession userSession) {
		this.userRepository = repository;
		this.groupRepository = groupRepository;
		this.config = config;
		this.userService = userService;
		this.result = result;
		this.userSession = userSession;
	}

	/**
	 * Shows the page to edit an user
	 *
	 * @param userId the id of the user to edit
	 */
	public void edit(int userId) {
		result.forwardTo(UserController.class).edit(userId);
	}

	/**
	 * Shows the page to edit the user groups
	 *
	 * @param userId
	 */
	public void groups(int userId) {
		this.result.include("user", this.userRepository.get(userId));
		this.result.include("groups", this.groupRepository.getAllGroups());
	}

	public void lockUnlock(int[] userIds) {
		result.of(this).list(0);
	}

	/**
	 * Save the groups
	 *
	 * @param userId the user id
	 * @param groupIds the id of the groups for the user
	 */
	public void groupsSave(int userId, int... groupIds) {
		RoleManager roleManager = this.userSession.getRoleManager();
		boolean canSave = roleManager.isAdministrator();

		if (!canSave) {
			canSave = true;

			for (int groupId : groupIds) {
				canSave = canSave && roleManager.isGroupManager(groupId);
			}
		}

		if (canSave) {
			this.userService.saveGroups(userId, groupIds);
		}

		this.result.redirectTo(this).list(0);
	}

	/**
	 * Search for users
	 *
	 * @param username the username to search
	 */
	public void search(String username) {
		List<User> users = this.userRepository.findByUserName(username);
		this.result.include("users", users);
		this.result.include("username", username);

		this.result.of(this).list(0);
	}

	/**
	 * List all users
	 *
	 * @param page
	 */
	public void list(int page) {
		Pagination pagination = new Pagination(this.config, page).forUsers(this.userRepository.getTotalUsers());

		this.result.include("pagination", pagination);
		this.result.include("users", this.userRepository.getAllUsers(pagination.getStart(), pagination.getRecordsPerPage()));
	}
}

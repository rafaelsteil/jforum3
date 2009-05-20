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

import java.util.List;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.actions.interceptors.ExternalUserManagementInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.User;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.UserService;
import net.jforum.services.ViewService;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;
import org.vraptor.plugin.interceptor.MethodInterceptorInterceptor;

/**
 * @author Rafael Steil
 */
@Component(Domain.USERS_ADMIN)
@InterceptedBy({MethodInterceptorInterceptor.class,ActionSecurityInterceptor.class})
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class UserAdminActions {
	private UserRepository userRepository;
	private GroupRepository groupRepository;
	private ViewPropertyBag propertyBag;
	private JForumConfig config;
	private ViewService viewService;
	private UserService userService;
	private SessionManager sessionManager;

	public UserAdminActions(UserRepository repository, GroupRepository groupRepository, ViewPropertyBag propertyBag,
			JForumConfig config, ViewService viewService, UserService userService, SessionManager sessionManager) {
		userRepository = repository;
		this.groupRepository = groupRepository;
		this.propertyBag = propertyBag;
		this.config = config;
		this.viewService = viewService;
		this.userService = userService;
		this.sessionManager = sessionManager;
	}

	/**
	 * Shows the page to edit an user
	 * @param userId the id of the user to edit
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void edit(@Parameter(key = "userId") int userId) {
		propertyBag.put("user", userRepository.get(userId));
		viewService.renderView(Domain.USER, Actions.EDIT);
	}

	/**
	 * Shows the page to edit the user groups
	 * @param userId
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void groups(@Parameter(key = "userId") int userId) {
		propertyBag.put("user", userRepository.get(userId));
		propertyBag.put("groups", groupRepository.getAllGroups());
	}

	public void lockUnlock(@Parameter(key = "userIds") int[] userIds) {

	}

	/**
	 * Save the groups
	 * @param userId the user id
	 * @param groupIds the id of the groups for the user
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void groupsSave(@Parameter(key = "userId") int userId, @Parameter(key = "groupIds") int... groupIds) {
		RoleManager roleManager = sessionManager.getUserSession().getRoleManager();
		boolean canSave = roleManager.isAdministrator();

		if (!canSave) {
			canSave = true;

			for (int groupId : groupIds) {
				canSave = canSave && roleManager.isGroupManager(groupId);
			}
		}

		if (canSave) {
			userService.saveGroups(userId, groupIds);
		}

		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Search for users
	 * @param username the username to search
	 */
	public void search(@Parameter(key = "username") String username) {
		List<User> users = userRepository.findByUserName(username);
		propertyBag.put("users", users);
		propertyBag.put("username", username);
		viewService.renderView(Actions.LIST);
	}

	/**
	 * List all users
	 * @param page
	 */
	public void list(@Parameter(key = "page") int page) {
		Pagination pagination = new Pagination(config, page)
			.forUsers(userRepository.getTotalUsers());

		propertyBag.put("pagination", pagination);
		propertyBag.put("users", userRepository.getAllUsers(pagination.getStart(),
			pagination.getRecordsPerPage()));
	}
}

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

import java.util.Arrays;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PermissionOptions;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.actions.interceptors.ExternalUserManagementInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Group;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.GroupRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.GroupService;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;
import org.vraptor.plugin.interceptor.MethodInterceptorInterceptor;

/**
 * @author Rafael Steil
 */
@Component(Domain.GROUPS_ADMIN)
@InterceptedBy({MethodInterceptorInterceptor.class,ActionSecurityInterceptor.class})
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class GroupAdminActions {
	private GroupRepository groupRepository;
	private CategoryRepository categoryRepository;
	private GroupService service;
	private ViewPropertyBag propertyBag;
	private ViewService viewService;
	private SessionManager sessionManager;

	public GroupAdminActions(GroupService service, GroupRepository repository, SessionManager sessionManager,
		ViewPropertyBag propertyBag, ViewService viewService, CategoryRepository categoryRepository) {
		this.service = service;
		this.groupRepository = repository;
		this.viewService = viewService;
		this.propertyBag = propertyBag;
		this.categoryRepository = categoryRepository;
		this.sessionManager = sessionManager;
	}

	/**
	 * Shows the page to set permissions for a specific group
	 * @param groupId the group id
	 */
	public void permissions(@Parameter(key = "groupId") int groupId) {
		Group group = this.groupRepository.get(groupId);

		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

		if (!roleManager.isAdministrator() && !roleManager.isGroupManager(groupId)) {
			this.viewService.redirectToAction(Actions.LIST);
		}
		else {
			this.propertyBag.put("group", group);
			this.propertyBag.put("groups", this.groupRepository.getAllGroups());
			this.propertyBag.put("categories", this.categoryRepository.getAllCategories());
			this.propertyBag.put("permissions", this.convertRolesToPermissionOptions(group));
		}
	}

	/**
	 * Save the permissions for this group
	 * @param groupId the id of the group to save
	 * @param permissions the set of permissions of this group
	 */
	public void permissionsSave(@Parameter(key = "groupId") int groupId, @Parameter(key = "permission") PermissionOptions permissions) {
		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();
		if (roleManager.isAdministrator() || roleManager.isGroupManager(groupId)) {
			this.service.savePermissions(groupId, permissions);
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * List all existing groups
	 */
	public void list() {
		this.propertyBag.put("groups", this.groupRepository.getAllGroups());
	}

	/**
	 * Shows the page to add a new group
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void add() {
		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

		if (!roleManager.isAdministrator()) {
			this.viewService.redirectToAction(Actions.LIST);
		}
	}

	/**
	 * Delete one or more groups
	 * @param groupId the id of the groups to delete
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void delete(@Parameter(key = "groupId") int... groupId) {
		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

		if (roleManager.isAdministrator()) {
			this.service.delete(groupId);
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Shows the page to edit a group
	 * @param groupId
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void edit(@Parameter(key = "groupId") int groupId) {
		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

		if (!roleManager.isAdministrator() && !roleManager.isGroupManager(groupId)) {
			this.viewService.redirectToAction(Actions.LIST);
		}
		else {
			this.propertyBag.put("group", this.groupRepository.get(groupId));
			this.viewService.renderView(Actions.ADD);
		}
	}

	/**
	 * Saves the new information of an existing group
	 * @param group
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void editSave(@Parameter(key = "group") Group group) {
		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

		if (roleManager.isAdministrator() || roleManager.isGroupManager(group.getId())) {
			this.service.update(group);
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Save a new grop
	 * @param group
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void addSave(@Parameter(key = "group") Group group) {
		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

		if (roleManager.isAdministrator()) {
			this.service.add(group);
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	private PermissionOptions convertRolesToPermissionOptions(Group group) {
		RoleManager manager = new RoleManager();
		manager.setGroups(Arrays.asList(group));

		return manager.asPermissionOptions();
	}
}

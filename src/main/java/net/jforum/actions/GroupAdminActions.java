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

import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PermissionOptions;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.entities.Group;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.GroupRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.GroupService;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.GROUPS_ADMIN)
// @InterceptedBy({MethodInterceptorInterceptor.class,ActionSecurityInterceptor.class})
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class GroupAdminActions {
	private GroupRepository groupRepository;
	private CategoryRepository categoryRepository;
	private GroupService service;
	private SessionManager sessionManager;
	private final Result result;

	public GroupAdminActions(GroupService service, GroupRepository repository,
			SessionManager sessionManager,
			CategoryRepository categoryRepository, Result result) {
		this.service = service;
		this.groupRepository = repository;
		this.categoryRepository = categoryRepository;
		this.sessionManager = sessionManager;
		this.result = result;
	}

	/**
	 * Shows the page to set permissions for a specific group
	 * 
	 * @param groupId
	 *            the group id
	 */
	public void permissions(int groupId) {
		Group group = this.groupRepository.get(groupId);

		RoleManager roleManager = this.sessionManager.getUserSession()
				.getRoleManager();

		if (!roleManager.isAdministrator()
				&& !roleManager.isGroupManager(groupId)) {
			this.result.redirectTo(this).list();
		} else {
			this.result.include("group", group);
			this.result.include("groups", this.groupRepository.getAllGroups());
			this.result.include("categories",
					this.categoryRepository.getAllCategories());
			this.result.include("permissions",
					this.convertRolesToPermissionOptions(group));
		}
	}

	/**
	 * Save the permissions for this group
	 * 
	 * @param groupId
	 *            the id of the group to save
	 * @param permissions
	 *            the set of permissions of this group
	 */
	public void permissionsSave(int groupId, PermissionOptions permissions) {
		RoleManager roleManager = this.sessionManager.getUserSession()
				.getRoleManager();
		if (roleManager.isAdministrator()
				|| roleManager.isGroupManager(groupId)) {
			this.service.savePermissions(groupId, permissions);
		}

		this.result.redirectTo(this).list();
	}

	/**
	 * List all existing groups
	 */
	public void list() {
		this.result.include("groups", this.groupRepository.getAllGroups());
	}

	/**
	 * Shows the page to add a new group
	 */
	// @InterceptedBy(ExternalUserManagementInterceptor.class)
	public void add() {
		RoleManager roleManager = this.sessionManager.getUserSession()
				.getRoleManager();

		if (!roleManager.isAdministrator()) {
			this.result.redirectTo(this).list();
		}
	}

	/**
	 * Delete one or more groups
	 * 
	 * @param groupId
	 *            the id of the groups to delete
	 */
	// @InterceptedBy(ExternalUserManagementInterceptor.class)
	public void delete(int... groupId) {
		RoleManager roleManager = this.sessionManager.getUserSession()
				.getRoleManager();

		if (roleManager.isAdministrator()) {
			this.service.delete(groupId);
		}

		this.result.redirectTo(this).list();
	}

	/**
	 * Shows the page to edit a group
	 * 
	 * @param groupId
	 */
	// @InterceptedBy(ExternalUserManagementInterceptor.class)
	public void edit(int groupId) {
		RoleManager roleManager = this.sessionManager.getUserSession()
				.getRoleManager();

		if (!roleManager.isAdministrator()
				&& !roleManager.isGroupManager(groupId)) {
			this.result.redirectTo(this).list();
		} else {
			this.result.include("group", this.groupRepository.get(groupId));
			this.result.forwardTo(this).add();
		}
	}

	/**
	 * Saves the new information of an existing group
	 * 
	 * @param group
	 */
	// @InterceptedBy(ExternalUserManagementInterceptor.class)
	public void editSave(Group group) {
		RoleManager roleManager = this.sessionManager.getUserSession()
				.getRoleManager();

		if (roleManager.isAdministrator()
				|| roleManager.isGroupManager(group.getId())) {
			this.service.update(group);
		}

		this.result.redirectTo(this).list();
	}

	/**
	 * Save a new grop
	 * 
	 * @param group
	 */
	// @InterceptedBy(ExternalUserManagementInterceptor.class)
	public void addSave(Group group) {
		RoleManager roleManager = this.sessionManager.getUserSession()
				.getRoleManager();

		if (roleManager.isAdministrator()) {
			this.service.add(group);
		}

		this.result.redirectTo(this).list();
	}

	private PermissionOptions convertRolesToPermissionOptions(Group group) {
		RoleManager manager = new RoleManager();
		manager.setGroups(Arrays.asList(group));

		return manager.asPermissionOptions();
	}
}

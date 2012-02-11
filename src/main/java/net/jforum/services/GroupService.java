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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Group;
import net.jforum.entities.Role;
import net.jforum.entities.UserSession;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.SecurityConstants;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class GroupService {
	private GroupRepository repository;
	private UserRepository userRepository;
	private UserSession userSession;
	private SessionManager sessionManager;

	public GroupService(GroupRepository repository, UserRepository userRepository,
			UserSession userSession, SessionManager sessionManager) {
		this.repository = repository;
		this.userSession = userSession;
		this.userRepository = userRepository;
		this.sessionManager = sessionManager;
	}

	/**
	 * Save the permissions for this group
	 */
	@SuppressWarnings("unchecked")
	public void savePermissions(int groupId, Map<String, Map<String, List<?>>> map) {
		Group group = this.repository.get(groupId);

		RoleManager currentRoles = new RoleManager();
		currentRoles.setGroups(Arrays.asList(group));

		group.getRoles().clear();

		boolean isAdministrator = currentRoles.isAdministrator();
		boolean canManageForums = currentRoles.roleExists(SecurityConstants.CAN_MANAGE_FORUMS);
		boolean isCoAdministrator = currentRoles.isCoAdministrator();

		List<Integer> groups = new ArrayList<Integer>();
		for (int gid : currentRoles.getRoleValues(SecurityConstants.GROUPS)) {
			groups.add(gid);
		}

		boolean canInteractwithOtherGroups = currentRoles.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS);
		boolean isSuperAdministrator = this.userSession.getRoleManager().isAdministrator();

		for (Map.Entry<String, List<?>> entry : map.get("boolean").entrySet()) {
			String key = entry.getKey();
			Boolean value = (Boolean)entry.getValue().get(0);

			if (SecurityConstants.ADMINISTRATOR.equals(key)) {
				registerRole(group, key, isSuperAdministrator ? value : isAdministrator);
			}
			else if (SecurityConstants.CAN_MANAGE_FORUMS.equals(key)) {
				registerRole(group, key, isSuperAdministrator ? value : canManageForums);
			}
			else if (SecurityConstants.CO_ADMINISTRATOR.equals(key)) {
				registerRole(group, key, isSuperAdministrator ? value : isCoAdministrator);
			}
			else if (SecurityConstants.INTERACT_OTHER_GROUPS.equals(key)) {
				registerRole(group, key, isSuperAdministrator ? value : canInteractwithOtherGroups);
			}
			else {
				registerRole(group, key, (Boolean)entry.getValue().get(0));
			}
		}

		for (Map.Entry<String, List<?>> entry : map.get("multiple").entrySet()) {
			String key = entry.getKey();
			List<Integer> value = (List<Integer>) entry.getValue();

			if (SecurityConstants.GROUPS.equals(key)) {
				registerRole(group, key, isSuperAdministrator ? value : groups);
			}
			else {
				registerRole(group, key, value);
			}
		}

		this.repository.update(group);

		this.sessionManager.computeAllOnlineModerators();
		//this.userRepository.changeAllowAvatarState(map.getCanHaveProfilePicture(), group);
	}

	/**
	 * Add a new group
	 * @param group
	 */
	public void add(Group group) {
		this.applyCommonConstraints(group);

		if (group.getId() > 0) {
			throw new ValidationException("Cannot save an existing (id > 0) group");
		}

		this.repository.add(group);
	}

	/**
	 * Updates the information of an existing group
	 * @param group
	 */
	public void update(Group group) {
		this.applyCommonConstraints(group);

		if (group.getId() == 0) {
			throw new ValidationException("update() expects a group with an existing id");
		}

		this.repository.update(group);
	}

	/**
	 * Deletes one or more groups
	 * @param ids
	 */
	public void delete(int... ids) {
		if (ids != null) {
			// FIXME: Must not delete a group if it has users
			for (int groupId : ids) {
				Group group = this.repository.get(groupId);
				this.repository.remove(group);
			}
		}
	}

	public void appendRole(Group group, String roleName, int roleValue) {
		for (Role role : group.getRoles()) {
			if (role.getName().equals(roleName)) {
				role.getRoleValues().add(roleValue);
				break;
			}
		}

		this.repository.update(group);
	}

	private void applyCommonConstraints(Group group) {
		if (group == null) {
			throw new NullPointerException("Cannot save a null group");
		}

		if (StringUtils.isEmpty(group.getName())) {
			throw new ValidationException("A group should have a name");
		}
	}

	private void registerRole(Group group, String name, List<Integer> values) {
		if (values.size() > 0) {
			group.addRole(this.createRole(name, values));
		}
	}

	private void registerRole(Group group, String name, boolean isAllowed) {
		if (isAllowed) {
			group.addRole(this.createRole(name, null));
		}
	}

	private Role createRole(String name, List<Integer> values) {
		Role role = new Role();
		role.setName(name);

		if (values != null) {
			for (int value : values) {
				role.addRoleValue(value);
			}
		}

		return role;
	}
}

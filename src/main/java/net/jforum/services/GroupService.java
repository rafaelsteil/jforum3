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

import net.jforum.actions.helpers.PermissionOptions;
import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Group;
import net.jforum.entities.Role;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.SecurityConstants;

import org.apache.commons.lang.StringUtils;

/**
 * @author Rafael Steil
 */
public class GroupService {
	private GroupRepository repository;
	private SessionManager sessionManager;
	private UserRepository userRepository;

	public GroupService(GroupRepository repository, SessionManager sessionManager, UserRepository userRepository) {
		this.repository = repository;
		this.sessionManager = sessionManager;
		this.userRepository = userRepository;
	}

	/**
	 * Required by CGLib. Use {@link #GroupService(GroupRepository)} instead
	 */
	public GroupService() {
	}

	/**
	 * Save the permissions for this group
	 * @param group
	 * @param permissions
	 */
	public void savePermissions(int groupId, PermissionOptions permissions) {
		Group group = repository.get(groupId);
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
		boolean isSuperAdministrator = sessionManager.getUserSession().getRoleManager().isAdministrator();

		this.registerRole(group, SecurityConstants.ADMINISTRATOR, isSuperAdministrator ? permissions.isAdministrator() : isAdministrator);
		this.registerRole(group, SecurityConstants.CAN_MANAGE_FORUMS, isSuperAdministrator ? permissions.getCanManageForums() : canManageForums);
		this.registerRole(group, SecurityConstants.CO_ADMINISTRATOR, isSuperAdministrator ? permissions.isCoAdministrator() : isCoAdministrator);
		this.registerRole(group, SecurityConstants.GROUPS, isSuperAdministrator ? permissions.getAllowedGroups() : groups);
		this.registerRole(group, SecurityConstants.INTERACT_OTHER_GROUPS, isSuperAdministrator ? permissions.getCanInteractOtherGroups() : canInteractwithOtherGroups);

		this.registerRole(group, SecurityConstants.ATTACHMENTS_DOWNLOAD, permissions.getDownloadAttachments());
		this.registerRole(group, SecurityConstants.ATTACHMENTS_ENABLED, permissions.getAttachments());
		this.registerRole(group, SecurityConstants.CATEGORY, permissions.getAllowedCategories());
		this.registerRole(group, SecurityConstants.POLL_CREATE, permissions.getCanCreatePoll());
		this.registerRole(group, SecurityConstants.CREATE_STICKY_ANNOUNCEMENT_TOPICS, permissions.getCanCreateStickyAnnouncement());
		this.registerRole(group, SecurityConstants.FORUM, permissions.getAllowedForums());
		this.registerRole(group, SecurityConstants.HTML_ALLOWED, permissions.getHtml());
		this.registerRole(group, SecurityConstants.MODERATOR, permissions.isModerator());
		this.registerRole(group, SecurityConstants.APPROVE_MESSAGES, permissions.getCanApproveMessages());
		this.registerRole(group, SecurityConstants.MODERATE_FORUM, permissions.getModerateForums());
		this.registerRole(group, SecurityConstants.POST_EDIT, permissions.getCanEditPosts());
		this.registerRole(group, SecurityConstants.POST_DELETE, permissions.getCanRemovePosts());
		this.registerRole(group, SecurityConstants.TOPIC_LOCK_UNLOCK, permissions.getCanLockUnlock());
		this.registerRole(group, SecurityConstants.TOPIC_MOVE, permissions.getCanMoveTopics());
		this.registerRole(group, SecurityConstants.MODERATE_REPLIES, permissions.getModeratedReplies());
		this.registerRole(group, SecurityConstants.FORUM_REPLY_ONLY, permissions.getReplyOnly());
		this.registerRole(group, SecurityConstants.FORUM_READ_ONLY, permissions.getReadOnlyForums());
		this.registerRole(group, SecurityConstants.POLL_VOTE, permissions.getAllowPollVote());
		this.registerRole(group, SecurityConstants.PRIVATE_MESSAGE, permissions.isPrivateMessageAllowed());
		this.registerRole(group, SecurityConstants.USER_LISTING, permissions.isUserListingAllowed());
		this.registerRole(group, SecurityConstants.VIEW_PROFILE, permissions.getCanViewProfile());
		this.registerRole(group, SecurityConstants.PROFILE_PICTURE, permissions.getCanHaveProfilePicture());
		this.registerRole(group, SecurityConstants.POST_ONLY_WITH_MODERATOR_ONLINE, permissions.getPostOnlyWithModeratorOnline());
		this.registerRole(group, SecurityConstants.PM_ONLY_TO_MODERATORS, permissions.isPmOnlyToModerators());

		repository.update(group);

		sessionManager.computeAllOnlineModerators();
		userRepository.changeAllowAvatarState(permissions.getCanHaveProfilePicture(), group);
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

		repository.add(group);
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

		repository.update(group);
	}

	/**
	 * Deletes one or more groups
	 * @param ids
	 */
	public void delete(int... ids) {
		if (ids != null) {
			// TODO: Must not delete a group if it has users
			for (int groupId : ids) {
				Group group = repository.get(groupId);
				repository.remove(group);
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

		repository.update(group);
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

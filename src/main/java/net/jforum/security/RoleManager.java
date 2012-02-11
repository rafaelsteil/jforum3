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
package net.jforum.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jforum.entities.Forum;
import net.jforum.entities.Group;
import net.jforum.entities.Role;
import net.jforum.entities.User;
import net.jforum.util.SecurityConstants;

/**
 * Provide access to all roles from a set of groups.
 * The awkward syntax of many methods, following the javabeans
 * style and a "can" prefix, as in "getCanDoSomething" is to
 * make it play nice with JSP's EL.
 *
 * @author Rafael Steil
 */
public class RoleManager {
	private Map<String, Role> roles = new HashMap<String, Role>();

	/**
	 * Set the groups for this role manager
	 * This will replace any existing group that may exist already
	 * @param groups the groups to add
	 */
	public void setGroups(List<Group> groups) {
		this.roles = new HashMap<String, Role>();

		if (groups != null) {
			for (Group group : groups) {
				List<Role> currentGroupRoles = group.getRoles();

				for (Role role : currentGroupRoles) {
					Role existingRole = this.roles.get(role.getName());

					if (existingRole == null) {
						this.roles.put(role.getName(), new Role(role));
					}
					else {
						// Handle the very special case of read only and reply only
						if (role.getName().equals(SecurityConstants.FORUM_READ_ONLY)
								|| role.getName().equals(SecurityConstants.FORUM_REPLY_ONLY)) {
							existingRole.getRoleValues().retainAll(role.getRoleValues());
						}
						else {
							existingRole.getRoleValues().addAll(role.getRoleValues());
						}
					}
				}
			}

			this.readReplyOnlySecondPass(SecurityConstants.FORUM_READ_ONLY, groups);
			this.readReplyOnlySecondPass(SecurityConstants.FORUM_REPLY_ONLY, groups);
		}
	}

	private void readReplyOnlySecondPass(String roleName, List<Group> groups) {
		Role role = this.roles.get(roleName);

		if (role != null) {
			for (int forumId : new ArrayList<Integer>(role.getRoleValues())) {
				for (Group g : groups) {
					if (g.roleExists(SecurityConstants.FORUM, forumId) && !g.roleExists(roleName, forumId)) {
						role.getRoleValues().remove((Object)forumId);
					}
				}
			}
		}
	}

	public boolean getPostOnlyWithModeratorOnline() {
		return this.roleExists(SecurityConstants.POST_ONLY_WITH_MODERATOR_ONLINE);
	}

	/**
	 * Check if replies should be moderated in some forum
	 * @param forumId the forum id
	 * @return true if replies should be moderated
	 */
	public boolean isReplyModerationNeeded(int forumId) {
		return this.roleExists(SecurityConstants.MODERATE_REPLIES, forumId);
	}

	/**
	 * Check if it can download existing attachments from some forum
	 * @param forumId the forum id
	 * @return true if it can download attachments
	 */
	public boolean getCanDownloadAttachments(int forumId) {
		return this.roleExists(SecurityConstants.ATTACHMENTS_DOWNLOAD, forumId);
	}

	/**
	 * Check if it can add attachments to the messages of some forum
	 * @param forumId the forum id
	 * @return true if it can add attachments
	 */
	public boolean isAttachmentsAlllowed(int forumId) {
		return this.roleExists(SecurityConstants.ATTACHMENTS_ENABLED, forumId);
	}

	/**
	 * Check if some forum is reply only (no new messages allowed)
	 * @param forumId the forum id
	 * @return true if the forum id reply only
	 */
	public boolean isForumReplyOnly(int forumId) {
		return this.roleExists(SecurityConstants.FORUM_REPLY_ONLY, forumId);
	}

	/**
	 * Check if HTML is allowed in the messages of some forum
	 * @param forumId the forum id
	 * @return true if HTML is allowed
	 */
	public boolean isHtmlAllowed(int forumId) {
		return this.roleExists(SecurityConstants.HTML_ALLOWED, forumId);
	}

	/**
	 * Check it is a read only forum
	 * @param forumId the forum id
	 * @return true if it is a read only forum
	 */
	public boolean isForumReadOnly(int forumId) {
		return this.roleExists(SecurityConstants.FORUM_READ_ONLY, forumId);
	}

	/**
	 * Check if it can vote on existing polls
	 * @return true if it can vote on existing polls
	 */
	public boolean getCanVoteOnPolls() {
		return this.roleExists(SecurityConstants.POLL_VOTE);
	}

	/**
	 * Check if it can create polls
	 * @return true if it can create polls
	 */
	public boolean getCanCreatePolls() {
		return this.roleExists(SecurityConstants.POLL_CREATE);
	}

	/**
	 * Check if can create sticky and announcement topics
	 * @return true if can create sticky and announcement topics
	 */
	public boolean getCanCreateStickyAnnouncementTopics() {
		return this.roleExists(SecurityConstants.CREATE_STICKY_ANNOUNCEMENT_TOPICS);
	}

	/**
	 * Check if it can lock and unlock topics
	 * @return true if it can lock and unlock topics
	 */
	public boolean getCanLockUnlockTopics() {
		return this.roleExists(SecurityConstants.TOPIC_LOCK_UNLOCK);
	}

	/**
	 * Check if can move topics between forums
	 * @return true if it can move topics
	 */
	public boolean getCanMoveTopics() {
		return this.roleExists(SecurityConstants.TOPIC_MOVE);
	}

	/**
	 * Check if it can edit any message
	 * @return true if it can edit any message
	 */
	public boolean getCanEditPosts() {
		return this.roleExists(SecurityConstants.POST_EDIT);
	}

	/**
	 * Check if can remove any message
	 * @return true if it can remove any message
	 */
	public boolean getCanDeletePosts() {
		return this.roleExists(SecurityConstants.POST_DELETE);
	}

	/**
	 * Check if it can moderate some forum
	 * @param forumId the forum id
	 * @return true if it can moderate the forum
	 */
	public boolean getCanModerateForum(int forumId) {
		return isAdministrator() || this.roleExists(SecurityConstants.MODERATE_FORUM, forumId);
	}

	/**
	 * Check if it can approve messages in moderated forums
	 * @return
	 */
	public boolean getCanApproveMessages() {
		return this.roleExists(SecurityConstants.APPROVE_MESSAGES);
	}

	/**
	 * Check if it is a moderator
	 * @return true if it's a moderator
	 */
	public boolean isModerator() {
		return this.roleExists(SecurityConstants.MODERATOR);
	}

	/**
	 * Check if it has access to the forum
	 * @param forumId the forum id
	 * @return true if access is allowed
	 */
	public boolean isForumAllowed(int forumId) {
		return this.roleExists(SecurityConstants.FORUM, forumId);
	}

	/**
	 * Check if it has access to the category
	 * @param categoryId the category id
	 * @return true if access is allowed
	 */
	public boolean isCategoryAllowed(int categoryId) {
		return this.roleExists(SecurityConstants.CATEGORY, categoryId);
	}

	public boolean isCategoryModerated(List<Forum> forumsOfACategory) {
		for (Forum forum : forumsOfACategory) {
			if(this.roleExists(SecurityConstants.MODERATE_FORUM, forum.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if is an administrator
	 */
	public boolean isAdministrator() {
		return this.roleExists(SecurityConstants.ADMINISTRATOR);
	}

	public boolean isCoAdministrator() {
		return this.roleExists(SecurityConstants.CO_ADMINISTRATOR);
	}

	public boolean isGroupManager(int groupId) {
		return this.roleExists(SecurityConstants.GROUPS, groupId);
	}

	public boolean isPrivateMessageEnabled() {
		return this.roleExists(SecurityConstants.PRIVATE_MESSAGE);
	}

	public boolean isUserListingEnabled() {
		return this.roleExists(SecurityConstants.USER_LISTING);
	}

	public boolean getCanViewProfile() {
		return this.roleExists(SecurityConstants.VIEW_PROFILE);
	}

	public boolean getCanHaveProfilePicture() {
		return this.roleExists(SecurityConstants.PROFILE_PICTURE);
	}

	public boolean getCanOnlyContactModerators() {
		return this.roleExists(SecurityConstants.PM_ONLY_TO_MODERATORS);
	}

	/**
	 * Return all values (if any) associated to a specific role
	 * @param name the role name
	 * @return the role values (if any)
	 */
	public int[] getRoleValues(String name) {
		Role role = this.get(name);

		if (role == null || role.getRoleValues().size() == 0) {
			return new int[0];
		}

		// This is lame, but due to the dificulties of
		// working with int... versus Integer...
		// versus int[] and Integer[] in some other classes,
		// and that .toArray() does not work with int itself,
		// we do the copy by hand there.
		int[] data = new int[role.getRoleValues().size()];
		int counter = 0;
		for (int value : role.getRoleValues()) {
			data[counter++] = value;
		}

		return data;
	}

	public List<Integer> getRoleValuesAsList(String name) {
		Role role = this.get(name);
		return role != null ? role.getRoleValues() : new ArrayList<Integer>();
	}

	public boolean roleExists(String name) {
		return this.get(name) != null;
	}

	public boolean roleExists(String name, int value) {
		Role role = this.get(name);
		return role != null && role.getRoleValues().contains(value);
	}

	private Role get(String name) {
		return this.roles.get(name);
	}

	public boolean getCanEditUser(User userToEdit, List<Group> groups) {
		if (isAdministrator()) {
			return true;
		}

		for (Group group : groups) {
			for (Group group2 : userToEdit.getGroups()) {
				if (group.equals(group2)) {
					return true;
				}
			}
		}

		return false;
	}
}

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

import java.util.Date;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Avatar;
import net.jforum.entities.AvatarType;
import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.sso.LoginAuthenticator;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.MD5;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class UserService {
	private UserRepository userRepository;
	private GroupRepository groupRepository;
	private JForumConfig config;
	private LoginAuthenticator loginAuthenticator;
	private AvatarService avatarService;

	public UserService(UserRepository userReposistory, GroupRepository groupRepository,
		JForumConfig config, LoginAuthenticator loginAuthenticator, AvatarService avatarService) {
		this.userRepository = userReposistory;
		this.groupRepository = groupRepository;
		this.config = config;
		this.loginAuthenticator = loginAuthenticator;
		this.avatarService = avatarService;
	}

	/**
	 * Add a new user
	 * @param user the user to add. The password should be in
	 * plain text, as it hashed internaly before persisting.
	 */
	public void add(User user) {
		this.performAddValidations(user);

		if (user.getId() > 0) {
			throw new ValidationException("Cannot add an existing (id > 0) user");
		}

		if (user.getRegistrationDate() == null) {
			user.setRegistrationDate(new Date());
		}

		user.setPassword(MD5.hash(user.getPassword()));

		if (user.getGroups().size() == 0) {
			Group defaultGroup = this.groupRepository.get(this.config.getInt(ConfigKeys.DEFAULT_USER_GROUP));
			user.addGroup(defaultGroup);
		}

		this.userRepository.add(user);
	}

	/**
	 * Updates an existing user
	 * @param user
	 */
	public void update(User user, boolean changeUsername) {
		if (user == null) {
			throw new NullPointerException("Cannot save a null user");
		}

		if (user.getId() == 0) {
			throw new ValidationException("Cannot update an user without an id");
		}

		User currentUser = this.userRepository.get(user.getId());
		this.copyUpdatableProperties(user, currentUser);

		Avatar userAvatar = user.getAvatar();
		Avatar currentAvatar = currentUser.getAvatar();

		if (userAvatar == null || !userAvatar.equals(currentAvatar)) {
			if (currentAvatar != null && currentAvatar.getAvatarType() == AvatarType.AVATAR_UPLOAD) {
				this.avatarService.delete(currentAvatar);
			}

			currentUser.setAvatar(userAvatar);
		}

		if (changeUsername && StringUtils.isNotEmpty(user.getUsername())) {
			currentUser.setUsername(user.getUsername());
		}

		this.userRepository.update(currentUser);
	}

	/**
	 * Create a security hash to be used as extra security for auto logins.
	 * @param userId the id of the user to generate the hash
	 * @return the hash
	 */
	public String generateAutoLoginSecurityHash(int userId) {
		String systemHash = MD5.hash(this.config.getValue(ConfigKeys.USER_HASH_SEQUENCE) + userId);
		return MD5.hash(System.currentTimeMillis() + systemHash);
	}

	/**
	 * Generate a hash based on the security hash of an user
	 * @param securityHash the user's current security hash
	 * @return the hash
	 */
	public String generateAutoLoginUserHash(String securityHash) {
		return MD5.hash(securityHash);
	}

	private void copyUpdatableProperties(User from, User to) {
		to.setRanking(from.getRanking());
		to.setAim(from.getAim());
		to.setAttachSignature(from.getAttachSignature());
		to.setBbCodeEnabled(from.isBbCodeEnabled());
		to.setBiography(from.getBiography());
		to.setFrom(from.getFrom());
		to.setHtmlEnabled(from.isHtmlEnabled());
		to.setInterests(from.getinterests());
		to.setLang(from.getLang());
		to.setMsn(from.getMsn());
		to.setNotifyAlways(from.getNotifyAlways());
		to.setNotifyReply(from.getNotifyReply());
		to.setOccupation(from.getOccupation());
		to.setViewEmailEnabled(from.isViewEmailEnabled());
		to.setViewOnlineEnabled(from.isViewOnlineEnabled());
		to.setSignature(from.getSignature());
		to.setWebsite(from.getWebsite());
		to.setYim(from.getYim());
		to.setNotifyReply(from.getNotifyReply());
		to.setNotifyPrivateMessages(from.getNotifyPrivateMessages());
		to.setSmiliesEnabled(from.isSmiliesEnabled());
		to.setNotifyText(from.getNotifyText());
	}

	/**
	 * Authenticates an user
	 * @param username the username
	 * @param password the password, in plain text
	 * @return an instance of an {@link User}, of null if authentication failed
	 */
	public User validateLogin(String username, String password) {
		return this.loginAuthenticator.validateLogin(username, MD5.hash(password), null);
	}

	/**
	 * Save the gropus for the user
	 * @param userId
	 * @param groupIds
	 */
	public void saveGroups(int userId, int... groupIds) {
		if (groupIds != null && groupIds.length > 0) {
			User user = this.userRepository.get(userId);
			user.getGroups().clear();

			for (int groupId : groupIds) {
				Group group = this.groupRepository.get(groupId);
				user.addGroup(group);
			}

			this.userRepository.update(user);
		}
	}

	private void performAddValidations(User user) {
		if (user == null) {
			throw new NullPointerException("User cannot be null");
		}

		if (StringUtils.isEmpty(user.getUsername())) {
			throw new ValidationException("Username cannot be null");
		}

		if (StringUtils.isEmpty(user.getPassword())) {
			throw new ValidationException("Password cannot be null");
		}

		if (StringUtils.isEmpty(user.getEmail())) {
			throw new ValidationException("Email cannot be null");
		}
	}
}

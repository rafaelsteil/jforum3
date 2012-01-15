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

import net.jforum.entities.User;
import net.jforum.repository.UserRepository;
import net.jforum.util.MD5;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class LostPasswordService {
	private UserRepository userRepository;

	public LostPasswordService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Dispatches the email asking for a new password
	 * @param requestedUser the username who lost the password. This parameter
	 * is optional if requestedEmail is provided.
	 * @param requestedEmail the email who lost the password. This parameter
	 * is optional if requestedUser is provided
	 * @return true if the email was sent, or false if no user matching the
	 * parameters was found.
	 */
	public boolean send(String requestedUser, String requestedEmail) {
		User user = this.findUser(requestedUser, requestedEmail);

		if (user == null) {
			return false;
		}

		String hash = MD5.hash(user.getEmail() + System.currentTimeMillis());
		user.setActivationKey(hash);
		this.userRepository.update(user);

		return true;
	}

	private User findUser(String username, String email)
	{
		User user = null;

		if (!StringUtils.isEmpty(username)) {
			user = this.userRepository.getByUsername(username);
		}
		else if (!StringUtils.isEmpty(email)) {
			user = this.userRepository.getByEmail(email);
		}

		if (user == null) {
			return null;
		}

		return user;
	}
}

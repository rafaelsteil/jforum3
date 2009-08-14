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
package net.jforum.sso;

import net.jforum.entities.User;
import net.jforum.repository.UserRepository;

/**
 * General utilities to use with SSO.
 *
 * @author Rafael Steil
 */
public class SSOUtils {
	private String username;
	private boolean exists = true;
	private User user;
	private UserRepository userRepository;

	public SSOUtils(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Checks if an user exists in the database
	 *
	 * @param username The username to check
	 * @return <code>true</code> if the user exists. If <code>false</code> is
	 * returned, then you can insert the user by calling {@link #register(String, String)}
	 * @see #register(String, String)
	 * @see #getUser()
	 */
	public boolean userExists(String username) {
		this.username = username;

		this.user = this.userRepository.getByUsername(username);
		this.exists = this.user != null;

		return this.exists;
	}

	/**
	 * Registers a new user. This method should be used together with {@link #userExists(String)}.
	 *
	 * @param password the user's password. It <em>should</em> be the
	 * real / final password. In other words, the data passed as
	 * password is the data that'll be written to the database
	 * @param email the user's email
	 * @see #getUser()
	 */
	public void register(String password, String email) {
		if (this.exists) {
			return;
		}

		// Is a new user for us. Register him
		this.user = new User();

		user.setUsername(this.username);
		user.setPassword(password);
		user.setEmail(email);
		user.setActive(true);

		this.userRepository.add(user);
	}

	/**
	 * Gets the user associated to this class instance.
	 *
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}
}

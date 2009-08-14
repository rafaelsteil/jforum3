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

import javax.servlet.http.HttpServletRequest;

import net.jforum.core.SecurityConstraint;
import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.UserRepository;

/**
 * Check if the user can edit his profile
 * This is intended to be used with {@link SecurityConstraint}, and will check
 * if the current user can edit a specific profile
 * @author Rafael Steil
 */
public class EditUserRule implements AccessRule {
	private final UserRepository repository;

	public EditUserRule(UserRepository repository) {
		this.repository = repository;
	}

	/**
	 * Applies the following rules:
	 * <ul>
	 * 	<li> User must be logged
	 * 	<li> His user id must be the same of the profile he wants to edit, or be an administraor
	 * </ul>
	 * It is expected that the parameter <i>userId</i> or <i>user.id</i> exists in the request
	 */
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		int userId = this.findUserId(request);

		boolean logged = userSession.isLogged();
		if(logged && userSession.getUser().getId() == userId) {
			return true;
		}

		User user = repository.get(userId);
		return logged && userSession.getRoleManager().getCanEditUser(user, userSession.getUser().getGroups());
	}

	private int findUserId(HttpServletRequest request) {
		int userId = 0;

		if (request.getParameterMap().containsKey("userId")) {
			userId = Integer.parseInt(request.getParameter("userId"));
		}
		else if (request.getParameterMap().containsKey("user.id")) {
			userId = Integer.parseInt(request.getParameter("user.id"));
		}
		else {
			throw new AccessRuleException("Could not find userId or user.id in the current request");
		}

		return userId;
	}
}

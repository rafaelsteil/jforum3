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

import net.jforum.entities.UserSession;

/**
 * Check if determined method can be executed.
 * This is used together with {@link SecurityConstraint} in some actions.
 * <strong>Implementations should be stateless</strong>
 * @author Rafael Steil
 */
public interface AccessRule {
	/**
	 * Check if the method can be executed
	 * @param userSession the {@link UserSession} of the current user
	 * @param request the current request
	 * @return true if method execution is allowed
	 */
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request);
}

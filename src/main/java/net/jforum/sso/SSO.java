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

import javax.servlet.http.HttpServletRequest;

import net.jforum.entities.UserSession;
import net.jforum.util.JForumConfig;

/**
 * @author Rafael Steil
 */
public interface SSO {
	/**
	 * Set the config object
	 *
	 * @param config
	 */
	public void setConfig(JForumConfig config);

	/**
	 * Authenticates an user. This method should check if the incoming user is authorized to access the forum.
	 *
	 * @param request The request object
	 * @return The username, if authentication succeded, or <code>nulll</code> otherwise.
	 */
	public String authenticateUser(HttpServletRequest request);

	/**
	 * Check to see if the user for the current {@link UserSession} is the same user by single sign on mechanisim.
	 *
	 * @param userSession the current user session
	 * @return if the UserSession is valid
	 */
	public boolean isSessionValid(UserSession userSession);
}

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
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

/**
 * Simple SSO authenticator. This class will try to validate an user by simple checking <code>request.getRemoteUser()</code> is not null.
 *
 * @author Rafael Steil
 */
public class RemoteUserSSO implements SSO {
	private JForumConfig config;

	/**
	 * @see net.jforum.sso.SSO#authenticateUser(net.jforum.context.RequestContext)
	 * @param request AWebContextRequest * @return String
	 */
	@Override
	public String authenticateUser(HttpServletRequest request) {
		return request.getRemoteUser();
	}

	@Override
	public boolean isSessionValid(UserSession userSession) {
		String remoteUser = userSession.getRequest().getRemoteUser();

		// user has since logged out
		if (remoteUser == null && userSession.getUser().getId() != this.config.getInt(ConfigKeys.ANONYMOUS_USER_ID)) {
			return false;
		}
		// user has since logged in
		else if (remoteUser != null && userSession.getUser().getId() == this.config.getInt(ConfigKeys.ANONYMOUS_USER_ID)) {
			return false;
		}
		// user has changed user
		else if (remoteUser != null && !remoteUser.equals(userSession.getUser().getUsername())) {
			return false;
		}

		return true;
	}

	/**
	 * @see net.jforum.sso.SSO#setConfig(net.jforum.util.JForumConfig)
	 */
	@Override
	public void setConfig(JForumConfig config) {
		this.config = config;
	}
}

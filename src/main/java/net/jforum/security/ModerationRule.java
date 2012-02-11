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
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class ModerationRule implements AccessRule {
	/**
	 * @see net.jforum.security.AccessRule#shouldProceed(net.jforum.entities.UserSession, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		return userSession.getRoleManager().isModerator();
	}
}

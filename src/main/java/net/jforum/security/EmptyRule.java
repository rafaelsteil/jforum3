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
 * @author Rafael Steil
 */
public class EmptyRule implements AccessRule {

	/**
	 * @see net.jforum.security.AccessRule#shouldProceed(net.jforum.entities.UserSession, javax.servlet.http.HttpServletRequest)
	 */
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		return false;
	}

}

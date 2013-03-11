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
package net.jforum.core.support.spring;

import net.jforum.core.SessionManager;
import net.jforum.entities.UserSession;
import net.jforum.security.RoleManager;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Rafael Steil
 */
public class RoleManagerFactoryBean implements FactoryBean<RoleManager> {
	private SessionManager sessionManager;

	public RoleManagerFactoryBean(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public RoleManager getObject() throws Exception {
		String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		UserSession userSession = this.sessionManager.getUserSession(sessionId);
		return userSession != null ? userSession.getRoleManager() : null;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class<?> getObjectType() {
		return RoleManager.class;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return false;
	}

}

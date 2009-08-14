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
package net.jforum.api;

import javax.servlet.http.HttpServletRequest;

import net.jforum.util.ConfigKeys;

import org.springframework.context.ApplicationContext;

/**
 * Base class to be used to run any JForum code in other environments.
 * @author Rafael Steil
 */
public abstract class JForumExecutionContext {
	private ApplicationContext context;
	private HttpServletRequest request;
	private boolean initialized;

	public JForumExecutionContext(HttpServletRequest request) {
		this.request = request;
		this.context = (ApplicationContext)request.getSession().getServletContext()
			.getAttribute(ConfigKeys.SPRING_CONTEXT);

		this.initialized = this.context != null;
	}

	protected HttpServletRequest getReques() {
		return this.request;
	}

	/**
	 * Check if JForum is initialized and ready to use.
	 * @return true if JForum is ready for use
	 */
	protected boolean isInitialized() {
		return this.initialized;
	}

	public abstract void execute();

	@SuppressWarnings("unchecked")
	protected final <T> T getComponent(Class<T> k) {
		return (T)this.context.getBean(k.getName());
	}
}

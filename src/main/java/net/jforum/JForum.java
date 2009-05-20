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
package net.jforum;

import net.jforum.core.SessionManager;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Rafael Steil
 */
public class JForum implements ApplicationContextAware {
	private static ApplicationContext beanFactory;

	public Object getComponent(String componentName) {
		return beanFactory.getBean(componentName);
	}

	public SessionManager getSessionManager() {
		return (SessionManager)this.getComponent(SessionManager.class.getName());
	}

	/**
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		beanFactory = applicationContext;
	}
}

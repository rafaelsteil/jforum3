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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Rafael Steil
 */
public class HttpServletRequestFactoryBean implements FactoryBean<HttpServletRequest> {
	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public HttpServletRequest getObject() throws Exception {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		return ((ServletRequestAttributes)attributes).getRequest();
	}
 
	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class<?> getObjectType() {
		return HttpServletRequest.class;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return false;
	}
}

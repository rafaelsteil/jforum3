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

import javax.servlet.http.HttpServletResponse;

import net.jforum.util.ConfigKeys;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Rafael Steil
 */
public class HttpServletResponseFactoryBean implements FactoryBean<HttpServletResponse> {
	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public HttpServletResponse getObject() throws Exception {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		return (HttpServletResponse) attributes.getAttribute(ConfigKeys.HTTP_SERVLET_RESPONSE, RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class<?> getObjectType() {
		return HttpServletResponse.class;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return false;
	}
}

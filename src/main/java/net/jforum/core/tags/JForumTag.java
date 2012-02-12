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
package net.jforum.core.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.springframework.context.ApplicationContext;

/**
 * @author Rafael Steil
 */
public abstract class JForumTag extends SimpleTagSupport {
	private static ApplicationContext springContext;

	protected HttpServletRequest request() {
		return (HttpServletRequest)this.pageContext().getRequest();
	}

	protected void setAttribute(String key, Object value) {
		this.request().setAttribute(key, value);
	}

	protected HttpServletResponse response() {
		return (HttpServletResponse)this.pageContext().getResponse();
	}

	protected JForumConfig config() {
		return this.getBean(JForumConfig.class);
	}

	protected void write(String content) throws IOException {
		this.pageContext().getOut().write(content);
	}

	protected void invokeJspBody() throws JspException, IOException {
		this.getJspBody().invoke(this.pageContext().getOut());
	}

	protected <T> T getBean(Class<T> beanId) {
		if (springContext == null) {
			springContext = (ApplicationContext)this.pageContext().getServletContext().getAttribute(ConfigKeys.SPRING_CONTEXT);
		}

		return springContext != null
			? springContext.getBean(beanId)
			: null;
	}

	protected PageContext pageContext() {
		return (PageContext)this.getJspContext();
	}
}

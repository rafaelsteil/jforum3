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
package net.jforum.core.support.vraptor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Rafael Steil
 */
public class ViewPropertyBag {
	private HttpServletRequest request;

	public ViewPropertyBag(HttpServletRequest request) {
		this.request = request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void put(String key, Object value) {
		request.setAttribute(key, value);
	}

	public Object get(String key) {
		return request.getAttribute(key);
	}

	public void remove(String key) {
		request.removeAttribute(key);
	}
}

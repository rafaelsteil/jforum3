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
package net.jforum.actions.interceptors;

import javax.servlet.http.HttpServletRequest;

import net.jforum.core.SecurityConstraint;
import net.jforum.entities.UserSession;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Intercepts and process the {@link SecurityConstraint} annotation for methods
 * @author Rafael Steil
 */
@Intercepts(after = SessionManagerInterceptor.class)
@RequestScoped
public class MethodSecurityInterceptor extends SecurityInterceptor {
	public MethodSecurityInterceptor(HttpServletRequest request, Result result, UserSession userSession, Container container) {
		super(request, result, userSession, container);
	}

	@Override
	protected SecurityConstraint getAnnotation(ResourceMethod method) {
		return method.getMethod().getAnnotation(SecurityConstraint.class);
	}

	@Override
	protected boolean isAnnotationPresent(ResourceMethod method) {
		return method.getMethod().isAnnotationPresent(SecurityConstraint.class);
	}
}

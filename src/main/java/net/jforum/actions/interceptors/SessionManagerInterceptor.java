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
import javax.servlet.http.HttpServletResponse;

import net.jforum.core.SessionManager;
import net.jforum.entities.UserSession;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Intercepts
public class SessionManagerInterceptor implements Interceptor {
	private final UserSession userSession;
	private final SessionManager sessionManager;
	private final HttpServletRequest request;

	public SessionManagerInterceptor(UserSession userSession, SessionManager sessionManager,
			HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		userSession.setRequest(request);
		userSession.setResponse(response);
		this.userSession = userSession;
		this.sessionManager = sessionManager;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		sessionManager.refreshSession(userSession);
		request.setAttribute("userSession", userSession);
		request.setAttribute("roleManager", userSession.getRoleManager());
		stack.next(method, resourceInstance);
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return true;
	}
}

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

	public SessionManagerInterceptor(UserSession userSession, SessionManager sessionManager) {
		this.userSession = userSession;
		this.sessionManager = sessionManager;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		sessionManager.refreshSession(userSession);
		stack.next(method, resourceInstance);
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return true;
	}
}

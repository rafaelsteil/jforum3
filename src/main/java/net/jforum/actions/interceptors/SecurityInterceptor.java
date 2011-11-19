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

import net.jforum.controllers.MessageController;
import net.jforum.controllers.UserController;
import net.jforum.core.Role;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.UserSession;
import net.jforum.security.AccessRule;
import net.jforum.security.EmptyRule;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * @author Rafael Steil
 */
public abstract class SecurityInterceptor implements Interceptor {
	private final HttpServletRequest request;
	private final Result result;
	private final UserSession userSession;

	public SecurityInterceptor(HttpServletRequest request, Result result, UserSession userSession) {
		this.request = request;
		this.result = result;
		this.userSession = userSession;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		SecurityConstraint annotation = this.getAnnotation(method);
		Class<? extends AccessRule> accessRuleClass = annotation.value();

		boolean shouldProceed = true;
		boolean displayLogin = true;

		if (!accessRuleClass.equals(EmptyRule.class)) {
			AccessRule accessRule = this.findAccessRule(annotation.value().getName(), application, flow);
			shouldProceed = accessRule.shouldProceed(userSession, request);
			displayLogin = annotation.displayLogin();
		}
		else {
			Role[] multiRoles = annotation.multiRoles();

			if (multiRoles.length == 0) {
				throw new IllegalStateException("@SecurityConstraint does not have an access rule nor multi roles. Cannot continue");
			}
			else {
				for (Role role : multiRoles) {
					AccessRule accessRule = this.findAccessRule(role.value().getName(), application, flow);

					if (!accessRule.shouldProceed(userSession, request)) {
						shouldProceed = false;
						displayLogin = role.displayLogin();
						break;
					}
				}
			}
		}

		if (shouldProceed) {
			stack.next(method, resourceInstance);
		}
		else {
			if (displayLogin) {
				this.result.redirectTo(UserController.class).login(null);
			}
			else {
				this.result.redirectTo(MessageController.class).accessDenied();
			}
		}
	}

	private AccessRule findAccessRule(String name, WebApplication application, LogicFlow flow) {
		AccessRule accessRule = (AccessRule)application.getIntrospector().getBeanProvider()
			.findAttribute(flow.getLogicRequest(), name);

		if (accessRule == null) {
			throw new NullPointerException(
				String.format("Could not find the rule %s. Have you registered it in the configuration file?", name));
		}

		return accessRule;
	}


	protected abstract SecurityConstraint getAnnotation(ResourceMethod method);

	protected abstract boolean isAnnotationPresent(ResourceMethod method);


	@Override
	public boolean accepts(ResourceMethod method) {
		return this.isAnnotationPresent(method);
	}
}

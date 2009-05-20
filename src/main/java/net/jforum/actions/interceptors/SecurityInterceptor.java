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

import net.jforum.core.Role;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.UserSession;
import net.jforum.security.AccessRule;
import net.jforum.security.EmptyRule;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;

import org.vraptor.Interceptor;
import org.vraptor.LogicException;
import org.vraptor.LogicFlow;
import org.vraptor.LogicRequest;
import org.vraptor.view.ViewException;
import org.vraptor.webapp.WebApplication;

/**
 * @author Rafael Steil
 */
public abstract class SecurityInterceptor implements Interceptor {
	/**
	 * @see org.vraptor.Interceptor#intercept(org.vraptor.LogicFlow)
	 */
	public void intercept(LogicFlow flow) throws LogicException, ViewException {
		LogicRequest logicRequest = flow.getLogicRequest();

		if (!this.isAnnotationPresent(logicRequest)) {
			flow.execute();
		}
		else {
			WebApplication application = (WebApplication)flow.getLogicRequest().getServletContext()
				.getAttribute(WebApplication.class.getName());

			SecurityConstraint annotation = this.getAnnotation(logicRequest);
			Class<? extends AccessRule> accessRuleClass = annotation.value();
			UserSession userSession = (UserSession)logicRequest.getRequest().getAttribute(ConfigKeys.USER_SESSION);

			boolean shouldProceed = true;
			boolean displayLogin = true;

			if (!accessRuleClass.equals(EmptyRule.class)) {
				AccessRule accessRule = this.findAccessRule(annotation.value().getName(), application, flow);
				shouldProceed = accessRule.shouldProceed(userSession, logicRequest.getRequest());
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

						if (!accessRule.shouldProceed(userSession, logicRequest.getRequest())) {
							shouldProceed = false;
							displayLogin = role.displayLogin();
							break;
						}
					}
				}
			}

			if (shouldProceed) {
				flow.execute();
			}
			else {
				ViewService viewService = (ViewService)application.getIntrospector().getBeanProvider()
					.findAttribute(flow.getLogicRequest(), ViewService.class.getName());
				this.executeViewService(viewService, displayLogin);
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

	private void executeViewService(ViewService viewService, boolean displayLogin) {
		if (displayLogin) {
			viewService.displayLogin();
		}
		else {
			viewService.accessDenied();
		}
	}

	protected abstract SecurityConstraint getAnnotation(LogicRequest logicRequest);

	protected abstract boolean isAnnotationPresent(LogicRequest logicRequest);
}

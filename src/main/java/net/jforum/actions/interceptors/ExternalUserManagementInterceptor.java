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

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.vraptor.Interceptor;
import org.vraptor.LogicException;
import org.vraptor.LogicFlow;
import org.vraptor.LogicRequest;
import org.vraptor.introspector.BeanProvider;
import org.vraptor.view.ViewException;
import org.vraptor.webapp.WebApplication;

/**
 * @author Bill
 *
 */
public class ExternalUserManagementInterceptor implements Interceptor {

	private JForumConfig config;

	public ExternalUserManagementInterceptor(JForumConfig config) {
		this.config = config;
	}

	/**
	 * @see org.vraptor.Interceptor#intercept(org.vraptor.LogicFlow)
	 */
	public void intercept(LogicFlow flow) throws LogicException, ViewException {
		boolean isExternalUserManagement = config.getBoolean(
				ConfigKeys.EXTERNAL_USER_MANAGEMENT, false);

		if(isExternalUserManagement){
			//forward
			LogicRequest logicRequest = flow.getLogicRequest();
			HttpServletRequest request = logicRequest.getRequest();

			WebApplication application = (WebApplication)logicRequest.
				getServletContext().getAttribute(WebApplication.class.getName());
			BeanProvider provider = application.getIntrospector().getBeanProvider();
			ViewService viewService = (ViewService)provider.
				findAttribute(logicRequest, ViewService.class.getName());

			String contextPath = viewService.getContextPath();
			String url = request.getRequestURL().toString();
			String serverName = url.substring(0,url.indexOf(contextPath)+contextPath.length());

			StringBuilder urlBuilder = new StringBuilder(serverName);
			urlBuilder.append("/")
					  .append(Domain.FORUMS)
					  .append("/")
					  .append(Actions.LIST)
					  .append(config.getString(ConfigKeys.SERVLET_EXTENSION));
			request.setAttribute("redirectURL", urlBuilder.toString());

			viewService.renderView(Domain.USER, "externalManagement");
			return; //end the excute flow
		}

		flow.execute(); //if not external user management, continue the execution flow
	}

}

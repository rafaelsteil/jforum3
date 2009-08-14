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
package net.jforum;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.DefaultViewManager;
import net.jforum.entities.UserSession;
import net.jforum.extensions.ActionExtensionManager;
import net.jforum.extensions.RequestOperationChain;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.vraptor.VRaptorServlet;
import org.vraptor.view.ViewManager;
import org.vraptor.webapp.WebApplication;

/**
 * @author Rafael Steil
 */
public class JForumServlet extends VRaptorServlet {
	private static final long serialVersionUID = 3467879855701631015L;

	private SessionManager sessionManager;
	private JForumConfig config;
	private RequestOperationChain operationChain;

	/**
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ApplicationContext beanFactory = (ApplicationContext)this.getServletContext()
			.getAttribute(ConfigKeys.SPRING_CONTEXT);

		this.config = (JForumConfig)beanFactory.getBean(JForumConfig.class.getName());
		this.config.setProperty(ConfigKeys.APPLICATION_PATH, this.getServletContext().getRealPath(""));

		this.sessionManager = (SessionManager)beanFactory.getBean(SessionManager.class.getName());
		this.operationChain = (RequestOperationChain)beanFactory.getBean(RequestOperationChain.class.getName());

		this.setupViewManager();
		this.setupExtentionManager(beanFactory);
	}

	/**
	 * @see org.vraptor.VRaptorServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute(ConfigKeys.ANONYMOUS_USER_ID, config.getInt(ConfigKeys.ANONYMOUS_USER_ID));
		request.setAttribute(ConfigKeys.HTTP_SERVLET_RESPONSE, response);

		ServletRequestAttributes attributes = new ServletRequestAttributes(request);

		try {
			RequestContextHolder.setRequestAttributes(attributes, false);

			UserSession userSession = this.sessionManager.refreshSession(request, response);

			request.setAttribute(ConfigKeys.USER_SESSION, userSession);
			request.setAttribute(ConfigKeys.ROLE_MANAGER, userSession.getRoleManager());
			request.setAttribute(UserSession.class.getName(), userSession);

			this.operationChain.callAllOperations();

			super.service(request, response);
		}
		finally {
			RequestContextHolder.resetRequestAttributes();
			attributes.requestCompleted();
		}
	}

	private void setupViewManager() {
		ViewManager viewManager = new DefaultViewManager(this.config.getValue(ConfigKeys.VRAPTOR_VIEW_PATTERN));

		WebApplication app = (WebApplication)this.getServletContext().getAttribute("webApplication");
		app.setViewManager(viewManager);

		this.getServletContext().setAttribute(ViewManager.class.getName(), viewManager);
		this.getServletContext().setAttribute("webApplication", app);
	}

	private void setupExtentionManager(ApplicationContext beanFactory){
		WebApplication app = (WebApplication)this.getServletContext().getAttribute("webApplication");

		ActionExtensionManager extensionManager = (ActionExtensionManager) beanFactory
			.getBean(ActionExtensionManager.class.getName(), new Object[] {app.getComponentManager() });

		this.getServletContext().setAttribute(ActionExtensionManager.class.getName(), extensionManager);
	}
}

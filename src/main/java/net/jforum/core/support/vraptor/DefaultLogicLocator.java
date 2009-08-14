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

import net.jforum.core.UrlPattern;
import net.jforum.core.exceptions.ForumException;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.vraptor.component.ComponentManager;
import org.vraptor.component.ComponentNotFoundException;
import org.vraptor.component.ComponentType;
import org.vraptor.component.LogicMethod;
import org.vraptor.component.LogicNotFoundException;
import org.vraptor.http.VRaptorServletRequest;
import org.vraptor.url.InvalidURLException;
import org.vraptor.url.LogicLocator;

/**
 * Given an URL, extracts the component and method that should be executed.
 * @author Rafael Steil
 */
public class DefaultLogicLocator implements LogicLocator {
	private ComponentManager manager;
	private JForumConfig config;

	public DefaultLogicLocator(ComponentManager manager) {
		this.manager = manager;
	}

	/**
	 * @see org.vraptor.url.LogicLocator#locate(javax.servlet.http.HttpServletRequest)
	 */
	public LogicMethod locate(VRaptorServletRequest request) throws InvalidURLException, LogicNotFoundException, ComponentNotFoundException {
		ApplicationContext springContext = (ApplicationContext)request.getSession()
			.getServletContext().getAttribute(ConfigKeys.SPRING_CONTEXT);
		this.config = (JForumConfig)springContext.getBean(JForumConfig.class.getName());

		String requestUri = this.extractRequestUri(request.getRequestURI(), request.getContextPath());
		String servletExtension = this.config.getValue(ConfigKeys.SERVLET_EXTENSION);

		LogicMethod method = null;

		if (requestUri.endsWith(servletExtension)) {
			method = this.parseFriendlyURL(requestUri, servletExtension, request);
		}

		if (method == null) {
			throw new ForumException(String.format("Could not find the action (%s) to execute. Is it configured in urlPattern.properties?", requestUri));
		}

		return method;
	}

	private LogicMethod parseFriendlyURL(String requestUri, String servletExtension, VRaptorServletRequest request)
		throws ComponentNotFoundException, LogicNotFoundException {
		requestUri = requestUri.substring(0, requestUri.length() - servletExtension.length());
		String[] urlModel = requestUri.split("/");
		int baseLen = 3;

		LogicMethod method = null;

		if (urlModel.length == 2 && "jforum".equals(urlModel[1])) {
			// This one is mostly due to legacy versions of JForum
			String componentName = request.getParameter("module");
			String logicName = request.getParameter("action");

			if (!StringUtils.isEmpty(componentName) && !StringUtils.isEmpty(logicName)) {
				ComponentType component = this.manager.getComponent(componentName, logicName);
				method = component.getLogic(logicName);
			}
		}
		else if (urlModel.length >= baseLen) {
			int componentIndex = 1;
			int logicIndex = 2;

			// <moduleName>.<actionName>.<numberOfParameters>
			StringBuilder sb = new StringBuilder(64)
				.append(urlModel[componentIndex])
				.append('.')
				.append(urlModel[logicIndex])
				.append('.')
				.append(urlModel.length - baseLen);

			UrlPattern url = this.config.getUrlPattern(sb.toString());

			if (url == null) {
				throw new ForumException("Could not find an url mapping for " + sb + ". Have you configured it at urlPattern.properties?");
			}

			if (url.getSize() >= urlModel.length - baseLen) {
				for (int i = 0; i < url.getSize(); i++) {
					request.setParameter(url.getVars()[i], urlModel[i + baseLen]);
				}
			}

			ComponentType component = this.manager.getComponent(urlModel[componentIndex], urlModel[logicIndex]);
			method = component.getLogic(urlModel[logicIndex]);
		}

		return method;
	}

	private String extractRequestUri(String requestUri, String contextPath) {
		// First, remove the context path from the requestUri,
		// so we can work only with the important stuff
		if (contextPath != null && contextPath.length() > 0) {
			requestUri = requestUri.substring(contextPath.length(), requestUri.length());
		}

		// Remove "jsessionid" (or similar) from the URI
		int index = requestUri.indexOf(';');

		if (index > -1) {
			int lastIndex = requestUri.indexOf('?', index);

			if (lastIndex == -1) {
				lastIndex = requestUri.indexOf('&', index);
			}

			if (lastIndex == -1) {
				requestUri = requestUri.substring(0, index);
			}
			else {
				String part1 = requestUri.substring(0, index);
				requestUri = part1 + requestUri.substring(lastIndex);
			}
		}

		return requestUri;
	}
}

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

import java.io.IOException;

import net.jforum.util.ConfigKeys;

import org.apache.commons.lang.StringUtils;
import org.vraptor.LogicRequest;
import org.vraptor.view.RegexViewManager;
import org.vraptor.view.ViewException;
import org.vraptor.view.ViewManager;

/**
 * Based on the code of {@link RegexViewManager}
 * @author Rafael Steil
 */
public class DefaultViewManager implements ViewManager {
	private final String regex = ("(.*);(.*)");

	private String replacement;

	/**
	 * Creates the overriden view manager with a default view manager
	 *
	 * @param viewManager the default view manager
	 */
	public DefaultViewManager(String regexPattern) {
		this.replacement = this.translateExpression(regexPattern);
	}

	/**
	 * @see org.vraptor.view.ViewManager#forward(org.vraptor.LogicRequest, java.lang.String)
	 */
	public void forward(LogicRequest logicRequest, String result) throws ViewException {
		if (!"true".equals(logicRequest.getRequest().getAttribute(ConfigKeys.IGNORE_VIEW_MANAGER_REDIRECT))) {
			String customLogic = (String)logicRequest.getRequest().getAttribute(ConfigKeys.RENDER_CUSTOM_LOGIC);
			String customComponent = (String)logicRequest.getRequest().getAttribute(ConfigKeys.RENDER_CUSTOM_COMPONENT);
			String forward;

			if (!StringUtils.isEmpty(customComponent) && !StringUtils.isEmpty(customLogic)) {
				forward = this.getForward(customComponent, customLogic);
			}
			else {
				forward = this.getForward(logicRequest.getRequestInfo().getComponentName(),
					!StringUtils.isEmpty(customLogic)
					? customLogic
					: logicRequest.getRequestInfo().getLogicName());
			}

			this.directForward(logicRequest, result, forward);
		}
	}

	/**
	 * Method replaces any $component with $1, $logic with $2 and $result with $3 within the string.
	 *
	 * @param replacement the string to replace
	 * @return the translated string
	 */
	private String translateExpression(String replacement) {
		if (replacement != null && replacement.matches(".*(\\$component|\\$logic|\\$result).*")) {
			replacement = replacement.replaceAll("\\$component", "\\$1").replaceAll("\\$logic", "\\$2");
		}

		return replacement;
	}

	private String getForward(String componentName, String result) throws ViewException {
		String value = componentName + ";" + result;
		return value.replaceAll(regex, replacement);
	}

	public void directForward(LogicRequest logicRequest, String result, String forwardUrl) throws ViewException {
		try {
			logicRequest.getRequest().getRequestDispatcher(forwardUrl).forward(
				logicRequest.getRequest(), logicRequest.getResponse());
		}
		catch (Exception ex) {
			throw new ViewException(ex);
		}
	}

	public void redirect(LogicRequest logicRequest, String url) throws ViewException {
		try {
			logicRequest.getResponse().sendRedirect(url);
		}
		catch (IOException e) {
			throw new ViewException(e);
		}
	}
}

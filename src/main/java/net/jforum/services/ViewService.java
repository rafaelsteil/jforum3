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
package net.jforum.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.exceptions.ForumException;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.vraptor.LogicRequest;

/**
 * @author Rafael Steil
 */
public class ViewService {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private JForumConfig config;

	public ViewService(HttpServletRequest request, HttpServletResponse response, JForumConfig config) {
		this.request = request;
		this.response = response;
		this.config = config;
	}

	/**
	 * Render a specific view.
	 * @param methodName the action's name to render
	 */
	public void renderView(String methodName) {
		request.setAttribute(ConfigKeys.RENDER_CUSTOM_LOGIC, methodName);
	}

	/**
	 * Render a specific view
	 * @param component the component name to which the view belongs to
	 * @param methodName the name of the action to render
	 */
	public void renderView(String component, String methodName) {
		this.renderView(methodName);
		request.setAttribute(ConfigKeys.RENDER_CUSTOM_COMPONENT, component);
	}

	/**
	 * Redirect to a specific action in the current module.
	 * This method will build an url like module/action/arg1/arg2.servletExtension
	 * @param action the action name to redirect
	 * @param args optional arguments.
	 */
	public void redirectToAction(String action, Object... args) {
		LogicRequest logicRequest = (LogicRequest)request.getAttribute("context");
		this.redirectToAction(logicRequest.getRequestInfo().getComponentName(), action, args);
	}

	/**
	 * Redirect to a specific module and action.
	 *
	 * @param module the module name
	 * @param action the action name
	 * @param args optional arguments.
	 */
	public void redirectToAction(String module, String action, Object... args) {
		this.redirect(this.buildUrl(module, action, args));
	}

	/**
	 * Redirects to a specific URL
	 * @param url the url to redirect
	 */
	public void redirect(String url) {
		request.setAttribute(ConfigKeys.IGNORE_VIEW_MANAGER_REDIRECT, "true");

		try {
			response.sendRedirect(url);
		}
		catch (Exception e) {
			throw new ForumException(e);
		}
	}

	/**
	 * Build an URL from a given module, action and optional arguments.
	 *
	 * @param module the module
	 * @param action the action
	 * @param args optional arguments
	 * @return an URL like /contextPath/module/action/arg1/arg2.servletExtension
	 */
	public String buildUrl(String module, String action, Object... args) {
		StringBuilder sb = new StringBuilder()
			.append(request.getContextPath()).append('/')
			.append(module).append('/')
			.append(action);

		if (args.length > 0) {
			sb.append('/');

			for (int i = 0; i < args.length - 1; i++) {
				sb.append(args[i]).append('/');
			}

			sb.append(args[args.length - 1]);
		}

		sb.append(config.getValue(ConfigKeys.SERVLET_EXTENSION));

		return sb.toString();
	}

	/**
	 * get Request Context Path
	 * @return
	 */
	public String getContextPath(){
		return request.getContextPath();
	}

	/**
	 * Get the forum link.
	 * @return the forum link, always with a trailing slash
	 */
	public String getForumLink() {
		String forumLink = config.getValue(ConfigKeys.FORUM_LINK);

		if (forumLink.charAt(forumLink.length() - 1) != '/') {
			forumLink += "/";
		}
		return forumLink;
	}

	/**
	 * Gets the refering url, if any
	 * @return the url
	 */
	public String getReferer() {
		return request.getHeader("Referer");
	}

	/**
	 * Displays the login page
	 */
	public void displayLogin() {
		this.redirectToAction(Domain.USER, Actions.LOGIN);
	}

	/**
	 * Displays the "Access Denied" page
	 */
	public void accessDenied() {
		this.redirectToAction(Domain.MESSAGES, Actions.ACCESS_DENIED);
	}

	/**
	 * @param downloadPath
	 * @param realFilename
	 * @param filesize
	 */
	public void startDownload(String downloadPath, String realFilename, long filesize) {
		FileInputStream fis = null;
		OutputStream os = null;

		try {
			fis = new FileInputStream(downloadPath);
			os = response.getOutputStream();

			response.setContentType("application/octet-stream");

			if (request.getHeader("User-Agent").indexOf("Firefox") != -1) {
				response.setHeader("Content-Disposition", "attachment; filename=\""
					+ new String(realFilename.getBytes(config.getValue(ConfigKeys.ENCODING)),
						config.getValue(ConfigKeys.DEFAULT_CONTAINER_ENCODING)) + "\";");
			}
			else {
				response.setHeader("Content-Disposition", "attachment; filename=\""
					+ this.toUtf8String(realFilename) + "\";");
			}

			response.setContentLength((int)filesize);

			int c;
			byte[] b = new byte[4096];
			while ((c = fis.read(b)) != -1) {
				os.write(b, 0, c);
			}
		}
		catch (IOException e) {
			throw new ForumException(e);
		}
		finally {
			if (fis != null) {
				try { fis.close(); }
				catch (Exception e) {}
			}

			if (os != null) {
				try { os.close(); }
				catch (Exception e) {}
			}
		}
	}

	public String toUtf8String(String s)
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if ((c >= 0) && (c <= 255)) {
				sb.append(c);
			}
			else {
				byte[] b;

				try {
					b = Character.toString(c).getBytes("utf-8");
				}
				catch (Exception ex) {
					System.out.println(ex);
					b = new byte[0];
				}

				for (byte k : b) {
					if (k < 0) {
						k += 256;
					}

					sb.append('%').append(Integer.toHexString(k).toUpperCase());
				}
			}
		}

		return sb.toString();
	}
}

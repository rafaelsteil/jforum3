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

import java.io.File;
import java.util.List;

import net.jforum.core.UrlPattern;
import net.jforum.core.exceptions.ForumException;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.vraptor.component.ComponentManager;
import org.vraptor.component.ComponentNotFoundException;
import org.vraptor.component.ComponentType;
import org.vraptor.component.LogicMethod;
import org.vraptor.component.LogicNotFoundException;
import org.vraptor.http.VRaptorServletRequest;
import org.vraptor.interceptor.BasicUploadedFileInformation;
import org.vraptor.interceptor.UploadedFileInformation;
import org.vraptor.url.InvalidURLException;
import org.vraptor.url.LogicLocator;

/**
 * Given an URL, extracts the component and method that should be executed.
 * @author Rafael Steil
 */
public class DefaultLogicLocator implements LogicLocator {
	private static final Logger logger = Logger.getLogger(DefaultLogicLocator.class);
	private final File temporaryDirectory;
	private ComponentManager manager;
	private JForumConfig config;

	public DefaultLogicLocator(ComponentManager manager) {
		this.manager = manager;
		this.temporaryDirectory = new File(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * @see org.vraptor.url.LogicLocator#locate(javax.servlet.http.HttpServletRequest)
	 */
	public LogicMethod locate(VRaptorServletRequest request) throws InvalidURLException, LogicNotFoundException, ComponentNotFoundException {
		ApplicationContext springContext = (ApplicationContext)request.getSession()
			.getServletContext().getAttribute(ConfigKeys.SPRING_CONTEXT);

		this.handleMultipartRequest(request);
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

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void handleMultipartRequest(VRaptorServletRequest servletRequest) {
		if (!FileUploadBase.isMultipartContent(servletRequest)) {
			return;
		}

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory(4096 * 16, this.temporaryDirectory);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		List<FileItem> fileItems;

		// assume we know there are two files. The first file is a small
		// text file, the second is unknown and is written to a file on
		// the server
		try {
			fileItems = upload.parseRequest(servletRequest);
		}
		catch (FileUploadException e) {
			logger.warn("There was some problem parsing this multipart request, or someone is not sending a "
				+ "RFC1867 compatible multipart request.", e);
			return;
		}

		for (FileItem item : fileItems) {
			if (item.isFormField()) {
				servletRequest.addParameterValue(item.getFieldName(), item.getString());
			}
			else {
				if (!item.getName().trim().equals("")) {
					try {
						File file = File.createTempFile("vraptor.", ".upload");
						item.write(file);

						UploadedFileInformation fileInformation = new BasicUploadedFileInformation(
							file, item.getName(), item.getContentType());

						this.registeUploadedFile(servletRequest, item.getFieldName(), fileInformation);
					}
					catch (Exception e) {
						logger.error("Nasty uploaded file " + item.getName(), e);
					}
				}
				else {
					logger.debug("A file field was empy: " + item.getFieldName());
				}
			}
		}
	}

	private void registeUploadedFile(VRaptorServletRequest request, String name, Object value) {
		if (request.getAttribute(name) == null) {
			request.setAttribute(name, value);
		}
		else {
			Object currentValue = request.getAttribute(name);

			if (!currentValue.getClass().isArray()) {
				request.setAttribute(name, new Object[] { currentValue, value });
			}
			else {
				Object[] currentArray = (Object[]) currentValue;
				Object[] newArray = new Object[currentArray.length + 1];

				for (int i = 0; i < currentArray.length; i++) {
					newArray[i] = currentArray[i];
				}

				newArray[currentArray.length] = value;
				request.setAttribute(name, newArray);
			}
		}
	}
}

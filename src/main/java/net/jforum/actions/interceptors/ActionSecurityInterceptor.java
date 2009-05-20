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

import net.jforum.core.SecurityConstraint;

import org.vraptor.LogicRequest;

/**
 * Intercepts and process the {@link SecurityConstraint} annotation for actions
 * @author Rafael Steil
 */
public class ActionSecurityInterceptor extends SecurityInterceptor {
	/**
	 * @see net.jforum.actions.interceptors.SecurityInterceptor#getAnnotation(org.vraptor.LogicRequest)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected SecurityConstraint getAnnotation(LogicRequest logicRequest) {
		return (SecurityConstraint)logicRequest.getLogicDefinition().getComponentType().getComponentClass()
			.getAnnotation(SecurityConstraint.class);
	}

	/**
	 * @see net.jforum.actions.interceptors.SecurityInterceptor#isAnnotationPresent(org.vraptor.LogicRequest)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected boolean isAnnotationPresent(LogicRequest logicRequest) {
		return logicRequest.getLogicDefinition().getComponentType().getComponentClass()
			.isAnnotationPresent(SecurityConstraint.class);
	}
}

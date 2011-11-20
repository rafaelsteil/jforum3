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
package net.jforum.actions;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import net.jforum.actions.interceptors.ControllerSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.security.AdministrationRule;

import org.junit.Test;
import org.vraptor.Interceptor;
import org.vraptor.annotations.InterceptedBy;

/**
 * @author Rafael Steil
 */
public abstract class AdminTestCase {
	private Class<?> type;

	public AdminTestCase(Class<?> type) {
		this.type = type;
	}

	@Test
	public void shouldHaveAdministrationRule() throws Exception {
		Assert.assertTrue(type.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(AdministrationRule.class, type.getAnnotation(SecurityConstraint.class).value());
		Assert.assertTrue(type.getAnnotation(SecurityConstraint.class).displayLogin());
	}

	@Test
	public void shouldBeInterceptedByActionSecurityInterceptor() {
		Assert.assertTrue(type.isAnnotationPresent(InterceptedBy.class));
		InterceptedBy interceptedBy = type.getAnnotation(InterceptedBy.class);
		List<Class<? extends Interceptor>> interceptors = Arrays.asList(interceptedBy.value());
		Assert.assertTrue(interceptors.contains(ControllerSecurityInterceptor.class));
	}
}

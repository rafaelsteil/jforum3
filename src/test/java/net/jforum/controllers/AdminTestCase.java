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
package net.jforum.controllers;

import static org.junit.Assert.*;
import net.jforum.core.SecurityConstraint;
import net.jforum.security.AdministrationRule;

import org.junit.Test;

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
		assertTrue(type.isAnnotationPresent(SecurityConstraint.class));
		assertEquals(AdministrationRule.class, type.getAnnotation(SecurityConstraint.class).value());
		assertTrue(type.getAnnotation(SecurityConstraint.class).displayLogin());
	}
}

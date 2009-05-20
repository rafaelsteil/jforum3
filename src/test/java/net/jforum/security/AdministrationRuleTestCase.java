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
package net.jforum.security;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;
import net.jforum.entities.UserSession;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class AdministrationRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private AdministrationRule rule = new AdministrationRule();

	@Test
	public void loggedIsAdministratorShouldAccept() {
		context.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isAdministrator(); will(returnValue(true));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void loggedIsCoAdministratorShouldAccept() {
		context.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCoAdministrator(); will(returnValue(true));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void notAdministratorShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCoAdministrator(); will(returnValue(false));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void notLoggedShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(false));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
		}});
	}
}

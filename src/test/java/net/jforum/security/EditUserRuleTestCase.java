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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;
import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.util.TestCaseUtils;
import net.jforum.repository.UserRepository;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class EditUserRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
    private UserRepository userRepository = context.mock(UserRepository.class);
	private EditUserRule rule = new EditUserRule(userRepository);
	private Map<String, String> parameterMap = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	{ put("userId", "1"); }};

	@Test
	public void loggedSameUserIdExpectSuccess() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("userId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));

			User user = new User(); user.setId(1);
			one(userSession).getUser(); will(returnValue(user));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void loggedDifferentUserIdIsAdministratorExpectSuccess() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("userId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));

			User user = new User(); user.setId(9);
			one(userSession).getUser(); will(returnValue(user));

			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(true));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void loggedDifferentUserIdIsCoAdministratorExpectSuccess() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("userId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));

			User user = new User(); user.setId(9);
			one(userSession).getUser(); will(returnValue(user));

			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCoAdministrator(); will(returnValue(true));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void loggedDifferentUserIdNotAdministratorNotCoAdministratorShouldDeny() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("userId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));

			User user = new User(); user.setId(9);
			one(userSession).getUser(); will(returnValue(user));

			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCoAdministrator(); will(returnValue(false));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void notLoggedShouldDeny() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("userId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(false));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test(expected = AccessRuleException.class)
	public void doestNotHaveUserIdExpectsException() {
		parameterMap.clear();

		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			ignoring(userSession); ignoring(roleManager);
		}});

		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void containsUserDotId() {
		parameterMap.clear(); parameterMap.put("user.id", "1");

		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("user.id"); will(returnValue("1"));
			ignoring(userSession); ignoring(roleManager);
		}});

		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void containsUserId() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("userId"); will(returnValue("1"));
			ignoring(userSession); ignoring(roleManager);
		}});

		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

}

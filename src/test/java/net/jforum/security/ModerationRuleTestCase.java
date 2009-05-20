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

import net.jforum.entities.UserSession;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ModerationRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private ModerationRule rule = new ModerationRule();

	@Test
	public void moderatorShouldProceed() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isModerator(); will(returnValue(true));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void notModeratorShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isModerator(); will(returnValue(false));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}
}

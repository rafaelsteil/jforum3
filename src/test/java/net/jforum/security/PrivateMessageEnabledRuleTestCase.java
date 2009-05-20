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

import net.jforum.entities.UserSession;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PrivateMessageEnabledRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private PrivateMessageEnabledRule rule = new PrivateMessageEnabledRule();

	@Test
	public void shouldProceed() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isPrivateMessageEnabled(); will(returnValue(true));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, null));
		context.assertIsSatisfied();
	}
}

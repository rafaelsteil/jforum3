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
package net.jforum.sso;

import javax.servlet.http.HttpServletRequest;

import net.jforum.entities.UserSession;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RemoteUserSSOTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private JForumConfig config = context.mock(JForumConfig.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private UserSession us = new UserSession();
	private SSO sso;

	@Test
	public void remoteUserNotNullSessionUserNameDoesNotMatchExpectFalse() {
		context.checking(new Expectations() {{
			one(request).getRemoteUser(); will(returnValue("user"));
		}});

		us.getUser().setUsername("another user");

		Assert.assertFalse(sso.isSessionValid(us));
	}

	@Test
	public void remoteUserNotNullAnonymousUserExpectFalse() {
		context.checking(new Expectations() {{
			one(request).getRemoteUser(); will(returnValue("user"));
		}});

		us.getUser().setId(1);

		Assert.assertFalse(sso.isSessionValid(us));
	}

	@Test
	public void remoteUserNullExpectFalse() {
		context.checking(new Expectations() {{
			one(request).getRemoteUser(); will(returnValue(null));
		}});

		Assert.assertFalse(sso.isSessionValid(us));
	}

	@Before
	public void setup() {
		us.setRequest(request);
		
		 sso = new RemoteUserSSO();
		 sso.setConfig(config);

		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.ANONYMOUS_USER_ID); will(returnValue(1));
		}});
	}
}

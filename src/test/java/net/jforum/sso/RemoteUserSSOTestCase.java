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

import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;

import net.jforum.entities.UserSession;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteUserSSOTestCase {
	
	@Mock private JForumConfig config;
	@Mock private HttpServletRequest request;
	private UserSession us = new UserSession();
	private SSO sso;

	@Test
	public void remoteUserNotNullSessionUserNameDoesNotMatchExpectFalse() {
		when(request.getRemoteUser()).thenReturn("user");

		us.getUser().setUsername("another user");

		Assert.assertFalse(sso.isSessionValid(us));
	}

	@Test
	public void remoteUserNotNullAnonymousUserExpectFalse() {
		when(request.getRemoteUser()).thenReturn("user");

		us.getUser().setId(1);

		Assert.assertFalse(sso.isSessionValid(us));
	}

	@Test
	public void remoteUserNullExpectFalse() {
		when(request.getRemoteUser()).thenReturn(null);

		Assert.assertFalse(sso.isSessionValid(us));
	}

	@Before
	public void setup() {
		us.setRequest(request);

		sso = new RemoteUserSSO();
		sso.setConfig(config);

		when(config.getInt(ConfigKeys.ANONYMOUS_USER_ID)).thenReturn(1);
	}
}

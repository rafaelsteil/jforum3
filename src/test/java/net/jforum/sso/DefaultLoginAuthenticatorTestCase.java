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

import net.jforum.entities.User;
import net.jforum.repository.UserRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class DefaultLoginAuthenticatorTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserRepository repository = context.mock(UserRepository.class);
	private DefaultLoginAuthenticator authenticator = new DefaultLoginAuthenticator(repository);

	@Test
	public void userHasActivationKeyButNotActiveExpectFail() {
		context.checking(new Expectations() {{
			User user = new User();
			user.setDeleted(false);
			user.setActivationKey("some key");
			user.setActive(false);

			one(repository).validateLogin("user", "passwd"); will(returnValue(user));
		}});

		User user = authenticator.validateLogin("user", "passwd", null);
		Assert.assertNull(user);
	}

	@Test
	public void activationKeyNotNullNotActiveExpectFail() {
		context.checking(new Expectations() {{
			User user = new User();
			user.setDeleted(false);
			user.setActivationKey("some key");

			one(repository).validateLogin("user", "passwd"); will(returnValue(user));
		}});

		User user = authenticator.validateLogin("user", "passwd", null);
		Assert.assertNull(user);
	}

	@Test
	public void userDeletedExpectFail() {
		context.checking(new Expectations() {{
			User user = new User();
			user.setDeleted(true);

			one(repository).validateLogin("user", "passwd"); will(returnValue(user));
		}});

		User user = authenticator.validateLogin("user", "passwd", null);
		Assert.assertNull(user);
	}

	@Test
	public void invalidLoginFail() {
		context.checking(new Expectations() {{
			one(repository).validateLogin("user", "passwd"); will(returnValue(null));
		}});

		User user = authenticator.validateLogin("user", "passwd", null);
		Assert.assertNull(user);
	}
}

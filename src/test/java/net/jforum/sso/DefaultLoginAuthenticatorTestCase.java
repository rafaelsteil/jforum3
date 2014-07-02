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
import net.jforum.entities.User;
import net.jforum.repository.UserRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultLoginAuthenticatorTestCase {
	
	@Mock private UserRepository repository;
	@InjectMocks private DefaultLoginAuthenticator authenticator;

	@Test
	public void userHasActivationKeyButNotActiveExpectFail() {
		User user = new User();
		user.setDeleted(false);
		user.setActivationKey("some key");
		user.setActive(false);

		when(repository.validateLogin("user", "passwd")).thenReturn(user);

		User userValidated = authenticator.validateLogin("user", "passwd", null);
		Assert.assertNull(userValidated);
	}

	@Test
	public void activationKeyNotNullNotActiveExpectFail() {
		User user = new User();
		user.setDeleted(false);
		user.setActive(false);
		user.setActivationKey("some key");

		when(repository.validateLogin("user", "passwd")).thenReturn(user);

		User userValidated = authenticator.validateLogin("user", "passwd", null);
		Assert.assertNull(userValidated);
	}

	@Test
	public void userDeletedExpectFail() {
		User user = new User();
		user.setDeleted(true);

		when(repository.validateLogin("user", "passwd")).thenReturn(user);

		User userValidated = authenticator.validateLogin("user", "passwd", null);
		Assert.assertNull(userValidated);
	}

	@Test
	public void invalidLoginFail() {
		when(repository.validateLogin("user", "passwd")).thenReturn(null);

		User userValidated = authenticator.validateLogin("user", "passwd", null);
		Assert.assertNull(userValidated);
	}
}

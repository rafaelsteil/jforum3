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

import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.PrivateMessageRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PrivateMessageOwnerRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private PrivateMessageRepository repository = context.mock(PrivateMessageRepository.class);
	private PrivateMessageOwnerRule rule = new PrivateMessageOwnerRule(repository);
	private Map<String, String> parameters = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	{ put("id", "1"); }};

	@Test
	public void invalidMessageShouldDeny() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(null));
			one(userSession).getUser(); will(returnValue(new User()));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void toUserDifferentFromCurrentUserAlsoNotSenderShouldDeny() {
		context.checking(new Expectations() {{
			PrivateMessage pm = new PrivateMessage();
			pm.setToUser(new User() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(2); }});
			pm.setFromUser(new User() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(3); }});

			one(repository).get(1); will(returnValue(pm));
			one(userSession).getUser(); will(returnValue(new User() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(1); }}));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void toUserDifferentFromCurrentUserIsSenderShouldAccept() {
		context.checking(new Expectations() {{
			PrivateMessage pm = new PrivateMessage();
			pm.setToUser(new User() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(2); }});
			pm.setFromUser(new User() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(1); }});

			one(repository).get(1); will(returnValue(pm));
			one(userSession).getUser(); will(returnValue(new User() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(1); }}));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test(expected = AccessRuleException.class)
	public void idNotFoundExpectException() {
		parameters.clear();
		rule.shouldProceed(userSession, request);
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(request).getParameterMap(); will(returnValue(parameters));
			allowing(request).getParameter("id"); will(returnValue(parameters.get("id")));
		}});
	}
}

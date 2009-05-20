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
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.TopicRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class AccessForumRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private TopicRepository topicRepository = context.mock(TopicRepository.class);
	private AccessForumRule rule = new AccessForumRule(topicRepository);
	private Map<String, String> parameterMap = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	{ put("topicId", "1"); }};

	@Test(expected = AccessRuleException.class)
	public void forumIdNotFoundExpectsException() {
		parameterMap.clear();

		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			ignoring(userSession); ignoring(roleManager);
		}});


		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	public void forumIdInForumIdParameter() {
		parameterMap.clear(); parameterMap.put("forumId", "1");

		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			ignoring(userSession); ignoring(roleManager);
		}});

		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void forumIsAllowedShouldProceed() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topicId"); will(returnValue("1"));

			Topic topic = new Topic(); topic.getForum().setId(7);

			one(topicRepository).get(1); will(returnValue(topic));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isForumAllowed(7); will(returnValue(true));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void forumIsBlockedShouldNotProceed() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topicId"); will(returnValue("1"));

			Topic topic = new Topic(); topic.getForum().setId(7);

			one(topicRepository).get(1); will(returnValue(topic));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isForumAllowed(7); will(returnValue(false));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}
}

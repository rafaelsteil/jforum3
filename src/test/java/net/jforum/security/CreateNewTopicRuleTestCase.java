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

import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.Forum;
import net.jforum.entities.UserSession;
import net.jforum.repository.ForumRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class CreateNewTopicRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private Map<String, String> parameterMap = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	{ put("forumId", "1"); }};
	private ForumRepository repository = context.mock(ForumRepository.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);

	@Test
	public void findForumIdInTopicForumId() {
		parameterMap.clear();
		parameterMap.put("topic.forum.id", "1");

		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topic.forum.id"); will(returnValue("1"));
			ignoring(userSession); ignoring(roleManager);
			one(repository).get(1); will(returnValue(new Forum()));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void findForumIdKey() {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			ignoring(userSession); ignoring(roleManager);
			one(repository).get(1); will(returnValue(new Forum()));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test(expected = AccessRuleException.class)
	public void forumIdNotFoundExpectsException() {
		parameterMap.clear();

		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			ignoring(userSession); ignoring(roleManager);
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void loggedNotReadOnlyNotReplyOnlyForumAllowedShouldAccept() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(false));
			one(roleManager).isForumReplyOnly(1); will(returnValue(false));
			one(roleManager).getPostOnlyWithModeratorOnline(); will(returnValue(false));
			one(repository).get(1); will(returnValue(new Forum()));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void postOnlyWithModeratorOnlineModeratorIsOffLineShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(false));
			one(roleManager).isForumReplyOnly(1); will(returnValue(false));
			exactly(2).of(roleManager).getPostOnlyWithModeratorOnline(); will(returnValue(true));
			one(sessionManager).isModeratorOnline(); will(returnValue(false));
			one(repository).get(1); will(returnValue(new Forum()));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void postOnlyWithModeratorOnlineModeratorIsOnlineShouldAccept() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(false));
			one(roleManager).isForumReplyOnly(1); will(returnValue(false));
			exactly(2).of(roleManager).getPostOnlyWithModeratorOnline(); will(returnValue(true));
			one(sessionManager).isModeratorOnline(); will(returnValue(true));
			one(repository).get(1); will(returnValue(new Forum()));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void anonymousPostsNotAllowedShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(false));
			one(roleManager).isForumAllowed(1); will(returnValue(true));

			Forum forum = new Forum();
			forum.setAllowAnonymousPosts(false);
			one(repository).get(1); will(returnValue(forum));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void forumReplyOnlyShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(false));
			one(roleManager).isForumReplyOnly(1); will(returnValue(true));
			one(repository).get(1); will(returnValue(new Forum()));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void forumNotAllowedShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			one(roleManager).isForumAllowed(1); will(returnValue(false));
			one(repository).get(1); will(returnValue(new Forum()));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void forumReadOnlyShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("forumId"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(true));
			one(repository).get(1); will(returnValue(new Forum()));
		}});

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}
}

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
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ReplyTopicRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private Map<String, String> parameterMap = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	{ put("topic.forum.id", "1"); }};
	private TopicRepository topicRepository = context.mock(TopicRepository.class);
	private PostRepository postRepository = context.mock(PostRepository.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private ReplyTopicRule rule = new ReplyTopicRule(topicRepository, postRepository, forumRepository, sessionManager);

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

	@Test
	public void forumIdFromPostId() {
		parameterMap.clear(); parameterMap.put("postId", "2");

		context.checking(new Expectations() {{
			ignoring(userSession); ignoring(roleManager);
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("postId"); will(returnValue("2"));

			one(forumRepository).get(0); will(returnValue(new Forum()));

			Post post = new Post(); post.setForum(new Forum());
			one(postRepository).get(2); will(returnValue(post));
		}});

		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void forumIdFromTopicId() {
		parameterMap.clear(); parameterMap.put("topicId", "2");

		context.checking(new Expectations() {{
			ignoring(userSession); ignoring(roleManager);
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topicId"); will(returnValue("2"));
			one(topicRepository).get(2); will(returnValue(new Topic()));
			one(forumRepository).get(0); will(returnValue(new Forum()));
		}});

		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void forumIdFromTopicForumId() {
		context.checking(new Expectations() {{
			ignoring(userSession); ignoring(roleManager);
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topic.forum.id"); will(returnValue("1"));
			one(forumRepository).get(1); will(returnValue(new Forum()));
		}});

		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void loggedNotReadOnlyForumAllowedShouldAccept() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topic.forum.id"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(false));
			one(roleManager).getPostOnlyWithModeratorOnline(); will(returnValue(false));
			one(forumRepository).get(1); will(returnValue(new Forum()));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void postOnlyWithModeratorOnlineModeratorIsOfflineShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topic.forum.id"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(false));
			exactly(2).of(roleManager).getPostOnlyWithModeratorOnline(); will(returnValue(true));
			one(sessionManager).isModeratorOnline(); will(returnValue(false));
			one(forumRepository).get(1); will(returnValue(new Forum()));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void postOnlyWithModeratorOnlineModeratorIsOnlineShouldAccept() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topic.forum.id"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(false));
			exactly(2).of(roleManager).getPostOnlyWithModeratorOnline(); will(returnValue(true));
			one(sessionManager).isModeratorOnline(); will(returnValue(true));
			one(forumRepository).get(1); will(returnValue(new Forum()));
		}});

		Assert.assertTrue(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void anonymousPostsNotAllowedShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topic.forum.id"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(false));
			one(roleManager).isForumAllowed(1); will(returnValue(true));

			Forum forum = new Forum();
			forum.setAllowAnonymousPosts(false);

			one(forumRepository).get(1); will(returnValue(forum));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void forumNotAllowedShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topic.forum.id"); will(returnValue("1"));
			one(roleManager).isForumAllowed(1); will(returnValue(false));
			one(forumRepository).get(1); will(returnValue(new Forum()));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}

	@Test
	public void forumReadOnlyShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("topic.forum.id"); will(returnValue("1"));
			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(roleManager).isForumReadOnly(1); will(returnValue(true));
			one(forumRepository).get(1); will(returnValue(new Forum()));
		}});

		Assert.assertFalse(rule.shouldProceed(userSession, request));
		context.assertIsSatisfied();
	}
}

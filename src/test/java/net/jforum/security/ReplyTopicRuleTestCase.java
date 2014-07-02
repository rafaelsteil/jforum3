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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ReplyTopicRuleTestCase {

	@Mock private UserSession userSession;
	@Mock private HttpServletRequest request;
	@Mock private RoleManager roleManager;
	private Map<String, String[]> parameterMap;
	@Mock private TopicRepository topicRepository;
	@Mock private PostRepository postRepository;
	@Mock private ForumRepository forumRepository;
	@Mock private SessionManager sessionManager;
	@InjectMocks private ReplyTopicRule rule;

	@Before
	public void setup() {
		parameterMap = new HashMap<String, String[]>();
		parameterMap.put("topic.forum.id", Arrays.asList("1").toArray(new String[1]));
		when(request.getParameterMap()).thenReturn(parameterMap);
		when(userSession.getRoleManager()).thenReturn(roleManager);
	}

	@Test(expected = AccessRuleException.class)
	public void forumIdNotFoundExpectsException() {
		parameterMap.clear();

		rule.shouldProceed(userSession, request);
	}

	@Test
	public void forumIdFromPostId() {
		parameterMap.clear(); parameterMap.put("postId", Arrays.asList("2").toArray(new String[1]));
		when(request.getParameter("postId")).thenReturn("2");
		when(forumRepository.get(0)).thenReturn(new Forum());
		Post post = new Post(); post.setForum(new Forum());
		when(postRepository.get(2)).thenReturn(post);

		rule.shouldProceed(userSession, request);
	}

	@Test
	public void forumIdFromTopicId() {
		parameterMap.clear(); parameterMap.put("topicId", Arrays.asList("2").toArray(new String[1]));
		when(request.getParameter("topicId")).thenReturn("2");
		when(topicRepository.get(2)).thenReturn(new Topic());
		when(forumRepository.get(0)).thenReturn(new Forum());

		rule.shouldProceed(userSession, request);
	}

	@Test
	public void forumIdFromTopicForumId() {
		when(request.getParameter("topic.forum.id")).thenReturn("1");
		when(forumRepository.get(1)).thenReturn(new Forum());

		rule.shouldProceed(userSession, request);
	}

	@Test
	public void loggedNotReadOnlyForumAllowedShouldAccept() {
		when(request.getParameter("topic.forum.id")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(false);
		when(roleManager.getPostOnlyWithModeratorOnline()).thenReturn(false);
		when(forumRepository.get(1)).thenReturn(new Forum());

		assertTrue(rule.shouldProceed(userSession, request));
	}

	@Test
	public void postOnlyWithModeratorOnlineModeratorIsOfflineShouldDeny() {
		when(request.getParameter("topic.forum.id")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(false);
		when(roleManager.getPostOnlyWithModeratorOnline()).thenReturn(true);
		when(sessionManager.isModeratorOnline()).thenReturn(false);
		when(forumRepository.get(1)).thenReturn(new Forum());

		assertFalse(rule.shouldProceed(userSession, request));

	}

	@Test
	public void postOnlyWithModeratorOnlineModeratorIsOnlineShouldAccept() {
		when(request.getParameter("topic.forum.id")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(false);
		when(roleManager.getPostOnlyWithModeratorOnline()).thenReturn(true);
		when(sessionManager.isModeratorOnline()).thenReturn(true);
		when(forumRepository.get(1)).thenReturn(new Forum());

		assertTrue(rule.shouldProceed(userSession, request));

	}

	@Test
	public void anonymousPostsNotAllowedShouldDeny() {
		when(request.getParameter("topic.forum.id")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(false);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		Forum forum = new Forum();
		forum.setAllowAnonymousPosts(false);

		when(forumRepository.get(1)).thenReturn(forum);

		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void forumNotAllowedShouldDeny() {
		when(request.getParameter("topic.forum.id")).thenReturn("1");
		when(roleManager.isForumAllowed(1)).thenReturn(false);
		when(forumRepository.get(1)).thenReturn(new Forum());

		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void forumReadOnlyShouldDeny() {
		when(request.getParameterMap()).thenReturn(parameterMap);
		when(request.getParameter("topic.forum.id")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(true);
		when(forumRepository.get(1)).thenReturn(new Forum());

		assertFalse(rule.shouldProceed(userSession, request));

	}
}

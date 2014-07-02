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
import net.jforum.entities.UserSession;
import net.jforum.repository.ForumRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateNewTopicRuleTestCase {

	@Mock private UserSession userSession;
	@Mock private HttpServletRequest request;
	@Mock private RoleManager roleManager;
	private Map<String, String[]> parameterMap;
	@Mock private ForumRepository repository;
	@Mock private SessionManager sessionManager;

	@Before
	public void setup() {
		parameterMap = new HashMap<String, String[]>();
		parameterMap.put("forumId", Arrays.asList("1").toArray(new String[1]));
		when(request.getParameterMap()).thenReturn(parameterMap);
		when(userSession.getRoleManager()).thenReturn(roleManager);
	}

	@Test
	public void findForumIdInTopicForumId() {
		parameterMap.clear();
		parameterMap.put("topic.forum.id", Arrays.asList("1").toArray(new String[1]));

		when(request.getParameter("topic.forum.id")).thenReturn("1");
		when(repository.get(1)).thenReturn(new Forum());

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		rule.shouldProceed(userSession, request);
	}

	@Test
	public void findForumIdKey() {
		when(request.getParameter("forumId")).thenReturn("1");
		when(repository.get(1)).thenReturn(new Forum());

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		rule.shouldProceed(userSession, request);
	}

	@Test(expected = AccessRuleException.class)
	public void forumIdNotFoundExpectsException() {
		parameterMap.clear();

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		rule.shouldProceed(userSession, request);
	}

	@Test
	public void loggedNotReadOnlyNotReplyOnlyForumAllowedShouldAccept() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(request.getParameter("forumId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(false);
		when(roleManager.isForumReplyOnly(1)).thenReturn(false);
		when(roleManager.getPostOnlyWithModeratorOnline()).thenReturn(false);
		when(repository.get(1)).thenReturn(new Forum());

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		assertTrue(rule.shouldProceed(userSession, request));
	}

	@Test
	public void postOnlyWithModeratorOnlineModeratorIsOffLineShouldDeny() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(request.getParameter("forumId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(false);
		when(roleManager.isForumReplyOnly(1)).thenReturn(false);
		when(roleManager.getPostOnlyWithModeratorOnline()).thenReturn(true);
		when(sessionManager.isModeratorOnline()).thenReturn(false);
		when(repository.get(1)).thenReturn(new Forum());

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void postOnlyWithModeratorOnlineModeratorIsOnlineShouldAccept() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(request.getParameter("forumId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(false);
		when(roleManager.isForumReplyOnly(1)).thenReturn(false);
		when(roleManager.getPostOnlyWithModeratorOnline()).thenReturn(true);
		when(sessionManager.isModeratorOnline()).thenReturn(true);
		when(repository.get(1)).thenReturn(new Forum());

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		assertTrue(rule.shouldProceed(userSession, request));
	}

	@Test
	public void anonymousPostsNotAllowedShouldDeny() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(request.getParameter("forumId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(false);
		when(roleManager.isForumAllowed(1)).thenReturn(true);

		Forum forum = new Forum();
		forum.setAllowAnonymousPosts(false);
		when(repository.get(1)).thenReturn(forum);

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void forumReplyOnlyShouldDeny() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(request.getParameter("forumId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(false);
		when(roleManager.isForumReplyOnly(1)).thenReturn(true);
		when(repository.get(1)).thenReturn(new Forum());

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void forumNotAllowedShouldDeny() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(request.getParameter("forumId")).thenReturn("1");
		when(roleManager.isForumAllowed(1)).thenReturn(false);
		when(repository.get(1)).thenReturn(new Forum());

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void forumReadOnlyShouldDeny() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(request.getParameter("forumId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumReadOnly(1)).thenReturn(true);
		when(repository.get(1)).thenReturn(new Forum());

		CreateNewTopicRule rule = new CreateNewTopicRule(repository, sessionManager);
		assertFalse(rule.shouldProceed(userSession, request));
	}
}

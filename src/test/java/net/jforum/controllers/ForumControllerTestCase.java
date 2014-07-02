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
package net.jforum.controllers;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.MostUsersEverOnline;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.AccessForumRule;
import net.jforum.security.RoleManager;
import net.jforum.services.MostUsersEverOnlineService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.GroupInteractionFilter;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ForumControllerTestCase {	
	@Mock private CategoryRepository categoryRepository;
	@Mock private ForumRepository forumRepository;
	@Mock private UserSession userSession;
	@Mock private UserRepository userRepository;
	@Mock private MostUsersEverOnlineService mostUsersEverOnlineService;
	@Mock private JForumConfig config;
	@Mock private GroupInteractionFilter groupInteractionFilter;
	@Spy private MockResult mockResult;
	@Mock private SessionManager sessionManager;
	
	@InjectMocks private ForumController controller;
	
	@Mock RoleManager roleManager;
	
	@Test
	public void showShouldHaveAccessForumConstraint() throws Exception {
		Method method = controller.getClass().getMethod("show", int.class, int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(AccessForumRule.class, method.getAnnotation(SecurityConstraint.class).value());
		Assert.assertTrue(method.getAnnotation(SecurityConstraint.class).displayLogin());
	}

	@Test
	public void show() {
		Forum forum = new Forum(forumRepository);
		ArrayList<Category> categories = new ArrayList<Category>();
		
		when(forumRepository.getTotalTopics(forum)).thenReturn(1);
		when(forumRepository.get(1)).thenReturn(forum);
		when(config.getInt(ConfigKeys.TOPICS_PER_PAGE)).thenReturn(10);
		when(categoryRepository.getAllCategories()).thenReturn(categories);
		when(sessionManager.isModeratorOnline()).thenReturn(true);
				
		controller.show(1, 0);
		
		verify(forumRepository).getTopics(forum, 0, 10);
		assertEquals(new ArrayList<Topic>(), mockResult.included("topics"));
		assertEquals(forum, mockResult.included("forum"));
		assertEquals(categories, mockResult.included("categories"));
		assertNotNull(mockResult.included("pagination"));
		assertEquals(true, mockResult.included("isModeratorOnline"));
		
	}

	@Test
	public void listCannotInteractWitOtherGroups() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)).thenReturn(false);

		controller.list();

		verify(groupInteractionFilter).filterForumListing(mockResult, userSession);
	}

	@Test
	public void list() {
		MostUsersEverOnline most = new MostUsersEverOnline();
		ArrayList<Category> categories = new ArrayList<Category>();
		ArrayList<UserSession> userSessions = new ArrayList<UserSession>();
		
		when(categoryRepository.getAllCategories()).thenReturn(categories);
		when(sessionManager.getLoggedSessions()).thenReturn(userSessions);
		when(userRepository.getTotalUsers()).thenReturn(1);
		when(forumRepository.getTotalMessages()).thenReturn(2);
		when(sessionManager.getTotalLoggedUsers()).thenReturn(3);
		when(sessionManager.getTotalAnonymousUsers()).thenReturn(4);
		when(userRepository.getLastRegisteredUser()).thenReturn(new User());
		when(mostUsersEverOnlineService.getMostRecentData(anyInt())).thenReturn(most);
		when(sessionManager.getTotalUsers()).thenReturn(3);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(7);
		when(userSession.isLogged()).thenReturn(false);

		controller.list();

		assertEquals(categories,mockResult.included("categories"));
		assertEquals(userSessions,mockResult.included("onlineUsers"));
		assertEquals(1,mockResult.included("totalRegisteredUsers"));
		assertEquals(2,mockResult.included("totalMessages"));
		assertEquals(3,mockResult.included("totalLoggedUsers"));
		assertEquals(4,mockResult.included("totalAnonymousUsers"));
		assertEquals(new User(),mockResult.included("lastRegisteredUser"));
		assertEquals(most,mockResult.included("mostUsersEverOnline"));
		assertEquals(7,mockResult.included("postsPerPage"));
	}
}

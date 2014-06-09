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
import static org.mockito.Mockito.*;
import net.jforum.actions.helpers.Actions;
import net.jforum.entities.UserSession;
import net.jforum.security.RoleManager;
import net.jforum.services.RSSService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.junit.Before;
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
public class RSSControllerTestCase {
	
	@Mock private RSSService rssService;
	@Mock private UserSession userSession;
	@Mock private JForumConfig config;
	@Mock private RoleManager roleManager;
	@Spy private MockResult mockResult;
	@InjectMocks private RSSController controller;
	@Mock private MessageController mockMessageController;

	@Test
	public void forumTopicsExpectSuccess() {
		when(config.getBoolean(ConfigKeys.RSS_ENABLED)).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(rssService.forForum(1)).thenReturn("contents");
			
		controller.forumTopics(1);
		
		assertEquals("contents", mockResult.included("contents"));
		verify(mockResult).forwardTo(Actions.RSS);
	}

	@Test
	public void forumTopicsUserDoesNotHaveRightsShouldDeny() {
		when(config.getBoolean(ConfigKeys.RSS_ENABLED)).thenReturn(true);
		when(roleManager.isForumAllowed(1)).thenReturn(false);
		when(mockResult.forwardTo(MessageController.class)).thenReturn(mockMessageController);
			
		controller.forumTopics(1);
		
		verify(mockMessageController).accessDenied();
	}

	@Test
	public void forumTopicsRSSDisabledShouldDeny() {
		when(config.getBoolean(ConfigKeys.RSS_ENABLED)).thenReturn(false);
		when(mockResult.forwardTo(MessageController.class)).thenReturn(mockMessageController);
			
		controller.forumTopics(1);
		
		verify(mockMessageController).accessDenied();
	}

	@Before
	public void setup() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
	}
}

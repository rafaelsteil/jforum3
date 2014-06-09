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

import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.TopicRepository;

import org.junit.Assert;
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
public class AccessForumRuleTestCase {
	
	@Mock private UserSession userSession;
	@Mock private HttpServletRequest request;
	@Mock private RoleManager roleManager;
	@Mock private TopicRepository topicRepository;
	@InjectMocks private AccessForumRule rule;
	private Map<String, String[]> parameterMap;
	
	@Before
	public void setup() {
		parameterMap = new HashMap<String, String[]>();
		parameterMap.put("topicId", Arrays.asList("1").toArray(new String[1]));
		when(request.getParameterMap()).thenReturn(parameterMap);
	}

	@Test(expected = AccessRuleException.class)
	public void forumIdNotFoundExpectsException() {
		parameterMap.clear();
		
			
		rule.shouldProceed(userSession, request);
	}

	public void forumIdInForumIdParameter() {
		parameterMap.clear(); parameterMap.put("forumId", Arrays.asList("1").toArray(new String[1]));

		when(request.getParameter("forumId")).thenReturn("1");
			
		rule.shouldProceed(userSession, request);
	}

	@Test
	public void forumIsAllowedShouldProceed() {
		when(request.getParameter("topicId")).thenReturn("1");

		Topic topic = new Topic(); topic.getForum().setId(7);

		when(topicRepository.get(1)).thenReturn(topic);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isForumAllowed(7)).thenReturn(true);
	
		assertTrue(rule.shouldProceed(userSession, request));
	}

	@Test
	public void forumIsBlockedShouldNotProceed() {
		when(request.getParameter("topicId")).thenReturn("1");

		Topic topic = new Topic(); topic.getForum().setId(7);

		when(topicRepository.get(1)).thenReturn(topic);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isForumAllowed(7)).thenReturn(false);
	
		Assert.assertFalse(rule.shouldProceed(userSession, request));
		
	}
}

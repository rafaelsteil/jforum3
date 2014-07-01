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
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.PrivateMessageRepository;

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
public class PrivateMessageOwnerRuleTestCase {
	
	@Mock private UserSession userSession;
	@Mock private HttpServletRequest request;
	@Mock private PrivateMessageRepository repository;
	@InjectMocks private PrivateMessageOwnerRule rule;
	private Map<String, String[]> parameters;
	
	@Before
	public void setup() {
		parameters = new HashMap<String, String[]>();
		parameters.put("id", Arrays.asList("1").toArray(new String[1]));
		when(request.getParameterMap()).thenReturn(parameters);
		when(request.getParameter("id")).thenReturn("1");
	}

	@Test
	public void invalidMessageShouldDeny() {
		
		when(repository.get(1)).thenReturn(null);
		when(userSession.getUser()).thenReturn(new User());
		
		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void toUserDifferentFromCurrentUserAlsoNotSenderShouldDeny() {
		PrivateMessage pm = new PrivateMessage();
		User user1 = new User();
		user1.setId(1);
		User user2 = new User();
		user1.setId(2);
		User user3 = new User();
		user1.setId(3);
		
		pm.setToUser(user2);
		pm.setFromUser(user3);
		when(userSession.getUser()).thenReturn(user1);
		when(repository.get(1)).thenReturn(pm);
		
		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void toUserDifferentFromCurrentUserIsSenderShouldAccept() {
		PrivateMessage pm = new PrivateMessage();
		User user1 = new User();
		user1.setId(1);
		User user2 = new User();
		user1.setId(2);
		
		pm.setToUser(user2);
		pm.setFromUser(user1);
		when(userSession.getUser()).thenReturn(user1);
		when(repository.get(1)).thenReturn(pm);
		
		assertTrue(rule.shouldProceed(userSession, request));
		
	}

	@Test(expected = AccessRuleException.class)
	public void idNotFoundExpectException() {
		parameters.clear();
		rule.shouldProceed(userSession, request);
	}
}

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
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.UserRepository;

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
public class EditUserRuleTestCase {

	@Mock private UserSession userSession;
	@Mock private HttpServletRequest request;
	@Mock private RoleManager roleManager;
	@Mock private UserRepository userRepository;
	@InjectMocks private EditUserRule rule;
	private Map<String, String[]> parameterMap;

	@Before
	public void setup() {
		parameterMap = new HashMap<String, String[]>();
		parameterMap.put("userId", Arrays.asList("1").toArray(new String[1]));
		when(request.getParameterMap()).thenReturn(parameterMap);
		when(userSession.getRoleManager()).thenReturn(roleManager);
	}
	
	@Test
	public void loggedSameUserIdExpectSuccess() {
		when(request.getParameter("userId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);

		User user = new User(); user.setId(1);
		when(userSession.getUser()).thenReturn(user);

		assertTrue(rule.shouldProceed(userSession, request));
	}

	@Test
	public void loggedDifferentUserIdIsAdministratorExpectSuccess() {
		when(request.getParameter("userId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(true);

		User currentUser = new User(); currentUser.setId(9);
		when(userSession.getUser()).thenReturn(currentUser);

		User user1 = new User(); user1.setId(1);
		when(userRepository.get(1)).thenReturn(user1);

		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.getCanEditUser(user1, currentUser.getGroups())).thenReturn(true);


		boolean shouldProceed = rule.shouldProceed(userSession, request);

		assertTrue(shouldProceed);
	}

	@Test
	public void notLoggedShouldDeny() {
		when(request.getParameter("userId")).thenReturn("1");
		when(userSession.isLogged()).thenReturn(false);

		boolean shouldProceed = rule.shouldProceed(userSession, request);

		assertFalse(shouldProceed);
	}

	@Test(expected = AccessRuleException.class)
	public void doestNotHaveUserIdExpectsException() {
		parameterMap.clear();
		
		rule.shouldProceed(userSession, request);
	}

	@Test
	public void containsUserDotId() {
		parameterMap.clear(); parameterMap.put("user.id", Arrays.asList("1").toArray(new String[1]));
		when(request.getParameter("user.id")).thenReturn("1");
		when(userRepository.get(1)).thenReturn(new User());

		rule.shouldProceed(userSession, request);
	}

	@Test
	public void containsUserId() {
		when(request.getParameter("userId")).thenReturn("1");
		when(userRepository.get(1)).thenReturn(new User());

		rule.shouldProceed(userSession, request);
	}

}

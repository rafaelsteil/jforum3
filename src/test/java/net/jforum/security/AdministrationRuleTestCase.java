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

import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;

import javax.servlet.http.HttpServletRequest;

import net.jforum.entities.UserSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class AdministrationRuleTestCase {

	@Mock private UserSession userSession;
	@Mock private HttpServletRequest request;
	@Mock private RoleManager roleManager;
	private AdministrationRule rule = new AdministrationRule();

	@Test
	public void loggedIsAdministratorShouldAccept() {
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isAdministrator()).thenReturn(true);

		assertTrue(rule.shouldProceed(userSession, request));
	}

	@Test
	public void loggedIsCoAdministratorShouldAccept() {
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isCoAdministrator()).thenReturn(true);


		assertTrue(rule.shouldProceed(userSession, request));
	}

	@Test
	public void notAdministratorShouldDeny() {
		when(userSession.isLogged()).thenReturn(true);
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isCoAdministrator()).thenReturn(false);

		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Test
	public void notLoggedShouldDeny() {
		when(userSession.isLogged()).thenReturn(false);

		assertFalse(rule.shouldProceed(userSession, request));
	}

	@Before
	public void setup() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
	}
}

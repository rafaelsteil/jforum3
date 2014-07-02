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

import javax.servlet.http.HttpServletRequest;

import net.jforum.entities.UserSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ModerationRuleTestCase {

	@Mock private UserSession userSession;
	@Mock private RoleManager roleManager;
	@Mock private HttpServletRequest request;
	private ModerationRule rule = new ModerationRule();

	@Test
	public void moderatorShouldProceed() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isModerator()).thenReturn(true);

		assertTrue(rule.shouldProceed(userSession, request));
	}

	@Test
	public void notModeratorShouldDeny() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isModerator()).thenReturn(false);

		assertFalse(rule.shouldProceed(userSession, request));
	}
}

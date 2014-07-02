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
import net.jforum.entities.UserSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class PrivateMessageEnabledRuleTestCase {

	@Mock private UserSession userSession;
	@Mock private RoleManager roleManager;
	private PrivateMessageEnabledRule rule = new PrivateMessageEnabledRule();

	@Test
	public void shouldProceed() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isPrivateMessageEnabled()).thenReturn(true);

		assertTrue(rule.shouldProceed(userSession, null));
	}
}

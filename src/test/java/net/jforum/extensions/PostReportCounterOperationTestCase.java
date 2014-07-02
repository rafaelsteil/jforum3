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
package net.jforum.extensions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostReportRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.SecurityConstants;

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
public class PostReportCounterOperationTestCase {

	@Mock private PostReportRepository repository;
	@Mock private UserSession userSession;
	@Mock private RoleManager roleManager;
	@Spy private MockResult mockResult;
	@InjectMocks private PostReportCounterOperation operation;

	@Test
	public void notLoggedExpectZero() {
		when(userSession.isLogged()).thenReturn(false);

		operation.execute();
		
		assertEquals(0, mockResult.included("totalPostReports"));
	}

	@Test
	public void notModeratorExpectZero() {
		when(userSession.isLogged()).thenReturn(true);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isModerator()).thenReturn(false);

		operation.execute();
		
		assertEquals(0, mockResult.included("totalPostReports"));
	}

	@Test
	public void moderatorExpect10() {
		int[] forumIds = { 1, 2 };

		when(userSession.isLogged()).thenReturn(true);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isModerator()).thenReturn(true);
		when(roleManager.getRoleValues(SecurityConstants.FORUM)).thenReturn(forumIds);
		when(repository.countPendingReports(forumIds)).thenReturn(10);

		operation.execute();
		
		assertEquals(10, mockResult.included("totalPostReports"));
	}
}

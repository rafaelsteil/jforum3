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

import net.jforum.core.SessionManager;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostReportRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.SecurityConstants;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
public class PostReportCounterOperationTestCase {
	private Mockery mockery = TestCaseUtils.newMockery();
	private PostReportRepository repository = mockery.mock(PostReportRepository.class);
	private SessionManager sessionManager = mockery.mock(SessionManager.class);
	private UserSession userSession = mockery.mock(UserSession.class);
	private RoleManager roleManager = mockery.mock(RoleManager.class);
	private Result mockResult = mockery.mock(Result.class);
	private PostReportCounterOperation operation = new PostReportCounterOperation(repository, sessionManager, mockResult);

	@Test
	public void notLoggedExpectZero() {
		mockery.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(false));
			one(mockResult).include("totalPostReports", 0);
		}});

		operation.execute();
		mockery.assertIsSatisfied();
	}

	@Test
	public void notModeratorExpectZero() {
		mockery.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(true));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isModerator(); will(returnValue(false));
			one(mockResult).include("totalPostReports", 0);
		}});

		operation.execute();
		mockery.assertIsSatisfied();
	}

	@Test
	public void moderatorExpect10() {
		mockery.checking(new Expectations() {{
			int[] forumIds = { 1, 2 };

			one(userSession).isLogged(); will(returnValue(true));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isModerator(); will(returnValue(true));
			one(roleManager).getRoleValues(SecurityConstants.FORUM); will(returnValue(forumIds));
			one(repository).countPendingReports(forumIds); will(returnValue(10));
			one(mockResult).include("totalPostReports", 10);
		}});

		operation.execute();
		mockery.assertIsSatisfied();
	}

	@Before
	public void setup() {
		mockery.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
		}});
	}
}

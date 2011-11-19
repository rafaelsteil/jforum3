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
package net.jforum.actions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.controllers.PostReportController;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.PostReport;
import net.jforum.entities.PostReportStatus;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.PaginatedResult;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.PostReportRepository;
import net.jforum.security.ModerationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.Interceptor;
import org.vraptor.annotations.InterceptedBy;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class PostReportActionsTestCase {
	private Mockery mockery = TestCaseUtils.newMockery();
	private SessionManager sessionManager = mockery.mock(SessionManager.class);
	private UserSession userSession = mockery.mock(UserSession.class);
	private RoleManager roleManager = mockery.mock(RoleManager.class);
	private PostReportRepository repository = mockery.mock(PostReportRepository.class);
	private JForumConfig config = mockery.mock(JForumConfig.class);
	private MockResult mockResult = new MockResult();
	private PostReportController action = new PostReportController(repository, sessionManager, config, mockResult);

	@Test
	public void listResolved() {
		mockery.checking(new Expectations() {{
			ignoring(sessionManager); ignoring(roleManager);
			allowing(config).getInt(ConfigKeys.TOPICS_PER_PAGE); will(returnValue(10));
			one(repository).getPaginated(0, 10, PostReportStatus.RESOLVED, new int[] {});
				will(returnValue(new PaginatedResult<PostReport>(new ArrayList<PostReport>(), 10)));
			one(mockResult).include("pagination", new Pagination(0, 0, 0, "", 0));
			one(mockResult).include("reports", new ArrayList<PostReport>());
		}});

		action.listResolved(0);
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldHaveModerationRule() throws Exception {
		this.assertMethodModerationRule("list");
		this.assertMethodModerationRule("resolve", int.class);
		this.assertMethodModerationRule("delete", int.class);
		this.assertMethodModerationRule("listResolved", int.class);
	}

	private void assertMethodModerationRule(String methodName, Class<?>... argumentTypes) throws Exception {
		Method method = action.getClass().getMethod(methodName, argumentTypes);
		Assert.assertNotNull(methodName, method);
		Assert.assertTrue(methodName, method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(methodName, ModerationRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void shouldBeInterceptedByMethodSecurityInterceptor() {
		Assert.assertTrue(action.getClass().isAnnotationPresent(InterceptedBy.class));
		InterceptedBy interceptedBy = action.getClass().getAnnotation(InterceptedBy.class);
		List<Class<? extends Interceptor>> interceptors = Arrays.asList(interceptedBy.value());
		Assert.assertTrue(interceptors.contains(MethodSecurityInterceptor.class));
	}

	@Test
	public void reportNotLoggedShouldIgnore() {
		mockery.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(false));
		}});

		action.report(1, "x");
		mockery.assertIsSatisfied();
	}

	@Test
	public void reportLoggedShouldSucceed() {
		mockery.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(true));
			one(userSession).getUser(); will(returnValue(new User()));
			one(repository).add(with(any(PostReport.class)));
		}});

		action.report(1, "x");
		mockery.assertIsSatisfied();
	}

	@Test
	public void deleteNotForumModeratorShouldIgnore() {
		mockery.checking(new Expectations() {{
			int[] forumIds = new int[] {1};

			one(roleManager).getRoleValues(SecurityConstants.FORUM); will(returnValue(forumIds));

			PostReport report = new PostReport();
			report.setPost(new Post());
			report.getPost().setForum(new Forum());
			report.getPost().getForum().setId(2);

			one(repository).get(1); will(returnValue(report));

			one(mockResult).redirectTo(PostReportController.class).list();
		}});

		action.delete(1);
		mockery.assertIsSatisfied();
	}

	@Test
	public void deleteShouldSucceed() {
		mockery.checking(new Expectations() {{
			int[] forumIds = new int[] {1};

			one(roleManager).getRoleValues(SecurityConstants.FORUM); will(returnValue(forumIds));

			PostReport report = new PostReport();
			report.setPost(new Post());
			report.getPost().setForum(new Forum());
			report.getPost().getForum().setId(1);

			one(repository).get(1); will(returnValue(report));
			one(repository).remove(report);
			one(mockResult).redirectTo(PostReportController.class).list();
		}});

		action.delete(1);
		mockery.assertIsSatisfied();
	}

	@Test
	public void listNotAdministratorShouldFilterByForum() {
		mockery.checking(new Expectations() {{
			int[] forumIds = new int[] {1, 2};

			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCoAdministrator(); will(returnValue(false));
			one(roleManager).getRoleValues(SecurityConstants.FORUM); will(returnValue(forumIds));
			one(repository).getAll(PostReportStatus.UNRESOLVED, forumIds); will(returnValue(new ArrayList<PostReport>()));
			one(mockResult).include("reports", new ArrayList<PostReport>());
		}});

		action.list();
		mockery.assertIsSatisfied();
	}

	@Test
	public void listNullStatusDefaultShouldBeUnresolved() {
		mockery.checking(new Expectations() {{
			ignoring(sessionManager); ignoring(roleManager); ignoring(mockResult);
			one(repository).getAll(PostReportStatus.UNRESOLVED, new int[] {});
		}});

		action.list();
		mockery.assertIsSatisfied();
	}

	@Test
	public void listIsAdministratorShouldNotFilterByForum() {
		mockery.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(repository).getAll(PostReportStatus.UNRESOLVED, null); will(returnValue(new ArrayList<PostReport>()));
			one(mockResult).include("reports", new ArrayList<PostReport>());
		}});

		action.list();
		mockery.assertIsSatisfied();
	}

	@Test
	public void listIsCoAdministratorShouldNotFilterByForum() {
		mockery.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCoAdministrator(); will(returnValue(true));
			one(repository).getAll(PostReportStatus.UNRESOLVED, null); will(returnValue(new ArrayList<PostReport>()));
			one(mockResult).include("reports", new ArrayList<PostReport>());
		}});

		action.list();
		mockery.assertIsSatisfied();
	}

	@Before
	public void setup() {
		mockery.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
		}});
	}
}

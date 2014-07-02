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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.jforum.core.SecurityConstraint;
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
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;

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
public class PostReportControllerTestCase {
	
	@Mock private UserSession userSession;
	@Mock private RoleManager roleManager;
	@Mock private PostReportRepository repository;
	@Mock private JForumConfig config;
	@Spy private MockResult mockResult;
	@Mock private PostReportController mockPostReportController;
	@InjectMocks private PostReportController controller;

	@Test
	public void listResolved() {
		when(config.getInt(ConfigKeys.TOPICS_PER_PAGE)).thenReturn(10);
		when(repository.getPaginated(0, 10, PostReportStatus.RESOLVED, new int[] {})).thenReturn(new PaginatedResult<PostReport>(new ArrayList<PostReport>(), 10));
		
		controller.listResolved(0);
		
		assertEquals(new Pagination(0, 0, 0, "", 0), mockResult.included("pagination"));
		assertEquals(new ArrayList<PostReport>(), mockResult.included("reports"));
	}

	@Test
	public void shouldHaveModerationRule() throws Exception {
		this.assertMethodModerationRule("list");
		this.assertMethodModerationRule("resolve", int.class);
		this.assertMethodModerationRule("delete", int.class);
		this.assertMethodModerationRule("listResolved", int.class);
	}

	private void assertMethodModerationRule(String methodName, Class<?>... argumentTypes) throws Exception {
		Method method = controller.getClass().getMethod(methodName, argumentTypes);
		assertNotNull(methodName, method);
		assertTrue(methodName, method.isAnnotationPresent(SecurityConstraint.class));
		assertEquals(methodName, ModerationRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void reportNotLoggedShouldIgnore() {
		when(userSession.isLogged()).thenReturn(false);
		
		controller.report(1, "x");
		
		verifyZeroInteractions(repository);
	}

	@Test
	public void reportLoggedShouldSucceed() {
		when(userSession.isLogged()).thenReturn(true);
		when(userSession.getUser()).thenReturn(new User());
		
		controller.report(1, "x");

		verify(repository).add(any(PostReport.class));
	}

	@Test
	public void deleteNotForumModeratorShouldIgnore() {
		int[] forumIds = new int[] {1};

		when(roleManager.getRoleValues(SecurityConstants.FORUM)).thenReturn(forumIds);
		PostReport report = new PostReport();
		report.setPost(new Post());
		report.getPost().setForum(new Forum());
		report.getPost().getForum().setId(2);
		when(repository.get(1)).thenReturn(report);
		when(mockResult.redirectTo(controller)).thenReturn(mockPostReportController);
		
		controller.delete(1);
		
		verify(mockPostReportController).list();
	}

	@Test
	public void deleteShouldSucceed() {
		int[] forumIds = new int[] {1};

		when(roleManager.getRoleValues(SecurityConstants.FORUM)).thenReturn(forumIds);
		PostReport report = new PostReport();
		report.setPost(new Post());
		report.getPost().setForum(new Forum());
		report.getPost().getForum().setId(1);
		when(repository.get(1)).thenReturn(report);
		when(mockResult.redirectTo(controller)).thenReturn(mockPostReportController);
	
		controller.delete(1);
		
		verify(repository).remove(report);
		verify(mockPostReportController).list();
	}

	@Test
	public void listNotAdministratorShouldFilterByForum() {
		int[] forumIds = new int[] {1, 2};

		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isCoAdministrator()).thenReturn(false);
		when(roleManager.getRoleValues(SecurityConstants.FORUM)).thenReturn(forumIds);
		when(repository.getAll(PostReportStatus.UNRESOLVED, forumIds)).thenReturn(new ArrayList<PostReport>());
		
		controller.list();
		
		assertEquals(new ArrayList<PostReport>(), mockResult.included("reports"));
	}

	@Test
	public void listNullStatusDefaultShouldBeUnresolved() {
		controller.list();
		
		verify(repository).getAll(PostReportStatus.UNRESOLVED, new int[] {});
	}

	@Test
	public void listIsAdministratorShouldNotFilterByForum() {
		when(roleManager.isAdministrator()).thenReturn(true);
		when(repository.getAll(PostReportStatus.UNRESOLVED, null)).thenReturn(new ArrayList<PostReport>());
		
		controller.list();
		
		assertEquals(new ArrayList<PostReport>(), mockResult.included("reports"));
	}

	@Test
	public void listIsCoAdministratorShouldNotFilterByForum() {
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isCoAdministrator()).thenReturn(true);
		when(repository.getAll(PostReportStatus.UNRESOLVED, null)).thenReturn(new ArrayList<PostReport>());
		
		controller.list();
		
		assertEquals(new ArrayList<PostReport>(), mockResult.included("reports"));
	}

	@Before
	public void setup() {
		when(roleManager.getRoleValues(SecurityConstants.FORUM)).thenReturn(new int[] {});
		when(userSession.getRoleManager()).thenReturn(roleManager);
	}
}

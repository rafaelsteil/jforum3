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

import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.MostUsersEverOnline;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.AccessForumRule;
import net.jforum.security.RoleManager;
import net.jforum.services.MostUsersEverOnlineService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.GroupInteractionFilter;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.Interceptor;
import org.vraptor.annotations.InterceptedBy;

/**
 * @author Rafael Steil
 */
public class ForumActionsTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private CategoryRepository categoryRepository = context.mock(CategoryRepository.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private UserRepository userRepository = context.mock(UserRepository.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private MostUsersEverOnlineService mostUsersEverOnlineService = context.mock(MostUsersEverOnlineService.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private GroupInteractionFilter groupInteractionFilter = context.mock(GroupInteractionFilter.class);
	private ForumActions forumAction;

	@Test
	public void shouldBeInterceptedByMethodSecurityInterceptor() throws Exception {
		Assert.assertTrue(forumAction.getClass().isAnnotationPresent(InterceptedBy.class));
		InterceptedBy annotation = forumAction.getClass().getAnnotation(InterceptedBy.class);
		List<Class<? extends Interceptor>> interceptors = Arrays.asList(annotation.value());
		Assert.assertTrue(interceptors.contains(MethodSecurityInterceptor.class));
	}

	@Test
	public void showShouldHaveAccessForumConstraint() throws Exception {
		Method method = forumAction.getClass().getMethod("show", int.class, int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(AccessForumRule.class, method.getAnnotation(SecurityConstraint.class).value());
		Assert.assertTrue(method.getAnnotation(SecurityConstraint.class).displayLogin());
	}

	@Test
	public void show() {
		context.checking(new Expectations() {{
			Forum forum = new Forum(forumRepository);
			one(forumRepository).getTotalTopics(forum); will(returnValue(1));
			one(forumRepository).getTopics(forum, 0, 10);
			one(forumRepository).get(1); will(returnValue(forum));
			one(config).getInt(ConfigKeys.TOPICS_PER_PAGE); will(returnValue(10));
			one(categoryRepository).getAllCategories(); will(returnValue(new ArrayList<Category>()));
			one(sessionManager).isModeratorOnline(); will(returnValue(true));
			one(propertyBag).put("topics", new ArrayList<Topic>());
			one(propertyBag).put("forum", forum);
			one(propertyBag).put("categories", new ArrayList<Category>());
			one(propertyBag).put("pagination", new Pagination(0, 0, 0, "", 0));
			one(propertyBag).put("isModeratorOnline", true);
		}});

		forumAction.show(1, 0);
		context.assertIsSatisfied();
	}

	@Test
	public void listCannotInteractWitOtherGroups() {
		context.checking(new Expectations() {{
			ignoring(propertyBag); ignoring(categoryRepository); ignoring(config);
			ignoring(userRepository); ignoring(forumRepository); ignoring(mostUsersEverOnlineService);

			RoleManager roleManager = context.mock(RoleManager.class);
			UserSession userSession = context.mock(UserSession.class);
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));

			ignoring(sessionManager);

			one(userSession).isLogged(); will(returnValue(true));
			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(false));
			one(groupInteractionFilter).filterForumListing(propertyBag, userSession);
		}});

		forumAction.list();
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		context.checking(new Expectations() {{
			MostUsersEverOnline most = new MostUsersEverOnline();

			one(categoryRepository).getAllCategories(); will(returnValue(new ArrayList<Category>()));
			one(sessionManager).getLoggedSessions(); will(returnValue(new ArrayList<UserSession>()));
			one(userRepository).getTotalUsers(); will(returnValue(1));
			one(forumRepository).getTotalMessages(); will(returnValue(2));
			one(sessionManager).getTotalLoggedUsers(); will(returnValue(3));
			one(sessionManager).getTotalAnonymousUsers(); will(returnValue(4));
			one(userRepository).getLastRegisteredUser(); will(returnValue(new User()));
			one(mostUsersEverOnlineService).getMostRecentData(with(any(int.class))); will(returnValue(most));
			one(sessionManager).getTotalUsers(); will(returnValue(3));
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(7));

			one(propertyBag).put("categories", new ArrayList<Category>());
			one(propertyBag).put("onlineUsers", new ArrayList<UserSession>());
			one(propertyBag).put("totalRegisteredUsers", 1);
			one(propertyBag).put("totalMessages", 2);
			one(propertyBag).put("totalLoggedUsers", 3);
			one(propertyBag).put("totalAnonymousUsers", 4);
			one(propertyBag).put("lastRegisteredUser", new User());
			one(propertyBag).put("mostUsersEverOnline", most);
			one(propertyBag).put("postsPerPage", 7);

			UserSession userSession = context.mock(UserSession.class);
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).isLogged(); will(returnValue(false));
		}});

		forumAction.list();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		forumAction = new ForumActions(propertyBag, categoryRepository, sessionManager,
			forumRepository, userRepository, mostUsersEverOnlineService, config, groupInteractionFilter);
	}
}

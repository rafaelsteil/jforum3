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


import java.util.Arrays;

import net.jforum.actions.helpers.Actions;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.ForumService;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ForumAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ForumAdminActions component;
	private CategoryRepository categoryRepository = context.mock(CategoryRepository.class);
	private ForumService service = context.mock(ForumService.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);

	public ForumAdminActionsTestCase() {
		super(ForumAdminActions.class);
	}

	@Test
	public void deleteIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(service).delete(1, 2);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		final Category c = new Category(categoryRepository);

		context.checking(new Expectations() {{
			one(categoryRepository).getAllCategories(); will(returnValue(Arrays.asList(c)));
			one(propertyBag).put("categories", Arrays.asList(c));
		}});

		component.list();
		context.assertIsSatisfied();
	}

	@Test
	public void addExpectCategories() {
		context.checking(new Expectations() {{
			one(categoryRepository).getAllCategories(); will(returnValue(Arrays.asList(new Category())));
			one(propertyBag).put("categories", Arrays.asList(new Category()));
		}});

		component.add();
		context.assertIsSatisfied();
	}

	@Test
	public void editExpectForumAndCategories() {
		context.checking(new Expectations() {{
			one(forumRepository).get(3); will(returnValue(new Forum()));
			one(categoryRepository).getAllCategories(); will(returnValue(Arrays.asList(new Category())));
			one(propertyBag).put("forum", new Forum());
			one(propertyBag).put("categories", Arrays.asList(new Category()));
			one(viewService).renderView(Actions.ADD);
		}});

		component.edit(3);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsSuperAdministratorExpectsSuccess() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(service).update(with(aNonNull(Forum.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.editSave(new Forum());
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsCategoryAllowedExpectsSuccess() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCategoryAllowed(with(any(Category.class))); will(returnValue(true));
			one(service).update(with(aNonNull(Forum.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		Forum forum = new Forum();
		forum.setCategory(new Category());
		forum.getCategory().setId(1);

		component.editSave(forum);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveNotSuperAdministratorCategoryNotAllowedShouldIgnore() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCategoryAllowed(with(any(Category.class))); will(returnValue(false));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		Forum forum = new Forum();
		forum.setCategory(new Category());
		forum.getCategory().setId(1);

		component.editSave(forum);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsSuperAdministratorExpectSuccess() {
		final Forum f = new Forum();
		f.setName("f1");

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(propertyBag).put("forum", f);
			one(service).add(f);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.addSave(f);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsCategoryAllowedExpectSuccess() {
		final Forum f = new Forum();
		f.setName("f1");
		f.setCategory(new Category());
		f.getCategory().setId(1);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCategoryAllowed(with(any(Category.class))); will(returnValue(true));
			one(propertyBag).put("forum", f);
			one(service).add(f);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.addSave(f);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveNotSuperAdministratorCategoryNotAllowedShouldIgnore() {
		final Forum f = new Forum();
		f.setName("f1");
		f.setCategory(new Category());
		f.getCategory().setId(1);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCategoryAllowed(with(any(Category.class))); will(returnValue(false));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.addSave(f);
		context.assertIsSatisfied();
	}

	@Test
	public void up() {
		context.checking(new Expectations() {{
			one(service).upForumOrder(1);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.up(1);
		context.assertIsSatisfied();
	}

	@Test
	public void down() {
		context.checking(new Expectations() {{
			one(service).downForumOrder(2);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.down(2);
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		component = new ForumAdminActions(service, forumRepository,
			categoryRepository, propertyBag, viewService, sessionManager);
	}
}

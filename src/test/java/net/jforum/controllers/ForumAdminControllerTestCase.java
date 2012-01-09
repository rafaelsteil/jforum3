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

import java.util.Arrays;

import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.ForumService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class ForumAdminControllerTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ForumAdminController controller;
	private CategoryRepository categoryRepository = context.mock(CategoryRepository.class);
	private ForumService service = context.mock(ForumService.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private ForumAdminController mockForumAdminController = context.mock(ForumAdminController.class);
	private Result mockResult = context.mock(MockResult.class);

	public ForumAdminControllerTestCase() {
		super(ForumAdminController.class);
	}

	@Test
	public void deleteIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(service).delete(1, 2);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		final Category category = new Category(categoryRepository);

		context.checking(new Expectations() {
			{
				one(categoryRepository).getAllCategories();
				will(returnValue(Arrays.asList(category)));
				one(mockResult).include("categories", Arrays.asList(category));
			}
		});

		controller.list();
		context.assertIsSatisfied();
	}

	@Test
	public void addExpectCategories() {
		context.checking(new Expectations() {
			{
				one(categoryRepository).getAllCategories();
				will(returnValue(Arrays.asList(new Category())));
				one(mockResult).include("categories",
						Arrays.asList(new Category()));
			}
		});

		controller.add();
		context.assertIsSatisfied();
	}

	@Test
	public void editExpectForumAndCategories() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).getCanModerateForum(3);
				will(returnValue(true));
				one(forumRepository).get(3);
				will(returnValue(new Forum()));
				one(categoryRepository).getAllCategories();
				will(returnValue(Arrays.asList(new Category())));
				one(mockResult).include("forum", new Forum());
				one(mockResult).include("categories",
						Arrays.asList(new Category()));
				one(mockResult).forwardTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).add();
			}
		});

		controller.edit(3);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsSuperAdministratorExpectsSuccess() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(service).update(with(aNonNull(Forum.class)));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.editSave(new Forum());
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsCategoryAllowedExpectsSuccess() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(roleManager).getCanModerateForum(0);
				will(returnValue(true));
				one(service).update(with(aNonNull(Forum.class)));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		Forum forum = new Forum();
		forum.setCategory(new Category());
		forum.getCategory().setId(1);

		controller.editSave(forum);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveNotSuperAdministratorCategoryNotAllowedShouldIgnore() {
		final Forum forum = new Forum();
		forum.setCategory(new Category());
		forum.getCategory().setId(1);

		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(roleManager).getCanModerateForum(0);
				will(returnValue(true));
				one(service).update(forum);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.editSave(forum);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsSuperAdministratorExpectSuccess() {
		final Forum f = new Forum();
		f.setName("f1");

		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(mockResult).include("forum", f);
				one(service).add(f);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.addSave(f);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsCategoryAllowedExpectSuccess() {
		final Forum f = new Forum();
		f.setName("f1");
		f.setCategory(new Category());
		f.getCategory().setId(1);

		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(roleManager).isCategoryAllowed(1);
				will(returnValue(true));
				one(mockResult).include("forum", f);
				one(service).add(f);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.addSave(f);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveNotSuperAdministratorCategoryNotAllowedShouldIgnore() {
		final Forum forum = new Forum();
		forum.setName("f1");
		forum.setCategory(new Category());
		forum.getCategory().setId(1);

		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(roleManager).isCategoryAllowed(1);
				will(returnValue(true));
				one(service).add(forum);
				one(mockResult).include("forum", forum);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.addSave(forum);
		context.assertIsSatisfied();
	}

	@Test
	public void up() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).getCanModerateForum(1);
				will(returnValue(true));
				one(service).upForumOrder(1);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.up(1);
		context.assertIsSatisfied();
	}

	@Test
	public void down() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).getCanModerateForum(2);
				will(returnValue(true));
				one(service).downForumOrder(2);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockForumAdminController));
				one(mockForumAdminController).list();
			}
		});

		controller.down(2);
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		controller = new ForumAdminController(service, forumRepository,
				categoryRepository, mockResult, userSession);
	}
}

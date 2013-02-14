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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

//import net.jforum.actions.helpers.PermissionOptions;
import net.jforum.entities.Category;
import net.jforum.entities.Group;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.GroupRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.GroupService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.HttpRequestHandler;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class GroupAdminControllerTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private GroupAdminController controller;
	private GroupRepository repository = context.mock(GroupRepository.class);
	private GroupService service = context.mock(GroupService.class);
	private CategoryRepository categoryRepository = context.mock(CategoryRepository.class);
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private Result mockResult = context.mock(MockResult.class);
	private HttpServletRequest mockRequest = context.mock(HttpServletRequest.class);
	private GroupAdminController mockGroupAdminController = context.mock(GroupAdminController.class);

	public GroupAdminControllerTestCase() {
		super(GroupAdminController.class);
	}

	@Test
	public void permissions() {
		context.checking(new Expectations() {
			{
				one(repository).get(1);
				will(returnValue(new Group()));
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(categoryRepository).getAllCategories();
				will(returnValue(new ArrayList<Category>()));
				one(repository).getAllGroups();
				will(returnValue(new ArrayList<Group>()));

				one(mockResult).include("group", new Group());
				one(mockResult).include("groups", new ArrayList<Group>());
				one(mockResult).include("categories", new ArrayList<Category>());
				//TODO: fix PermOption				one(mockResult).include("permissions", new PermissionOptions());
			}
		});

		controller.permissions(1);
		context.assertIsSatisfied();
	}

	@Test
	public void permissionsSave() {
		//TODO: fix PermOption		final PermissionOptions permissions = new PermissionOptions();

		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));
				//TODO: fix PermOption		one(service).savePermissions(1, permissions);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		//TODO: fix PermOption	controller.permissionsSave(1, permissions);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(service).delete(1, 2);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		controller.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		controller.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		context.checking(new Expectations() {
			{
				one(repository).getAllGroups();
				will(returnValue(new ArrayList<Group>()));
				one(mockResult).include("groups", new ArrayList<Group>());
			}
		});

		controller.list();
		context.assertIsSatisfied();
	}

	@Test
	public void editExpectsAGroup() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(repository).get(2);
				will(returnValue(new Group()));
				one(mockResult).include("group", new Group());
				one(mockResult).forwardTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).add();
			}
		});

		controller.edit(2);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsFullAdministratorExpectsSuccess() {
		final Group group = new Group();

		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));

				one(service).update(group);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		controller.editSave(group);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsGroupManagerExpectsSuccess() {
		final Group group = new Group();

		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(roleManager).isGroupManager(group.getId());
				will(returnValue(true));

				one(service).update(group);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		controller.editSave(group);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsNotFullAdministratorAndNotGroupManagerShouldIgnore() {
		final Group group = new Group();

		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(roleManager).isGroupManager(group.getId());
				will(returnValue(false));

				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		controller.editSave(group);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(service).add(with(aNonNull(Group.class)));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		controller.addSave(new Group());
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		controller.addSave(new Group());
		context.assertIsSatisfied();
	}

	@Test
	public void addIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockGroupAdminController));
				one(mockGroupAdminController).list();
			}
		});

		controller.add();
		context.assertIsSatisfied();
	}

	@Test
	public void addIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));
			}
		});

		controller.add();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		controller = new GroupAdminController(service, repository, categoryRepository, mockResult, userSession, mockRequest);

		context.checking(new Expectations() {
			{
				allowing(userSession).getRoleManager();
				will(returnValue(roleManager));
			}
		});
	}
}

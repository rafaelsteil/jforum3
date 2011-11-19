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

import java.util.ArrayList;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.PermissionOptions;
import net.jforum.controllers.GroupAdminController;
import net.jforum.core.SessionManager;
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

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class GroupAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private GroupAdminController component;
	private GroupRepository repository = context.mock(GroupRepository.class);
	private GroupService service = context.mock(GroupService.class);
	private CategoryRepository categoryRepository = context
			.mock(CategoryRepository.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private MockResult mockResult = new MockResult();

	public GroupAdminActionsTestCase() {
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
				one(mockResult)
						.include("categories", new ArrayList<Category>());
				one(mockResult).include("permissions", new PermissionOptions());
			}
		});

		component.permissions(1);
		context.assertIsSatisfied();
	}

	@Test
	public void permissionsSave() {
		final PermissionOptions permissions = new PermissionOptions();

		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(service).savePermissions(1, permissions);
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.permissionsSave(1, permissions);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(service).delete(1, 2);
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.delete(1, 2);
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

		component.list();
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
				one(mockResult).forwardTo(Actions.ADD);
			}
		});

		component.edit(2);
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
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.editSave(group);
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
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.editSave(group);
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

				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.editSave(group);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(true));
				one(service).add(with(aNonNull(Group.class)));
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.addSave(new Group());
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.addSave(new Group());
		context.assertIsSatisfied();
	}

	@Test
	public void addIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {
			{
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		component.add();
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

		component.add();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		component = new GroupAdminController(service, repository, sessionManager,
				categoryRepository, mockResult);

		context.checking(new Expectations() {
			{
				allowing(sessionManager).getUserSession();
				will(returnValue(userSession));
				allowing(userSession).getRoleManager();
				will(returnValue(roleManager));
			}
		});
	}
}

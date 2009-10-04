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
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Category;
import net.jforum.entities.Group;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.GroupRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.GroupService;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class GroupAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private GroupAdminActions component;
	private GroupRepository repository = context.mock(GroupRepository.class);
	private GroupService service = context.mock(GroupService.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private CategoryRepository categoryRepository = context.mock(CategoryRepository.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);

	public GroupAdminActionsTestCase() {
		super(GroupAdminActions.class);
	}

	@Test
	public void permissions() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Group()));
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(categoryRepository).getAllCategories(); will(returnValue(new ArrayList<Category>()));
			one(repository).getAllGroups(); will(returnValue(new ArrayList<Group>()));

			one(propertyBag).put("group", new Group());
			one(propertyBag).put("groups", new ArrayList<Group>());
			one(propertyBag).put("categories", new ArrayList<Category>());
			one(propertyBag).put("permissions", new PermissionOptions());
		}});

		component.permissions(1);
		context.assertIsSatisfied();
	}

	@Test
	public void permissionsSave() {
		final PermissionOptions permissions = new PermissionOptions();

		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(service).savePermissions(1, permissions);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.permissionsSave(1, permissions);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {{
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
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		context.checking(new Expectations() {{
			one(repository).getAllGroups(); will(returnValue(new ArrayList<Group>()));
			one(propertyBag).put("groups", new ArrayList<Group>());
		}});

		component.list();
		context.assertIsSatisfied();
	}

	@Test
	public void editExpectsAGroup() {
		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(repository).get(2); will(returnValue(new Group()));
			one(propertyBag).put("group", new Group());
			one(viewService).renderView(Actions.ADD);
		}});

		component.edit(2);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsFullAdministratorExpectsSuccess() {
		final Group group = new Group();

		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(true));

			one(service).update(group);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.editSave(group);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsGroupManagerExpectsSuccess() {
		final Group group = new Group();

		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isGroupManager(group.getId()); will(returnValue(true));

			one(service).update(group);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.editSave(group);
		context.assertIsSatisfied();
	}

	@Test
	public void editSaveIsNotFullAdministratorAndNotGroupManagerShouldIgnore() {
		final Group group = new Group();

		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isGroupManager(group.getId()); will(returnValue(false));

			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.editSave(group);
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(service).add(with(aNonNull(Group.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.addSave(new Group());
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.addSave(new Group());
		context.assertIsSatisfied();
	}

	@Test
	public void addIsNotFullAdministratorShouldIgnore() {
		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.add();
		context.assertIsSatisfied();
	}

	@Test
	public void addIsFullAdministratorShouldAllow() {
		context.checking(new Expectations() {{
			one(roleManager).isAdministrator(); will(returnValue(true));
		}});

		component.add();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		component = new GroupAdminActions(service, repository, sessionManager, propertyBag, viewService, categoryRepository);

		context.checking(new Expectations() {{
			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
		}});
	}
}

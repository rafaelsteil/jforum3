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

import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.UserService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
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
public class UserAdminControllerTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserRepository repository = context.mock(UserRepository.class);
	private UserAdminController controller;
	private GroupRepository groupRepository = context.mock(GroupRepository.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private UserService userService = context.mock(UserService.class);
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private Result mockResult = context.mock(MockResult.class);
	private UserAdminController mockUserAdminController = context.mock(UserAdminController.class);
	private UserController mockUserController = context.mock(UserController.class);

	public UserAdminControllerTestCase() {
		super(UserAdminController.class);
	}

	@Test
	public void groupsSaveIsSuperAdministratorShouldAccept() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(true));

				one(userService).saveGroups(1, 1, 2);

				one(mockResult).forwardTo(controller);
				will(returnValue(mockUserAdminController));

				// TODO pass zero?
				one(mockUserAdminController).list(0);
			}
		});

		controller.groupsSave(1, 1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void groupsNotSuperAdministratorIsGroupManagerShouldAccept() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(roleManager).isGroupManager(1);
				will(returnValue(true));
				one(roleManager).isGroupManager(2);
				will(returnValue(true));

				one(userService).saveGroups(1, 1, 2);

				one(mockResult).forwardTo(controller);
				will(returnValue(mockUserAdminController));

				// TODO pass zero?
				one(mockUserAdminController).list(0);
			}
		});

		controller.groupsSave(1, 1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void groupsSaveNotSuperAdminNotGroupManagerShouldIgnore() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(roleManager).isAdministrator();
				will(returnValue(false));
				one(roleManager).isGroupManager(1);
				will(returnValue(false));

				one(mockResult).forwardTo(controller);
				will(returnValue(mockUserAdminController));

				// TODO pass zero?
				one(mockUserAdminController).list(0);
			}
		});

		controller.groupsSave(1, 1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void groups() {
		context.checking(new Expectations() {
			{
				User user = new User();
				user.setId(1);
				one(repository).get(1);
				will(returnValue(user));
				one(mockResult).include("user", user);
				one(groupRepository).getAllGroups();
				will(returnValue(new ArrayList<Group>()));
				one(mockResult).include("groups", new ArrayList<Group>());
			}
		});

		controller.groups(1);
		context.assertIsSatisfied();
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {
			{
				one(mockResult).forwardTo(UserController.class);
				will(returnValue(mockUserController));
				one(mockUserController).edit(1);
			}
		});

		controller.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		context.checking(new Expectations() {
			{
				one(repository).getTotalUsers();
				will(returnValue(100));
				one(config).getInt(ConfigKeys.USERS_PER_PAGE);
				will(returnValue(10));
				one(repository).getAllUsers(0, 10);
				will(returnValue(new ArrayList<User>()));
				one(mockResult).include("users", new ArrayList<User>());
				one(mockResult).include("pagination",
						new Pagination(0, 0, 0, "", 0));
			}
		});

		controller.list(0);
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		controller = new UserAdminController(repository, groupRepository, config,
				userService, mockResult, userSession);
	}
}

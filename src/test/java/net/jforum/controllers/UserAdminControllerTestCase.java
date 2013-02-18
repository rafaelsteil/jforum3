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

import java.util.ArrayList;

import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.UserService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

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
public class UserAdminControllerTestCase extends AdminTestCase {	
	@Mock private UserRepository userRepository;
	@Mock private GroupRepository groupRepository;
	@Mock private JForumConfig config;
	@Mock private UserService userService;
	@Spy private MockResult mockResult;
	@Mock private UserSession userSession;
	
	@Mock private RoleManager roleManager;
	@Mock private UserAdminController mockUserAdminControllerRedirect;
	@Mock private UserController mockUserController;

	@InjectMocks private UserAdminController controller;
	
	public UserAdminControllerTestCase() {
		super(UserAdminController.class);
	}

	@Before
	public void setup() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(mockResult.redirectTo(controller)).thenReturn(mockUserAdminControllerRedirect);
	}
	
	@Test
	public void groupsSaveIsSuperAdministratorShouldAccept() {
		when(roleManager.isAdministrator()).thenReturn(true);
		
		controller.groupsSave(1, 1, 2);
		
		verify(userService).saveGroups(1, 1, 2);
		verify(mockUserAdminControllerRedirect).list(0);
	}

	@Test
	public void groupsNotSuperAdministratorIsGroupManagerShouldAccept() {
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isGroupManager(1)).thenReturn(true);
		when(roleManager.isGroupManager(2)).thenReturn(true);
		
		controller.groupsSave(1, 1, 2);
		
		verify(userService).saveGroups(1, 1, 2);
		verify(mockUserAdminControllerRedirect).list(0);
	}

	@Test
	public void groupsSaveNotSuperAdminNotGroupManagerShouldIgnore() {
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isGroupManager(1)).thenReturn(false);
		
		controller.groupsSave(1, 1, 2);
		
		verify(userService,never()).saveGroups(anyInt(),(int[])anyVararg());
		verify(mockUserAdminControllerRedirect).list(0);
	}

	@Test
	public void groups() {
		User user = new User();
		user.setId(1);
		ArrayList<Group> groups = new ArrayList<Group>();
		
		when(userRepository.get(1)).thenReturn(user);
		when(groupRepository.getAllGroups()).thenReturn(groups);
		
		controller.groups(1);
		
		assertEquals(user, mockResult.included("user"));
		assertEquals(groups, mockResult.included("groups"));
	}

	@Test
	public void edit() {
		when(mockResult.forwardTo(UserController.class)).thenReturn(mockUserController);
		
		controller.edit(1);

		verify(mockUserController).edit(1);
	}

	@Test
	public void list() {
		ArrayList<User> users = new ArrayList<User>();
		
		when(userRepository.getTotalUsers()).thenReturn(100);
		when(config.getInt(ConfigKeys.USERS_PER_PAGE)).thenReturn(10);
		when(userRepository.getAllUsers(0, 10)).thenReturn(users);
		
		controller.list(0);
		
		assertEquals(users, mockResult.included("users"));
		assertNotNull(mockResult.included("users"));
	}
}

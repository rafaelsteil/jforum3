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
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import net.jforum.entities.Category;
import net.jforum.entities.Group;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.GroupRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.GroupService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;
//import net.jforum.actions.helpers.PermissionOptions;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupAdminControllerTestCase extends AdminTestCase {
	@Mock private GroupService service;
	@Mock private GroupRepository repository;
	@Mock private CategoryRepository categoryRepository;
	@Spy private MockResult mockResult;
	@Mock private UserSession userSession;
	@Mock private HttpServletRequest mockRequest;

	@Mock private RoleManager roleManager;
	@Mock private GroupAdminController mockGroupAdminControllerForward;
	@Mock private GroupAdminController mockGroupAdminControllerRedirect;

	@InjectMocks private GroupAdminController controller;
	
	private Group group = new Group();

	public GroupAdminControllerTestCase() {
		super(GroupAdminController.class);
	}

	@Before
	public void setup() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(mockResult.redirectTo(controller)).thenReturn(mockGroupAdminControllerRedirect);
		when(mockResult.forwardTo(controller)).thenReturn(mockGroupAdminControllerForward);
	}

	@Test
	public void permissions() {
		when(repository.get(1)).thenReturn(new Group());
		when(roleManager.isAdministrator()).thenReturn(true);
		when(categoryRepository.getAllCategories()).thenReturn(new ArrayList<Category>());
		when(repository.getAllGroups()).thenReturn(new ArrayList<Group>());

		controller.permissions(1);

		verify(mockResult).include("group", new Group());
		verify(mockResult).include("groups", new ArrayList<Group>());
		verify(mockResult).include("categories", new ArrayList<Category>());
	}

	@Test
	public void permissionsSave() {
		when(roleManager.isAdministrator()).thenReturn(true);
		when(mockRequest.getParameterNames()).thenReturn(Collections.enumeration(Collections.<String>emptyList()));
		
		controller.permissionsSave(1);

		verify(service).savePermissions(eq(1), anyMap());
		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void deleteIsFullAdministratorShouldAllow() {
		when(roleManager.isAdministrator()).thenReturn(true);

		controller.delete(1, 2);

		verify(service).delete(1, 2);
		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void deleteIsNotFullAdministratorShouldIgnore() {
		when(roleManager.isAdministrator()).thenReturn(false);

		controller.delete(1, 2);

		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void list() {
		ArrayList<Group> groups = new ArrayList<Group>();

		when(repository.getAllGroups()).thenReturn(groups);

		controller.list();

		assertEquals(groups, mockResult.included("groups"));
	}

	@Test
	public void editExpectsAGroup() {
		when(roleManager.isAdministrator()).thenReturn(true);
		when(repository.get(2)).thenReturn(group);

		controller.edit(2);

		verify(mockResult).include("group", group);
		verify(mockGroupAdminControllerForward).add();
	}

	@Test
	public void editSaveIsFullAdministratorExpectsSuccess() {
		when(roleManager.isAdministrator()).thenReturn(true);

		controller.editSave(group);

		verify(service).update(group);
		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void editSaveIsGroupManagerExpectsSuccess() {
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isGroupManager(group.getId())).thenReturn(true);

		controller.editSave(group);

		verify(service).update(group);
		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void editSaveIsNotFullAdministratorAndNotGroupManagerShouldIgnore() {
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isGroupManager(group.getId())).thenReturn(false);

		controller.editSave(group);

		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void addSaveIsFullAdministratorShouldAllow() {
		when(roleManager.isAdministrator()).thenReturn(true);

		controller.addSave(new Group());

		verify(service).add(notNull(Group.class));
		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void addSaveIsNotFullAdministratorShouldIgnore() {
		when(roleManager.isAdministrator()).thenReturn(false);

		controller.addSave(new Group());

		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void addIsNotFullAdministratorShouldIgnore() {
		when(roleManager.isAdministrator()).thenReturn(false);

		controller.add();

		verify(mockGroupAdminControllerRedirect).list();
	}

	@Test
	public void addIsFullAdministratorShouldAllow() {
		when(roleManager.isAdministrator()).thenReturn(true);

		controller.add();

		verify(mockGroupAdminControllerRedirect,never()).list();
	}
}

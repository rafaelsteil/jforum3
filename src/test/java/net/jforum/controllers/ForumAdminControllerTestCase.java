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

import java.util.Arrays;

import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.ForumService;

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
public class ForumAdminControllerTestCase extends AdminTestCase {
	
	@InjectMocks private ForumAdminController controller;
	@Mock private CategoryRepository categoryRepository;
	@Mock private ForumService service;
	@Mock private ForumRepository forumRepository;
	@Mock private UserSession userSession;
	@Mock private RoleManager roleManager;
	@Mock private ForumAdminController mockForumAdminController;
	@Spy private MockResult mockResult;

	public ForumAdminControllerTestCase() {
		super(ForumAdminController.class);
	}

	@Test
	public void deleteIsFullAdministratorShouldAllow() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			
		controller.delete(1, 2);
		
		verify(service).delete(1, 2);
		verify(mockForumAdminController).list();
	}

	@Test
	public void deleteIsNotFullAdministratorShouldIgnore() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(false);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			
		controller.delete(1, 2);
		
		verify(mockForumAdminController).list();
	}

	@Test
	public void list() {
		final Category category = new Category(categoryRepository);

		when(categoryRepository.getAllCategories()).thenReturn(Arrays.asList(category));
			
		controller.list();
		
		assertEquals(Arrays.asList(category), mockResult.included("categories"));
	}

	@Test
	public void addExpectCategories() {
		when(categoryRepository.getAllCategories()).thenReturn(Arrays.asList(new Category()));
			
		controller.add();
		
		assertEquals(Arrays.asList(new Category()), mockResult.included("categories"));
	}

	@Test
	public void editExpectForumAndCategories() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.getCanModerateForum(3)).thenReturn(true);
		when(forumRepository.get(3)).thenReturn(new Forum());
		when(categoryRepository.getAllCategories()).thenReturn(Arrays.asList(new Category()));
		when(mockResult.forwardTo(controller)).thenReturn(mockForumAdminController);
			
		controller.edit(3);
		
		assertEquals(new Forum(), mockResult.included("forum"));
		assertEquals(Arrays.asList(new Category()), mockResult.included("categories"));
		verify(mockForumAdminController).add();
	}

	@Test
	public void editSaveIsSuperAdministratorExpectsSuccess() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			
		controller.editSave(new Forum());
		
		verify(service).update(notNull(Forum.class));
		verify(mockForumAdminController).list();
	}

	@Test
	public void editSaveIsCategoryAllowedExpectsSuccess() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.getCanModerateForum(0)).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
	
		Forum forum = new Forum();
		forum.setCategory(new Category());
		forum.getCategory().setId(1);

		controller.editSave(forum);
		
		verify(service).update(notNull(Forum.class));
		verify(mockForumAdminController).list();
	}

	@Test
	public void editSaveNotSuperAdministratorCategoryNotAllowedShouldIgnore() {
		final Forum forum = new Forum();
		forum.setCategory(new Category());
		forum.getCategory().setId(1);
		
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.getCanModerateForum(0)).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			
		controller.editSave(forum);
		
		verify(service).update(forum);
		verify(mockForumAdminController).list();
	}

	@Test
	public void addSaveIsSuperAdministratorExpectSuccess() {
		final Forum f = new Forum();
		f.setName("f1");
		
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			
		controller.addSave(f);
		
		assertEquals(f, mockResult.included("forum"));
		verify(service).add(f);
		verify(mockForumAdminController).list();
	}

	@Test
	public void addSaveIsCategoryAllowedExpectSuccess() {
		final Forum f = new Forum();
		f.setName("f1");
		f.setCategory(new Category());
		f.getCategory().setId(1);
		
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isCategoryAllowed(1)).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			
		controller.addSave(f);
		
		assertEquals(f, mockResult.included("forum"));
		verify(service).add(f);
		verify(mockForumAdminController).list();
	}

	@Test
	public void addSaveNotSuperAdministratorCategoryNotAllowedShouldIgnore() {
		final Forum forum = new Forum();
		forum.setName("f1");
		forum.setCategory(new Category());
		forum.getCategory().setId(1);
		
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(false);
		when(roleManager.isCategoryAllowed(1)).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			
		controller.addSave(forum);
		
		verify(service).add(forum);
		assertEquals(forum, mockResult.included("forum"));
		verify(mockForumAdminController).list();
	}

	@Test
	public void up() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.getCanModerateForum(1)).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			
		controller.up(1);
		
		verify(service).upForumOrder(1);
		verify(mockForumAdminController).list();
	}

	@Test
	public void down() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.getCanModerateForum(2)).thenReturn(true);
		when(mockResult.redirectTo(controller)).thenReturn(mockForumAdminController);
			

		controller.down(2);
		
		verify(service).downForumOrder(2);
		verify(mockForumAdminController).list();
	}
}

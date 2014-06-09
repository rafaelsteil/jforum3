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
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import net.jforum.entities.Category;
import net.jforum.repository.CategoryRepository;
import net.jforum.services.CategoryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryAdminControllerTestCase extends AdminTestCase {

	
	private CategoryAdminController action;
	@Mock private CategoryRepository repository;
	@Mock private CategoryService service;
	@Mock private CategoryAdminController mockCategoryAdminController;
	@Spy private MockResult mockResult;

	public CategoryAdminControllerTestCase() {
		super(CategoryAdminController.class);
	}

	@Test
	public void delete() {
		when(mockResult.redirectTo(action)).thenReturn(mockCategoryAdminController);
			
		action.delete(1, 2);
		
		verify(mockCategoryAdminController).list();
		verify(service).delete(1, 2);
	}

	@Test
	public void addSave() {
		final Category c = new Category();

		c.setName("c1");
		c.setModerated(false);
		c.setDisplayOrder(1);
		
		when(mockResult.redirectTo(action)).thenReturn(mockCategoryAdminController);
			
		action.addSave(c);
		
		verify(mockCategoryAdminController).list();
		verify(service).add(c);
	}

	@Test
	public void editExpectACategory() {
		when(repository.get(5)).thenReturn(new Category());
		when(mockResult.forwardTo(action)).thenReturn(mockCategoryAdminController);
		
		action.edit(5);
		
		assertEquals(new Category(), mockResult.included("category"));
		verify(mockCategoryAdminController).add();
	}

	@Test
	public void editSave() {
		final Category c = new Category();
		c.setId(2);
		
		when(mockResult.redirectTo(action)).thenReturn(mockCategoryAdminController);
			
		action.editSave(c);
		
		verify(service).update(c);
		verify(mockCategoryAdminController).list();
	}

	/**
	 * Test method for
	 * {@link net.jforum.controllers.CategoryAdminController#list()}.
	 */
	@Test
	public void list() {
		when(repository.getAllCategories()).thenReturn(new ArrayList<Category>());
			
		action.list();
		
		assertEquals(new ArrayList<Category>(), mockResult.included("categories"));
	}

	/**
	 * Test method for
	 * {@link net.jforum.controllers.CategoryAdminController#up(java.lang.Integer)}
	 * .
	 */
	@Test
	public void up() {
		when(mockResult.redirectTo(action)).thenReturn(mockCategoryAdminController);
			
		action.up(1);
		
		verify(service).upCategoryOrder(1);
		verify(mockCategoryAdminController).list();
	}

	/**
	 * Test method for
	 * {@link net.jforum.controllers.CategoryAdminController#down(java.lang.Integer)}
	 * .
	 */
	@Test
	public void down() {
		when(mockResult.redirectTo(action)).thenReturn(mockCategoryAdminController);
			

		action.down(2);
		
		verify(service).downCategoryOrder(2);
		verify(mockCategoryAdminController).list();
	}

	@Before
	public void setup() {
		action = new CategoryAdminController(repository, service, mockResult);
	}
}

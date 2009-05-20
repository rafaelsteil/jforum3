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
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Category;
import net.jforum.repository.CategoryRepository;
import net.jforum.services.CategoryService;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class CategoryAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private CategoryAdminActions component;
	private final CategoryRepository repository = context.mock(CategoryRepository.class);
	private final CategoryService service = context.mock(CategoryService.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);

	public CategoryAdminActionsTestCase() {
		super(CategoryAdminActions.class);
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(service).delete(1, 2);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		final Category c = new Category();

		c.setName("c1");
		c.setModerated(false);
		c.setDisplayOrder(1);

		context.checking(new Expectations() {{
			one(service).add(c);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.addSave(c);
		context.assertIsSatisfied();
	}

	@Test
	public void editExpectACategory() {
		context.checking(new Expectations() {{
			one(repository).get(5); will(returnValue(new Category()));
			one(propertyBag).put("category", new Category());
			one(viewService).renderView(Actions.ADD);
		}});

		component.edit(5);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		final Category c = new Category(); c.setId(2);

		context.checking(new Expectations() {{
			one(service).update(c);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.editSave(c);
		context.assertIsSatisfied();
	}

	/**
	 * Test method for {@link net.jforum.actions.CategoryAdminActions#list()}.
	 */
	@Test
	public void list() {
		context.checking(new Expectations() {{
			one(repository).getAllCategories(); will(returnValue(new ArrayList<Category>()));
			one(propertyBag).put("categories", new ArrayList<Category>());
		}});

		component.list();
		context.assertIsSatisfied();
	}

	/**
	 * Test method for {@link net.jforum.actions.CategoryAdminActions#up(java.lang.Integer)}.
	 */
	@Test
	public void up() {
		context.checking(new Expectations() {{
			one(service).upCategoryOrder(1);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.up(1);
		context.assertIsSatisfied();
	}

	/**
	 * Test method for {@link net.jforum.actions.CategoryAdminActions#down(java.lang.Integer)}.
	 */
	@Test
	public void down() {
		context.checking(new Expectations() {{
			one(service).downCategoryOrder(2);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.down(2);
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		component = new CategoryAdminActions(repository, service, propertyBag, viewService);
	}
}

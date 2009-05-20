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
package net.jforum.plugins.tagging;

import java.util.ArrayList;
import java.util.List;

import net.jforum.actions.AdminTestCase;
import net.jforum.actions.helpers.Actions;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Bill
 *
 */
public class TagAdminActionsTestCase extends AdminTestCase {

	private Mockery context = TestCaseUtils.newMockery();
	private ViewService viewService = context.mock(ViewService.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private TagRepository repository = context.mock(TagRepository.class);
	private TagAdminActions action = new TagAdminActions(propertyBag,viewService,repository);

	public TagAdminActionsTestCase() {
		super(TagAdminActions.class);
	}

	@Test
	public void deleteUsingNullShouldIgnore() {
		context.checking(new Expectations() {{
			one(viewService).redirectToAction(Actions.LIST);
		}});

		String tag = null;
		action.delete(tag);

		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		final String tag1 = "IT";
		final String tag2 = "Indonesia";

		context.checking(new Expectations() {{

			one(repository).remove(tag1);
			one(repository).remove(tag2);

			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.delete(tag1, tag2);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {

		context.checking(new Expectations() {{
			List<String> list = new ArrayList<String>();
			one(repository).getAll(); will(returnValue(list));
			one(propertyBag).put("tags", list);
		}});

		action.list();
		context.assertIsSatisfied();
	}

	@Test
	public void edit() {
		final String name = "IT";

		context.checking(new Expectations() {{
			one(propertyBag).put("name", name);
			one(viewService).renderView(Actions.ADD);
		}});

		action.edit(name);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		final String oldTag ="IT";
		final String newTag ="Information Technology";

		context.checking(new Expectations() {{
			one(repository).update(oldTag,newTag);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.editsave(oldTag,newTag);
		context.assertIsSatisfied();
	}

}

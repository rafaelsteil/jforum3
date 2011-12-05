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

import net.jforum.controllers.AdminTestCase;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.Result;

/**
 * @author Bill
 */
public class TagAdminControllerTestCase extends AdminTestCase {

	private Mockery context = TestCaseUtils.newMockery();
	private TagRepository repository = context.mock(TagRepository.class);
	private Result mockResult = context.mock(Result.class);
	private TagAdminController controller = new TagAdminController(repository, mockResult);
	private TagAdminController mockController = context.mock(TagAdminController.class);

	public TagAdminControllerTestCase() {
		super(TagAdminController.class);
	}

	@Test
	public void deleteUsingNullShouldIgnore() {
		context.checking(new Expectations() {{
			one(mockResult).of(controller); will(returnValue(mockController));
			one(mockController).list();
		}});

		String tag = null;
		controller.delete(tag);

		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		final String tag1 = "IT";
		final String tag2 = "Indonesia";

		context.checking(new Expectations() {{
			one(repository).remove(tag1);
			one(repository).remove(tag2);
			one(mockResult).of(controller); will(returnValue(mockController));
			one(mockController).list();
		}});

		controller.delete(tag1, tag2);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {

		context.checking(new Expectations() {{
			List<String> list = new ArrayList<String>();
			one(repository).getAll(); will(returnValue(list));
			one(mockResult).include("tags", list);
		}});

		controller.list();
		context.assertIsSatisfied();
	}

	@Test
	public void edit() {
		final String name = "IT";

		context.checking(new Expectations() {{
			one(mockResult).include("name", name);
			one(mockResult).of(controller); will(returnValue(mockController));
			one(mockController).add();
		}});

		controller.edit(name);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		final String oldTag ="IT";
		final String newTag ="Information Technology";

		context.checking(new Expectations() {{
			one(repository).update(oldTag,newTag);
			one(mockResult).redirectTo(controller); will(returnValue(mockController));
			one(mockController).list();
		}});

		controller.editsave(oldTag,newTag);
		context.assertIsSatisfied();
	}
}

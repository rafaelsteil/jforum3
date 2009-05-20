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
package net.jforum.plugins.shoutbox;

import java.util.ArrayList;

import net.jforum.actions.AdminTestCase;
import net.jforum.actions.helpers.Actions;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ShoutAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ShoutRepository repository = context.mock(ShoutRepository.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private ShoutAdminActions action = new ShoutAdminActions(propertyBag, repository, viewService);

	public ShoutAdminActionsTestCase() {
		super(ShoutAdminActions.class);
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Shout()));
			one(propertyBag).put("shout", new Shout());
			one(viewService).renderView(Actions.ADD);
		}});

		action.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		final Shout shout = new Shout();
		context.checking(new Expectations() {{
			one(repository).update(shout);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.editSave(shout);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Shout()));
			one(repository).remove(new Shout());
			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.delete(1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		context.checking(new Expectations() {{
			one(repository).getAll(); will(returnValue(new ArrayList<Shout>()));
			one(propertyBag).put("shouts", new ArrayList<Shout>());
		}});

		action.list();
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		context.checking(new Expectations() {{
			one(repository).add(new Shout());
			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.addSave(new Shout());
		context.assertIsSatisfied();
	}
}

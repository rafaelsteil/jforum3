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
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Category;
import net.jforum.entities.UserSession;
import net.jforum.security.RoleManager;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Bill
 */
public class ShoutBoxAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ShoutBoxRepository repository = context.mock(ShoutBoxRepository.class);
	private ShoutBoxService shoutBoxService = context.mock(ShoutBoxService.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private UserSession userSession = context.mock(UserSession.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private ShoutBoxAdminActions action = new ShoutBoxAdminActions(propertyBag, repository,shoutBoxService, viewService, sessionManager);

	public ShoutBoxAdminActionsTestCase() {
		super(ShoutBoxAdminActions.class);
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {{
			one(roleManager).isCategoryAllowed(1); will(returnValue(true));

			ShoutBox shoutbox = new ShoutBox();
			shoutbox.setCategory(new Category());
			shoutbox.getCategory().setId(1);

			one(shoutBoxService).get(1); will(returnValue(shoutbox));
			one(propertyBag).put("shoutbox", shoutbox);
		}});

		action.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		final ShoutBox shoutBox = new ShoutBox();
		shoutBox.setId(1);

		context.checking(new Expectations() {{
			ShoutBox current = new ShoutBox();
			current.setCategory(new Category());
			current.getCategory().setId(1);

			one(repository).get(1); will(returnValue(current));
			one(roleManager).isCategoryAllowed(1); will(returnValue(true));

			one(shoutBoxService).update(shoutBox);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.editSave(shoutBox);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		context.checking(new Expectations() {{
			one(repository).getAllShoutBoxes(); will(returnValue(new ArrayList<ShoutBox>()));
			one(propertyBag).put("shoutboxes", new ArrayList<ShoutBox>());
		}});

		action.list();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
		}});
	}
}

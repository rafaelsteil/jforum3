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

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.jforum.entities.UserSession;
import net.jforum.services.ViewService;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.vraptor.annotations.Remotable;

/**
 * @author Bill
 */
public class ShoutActionsTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private JForumConfig config = context.mock(JForumConfig.class);
	private ShoutBoxService shoutBoxService = context.mock(ShoutBoxService.class);
	private ShoutService shoutService = context.mock(ShoutService.class);
	private ShoutRepository shoutRepository = context.mock(ShoutRepository.class);
	private UserSession userSession = context.mock(UserSession.class);
	private ViewService viewService = context.mock(ViewService.class);
	private I18n i18n = context.mock(I18n.class);
	private ShoutActions action = new ShoutActions(config, viewService, i18n,shoutBoxService,shoutRepository,shoutService,userSession);

	@Test
	public void shoutShouldBeRemotable() throws SecurityException, NoSuchMethodException {
		Method method = action.getClass().getMethod("shout", Shout.class, int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(Remotable.class));
	}

	@Test
	public void shout() {
		final int shoutBoxId = 1;
		final Shout shout = new Shout();
		context.checking(new Expectations() {{
			one(shoutBoxService).get(shoutBoxId); will(returnValue(new ShoutBox()));
			one(shoutService).addShout(shout);
		}});

		action.shout(shout,shoutBoxId);
		context.assertIsSatisfied();
	}

	@Test
	public void readShouldBeRemotable() throws SecurityException, NoSuchMethodException {
		Method method = action.getClass().getMethod("read", int.class, int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(Remotable.class));
	}

	@Test
	public void read() {
		final int shoutBoxId = 1;
		final int lastId = 1;
		context.checking(new Expectations() {{
			one(shoutBoxService).get(shoutBoxId); will(returnValue(new ShoutBox()));
			one(shoutRepository).getShout(lastId, new ShoutBox(), 10); will(returnValue(new ArrayList<Shout>()));
		}});

		action.read(lastId, shoutBoxId);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteShouldBeRemotable() throws SecurityException, NoSuchMethodException {
		Method method = action.getClass().getMethod("delete", int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(Remotable.class));
	}

	@Test
	public void delete() {
		final int shoutId = 1;
		context.checking(new Expectations() {{
			Shout shout = new Shout();
			one(shoutRepository).get(shoutId); will(returnValue(shout));
			one(shoutService).delShout(shout);
		}});

		action.delete(shoutId);
		context.assertIsSatisfied();
	}

}

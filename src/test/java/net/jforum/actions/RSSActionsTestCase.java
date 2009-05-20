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

import net.jforum.actions.helpers.Actions;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.UserSession;
import net.jforum.security.RoleManager;
import net.jforum.services.RSSService;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RSSActionsTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private RSSService rssService = context.mock(RSSService.class);
	private UserSession userSession = context.mock(UserSession.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private RSSActions action = new RSSActions(propertyBag, viewService, rssService, userSession, config);

	@Test
	public void forumTopicsExpectSuccess() {
		context.checking(new Expectations() {{
			one(config).getBoolean(ConfigKeys.RSS_ENABLED); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(true));
			one(rssService).forForum(1, viewService); will(returnValue("contents"));
			one(propertyBag).put("contents", "contents");
			one(viewService).renderView(Actions.RSS);
		}});

		action.forumTopics(1);
		context.assertIsSatisfied();
	}

	@Test
	public void forumTopicsUserDoesNotHaveRightsShouldDeny() {
		context.checking(new Expectations() {{
			one(config).getBoolean(ConfigKeys.RSS_ENABLED); will(returnValue(true));
			one(roleManager).isForumAllowed(1); will(returnValue(false));
			one(viewService).renderView(Actions.ACCESS_DENIED);
		}});

		action.forumTopics(1);
		context.assertIsSatisfied();
	}

	@Test
	public void forumTopicsRSSDisabledShouldDeny() {
		context.checking(new Expectations() {{
			one(config).getBoolean(ConfigKeys.RSS_ENABLED); will(returnValue(false));
			one(viewService).renderView(Actions.ACCESS_DENIED);
		}});

		action.forumTopics(1);
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
		}});
	}
}

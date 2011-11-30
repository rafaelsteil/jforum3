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

import net.jforum.actions.helpers.Actions;
import net.jforum.entities.UserSession;
import net.jforum.security.RoleManager;
import net.jforum.services.RSSService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class RSSControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private RSSService rssService = context.mock(RSSService.class);
	private UserSession userSession = context.mock(UserSession.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private Result mockResult = context.mock(MockResult.class);
	private RSSController action = new RSSController(mockResult, rssService,
			userSession, config);

	@Test
	public void forumTopicsExpectSuccess() {
		context.checking(new Expectations() {
			{
				one(config).getBoolean(ConfigKeys.RSS_ENABLED);
				will(returnValue(true));
				one(roleManager).isForumAllowed(1);
				will(returnValue(true));
				one(rssService).forForum(1);
				will(returnValue("contents"));
				one(mockResult).include("contents", "contents");
				one(mockResult).forwardTo(Actions.RSS);
			}
		});

		action.forumTopics(1);
		context.assertIsSatisfied();
	}

	@Test
	public void forumTopicsUserDoesNotHaveRightsShouldDeny() {
		context.checking(new Expectations() {
			{
				one(config).getBoolean(ConfigKeys.RSS_ENABLED);
				will(returnValue(true));
				one(roleManager).isForumAllowed(1);
				will(returnValue(false));
				one(mockResult).forwardTo(Actions.ACCESS_DENIED);
			}
		});

		action.forumTopics(1);
		context.assertIsSatisfied();
	}

	@Test
	public void forumTopicsRSSDisabledShouldDeny() {
		context.checking(new Expectations() {
			{
				one(config).getBoolean(ConfigKeys.RSS_ENABLED);
				will(returnValue(false));
				one(mockResult).forwardTo(Actions.ACCESS_DENIED);
			}
		});

		action.forumTopics(1);
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {
			{
				allowing(userSession).getRoleManager();
				will(returnValue(roleManager));
			}
		});
	}
}

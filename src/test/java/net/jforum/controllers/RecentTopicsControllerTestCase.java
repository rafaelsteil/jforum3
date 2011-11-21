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

import java.util.ArrayList;

import net.jforum.controllers.RecentTopicsController;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.RecentTopicsRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class RecentTopicsControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private RecentTopicsRepository repository = context
			.mock(RecentTopicsRepository.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private UserSession userSession = context.mock(UserSession.class);
	private MockResult mockResult = new MockResult();
	private RecentTopicsController component = new RecentTopicsController(repository,
			config, userSession, mockResult);

	@Test
	public void listNew() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(null));
				one(repository).getNewTopics(10);
				will(returnValue(new ArrayList<Topic>()));
				one(config).getInt(ConfigKeys.RECENT_TOPICS);
				will(returnValue(10));
				one(mockResult).include("topics", new ArrayList<Topic>());
				one(mockResult).include("recentTopicsSectionKey",
						"recentTopicsNew");
				one(mockResult).forwardTo("list");
			}
		});

		component.listNew();
		context.assertIsSatisfied();
	}

	@Test
	public void listUpdated() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(null));
				one(repository).getUpdatedTopics(10);
				will(returnValue(new ArrayList<Topic>()));
				one(config).getInt(ConfigKeys.RECENT_TOPICS);
				will(returnValue(10));
				one(mockResult).include("topics", new ArrayList<Topic>());
				one(mockResult).include("recentTopicsSectionKey",
						"recentTopicsUpdated");
				one(mockResult).forwardTo("list");
			}
		});

		component.listUpdated();
		context.assertIsSatisfied();
	}

	@Test
	public void listHot() {
		context.checking(new Expectations() {
			{
				one(userSession).getRoleManager();
				will(returnValue(null));
				one(repository).getHotTopics(10);
				will(returnValue(new ArrayList<Topic>()));
				one(config).getInt(ConfigKeys.RECENT_TOPICS);
				will(returnValue(10));
				one(mockResult).include("topics", new ArrayList<Topic>());
				one(mockResult).include("recentTopicsSectionKey",
						"recentTopicsHot");
				one(mockResult).forwardTo("list");
			}
		});

		component.listHot();
		context.assertIsSatisfied();
	}
}

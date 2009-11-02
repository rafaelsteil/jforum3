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

import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.RecentTopicsRepository;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RecentTopicsActionsTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private RecentTopicsRepository repository = context.mock(RecentTopicsRepository.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private JForumConfig config = context.mock(JForumConfig.class);
    private ViewService viewService = context.mock(ViewService.class);
    private UserSession userSession = context.mock(UserSession.class);
	private RecentTopicsActions component = new RecentTopicsActions(repository, propertyBag, config, viewService, userSession);

	@Test
	public void listNew() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(null));
			one(repository).getNewTopics(10); will(returnValue(new ArrayList<Topic>()));
			one(config).getInt(ConfigKeys.RECENT_TOPICS); will(returnValue(10));
			one(propertyBag).put("topics", new ArrayList<Topic>());
			one(propertyBag).put("recentTopicsSectionKey", "recentTopicsNew");
			one(viewService).renderView("list");
		}});

		component.listNew();
		context.assertIsSatisfied();
	}

    @Test
	public void listUpdated() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(null));
			one(repository).getUpdatedTopics(10); will(returnValue(new ArrayList<Topic>()));
			one(config).getInt(ConfigKeys.RECENT_TOPICS); will(returnValue(10));
			one(propertyBag).put("topics", new ArrayList<Topic>());
			one(propertyBag).put("recentTopicsSectionKey", "recentTopicsUpdated");
			one(viewService).renderView("list");
		}});

		component.listUpdated();
		context.assertIsSatisfied();
	}

    @Test
	public void listHot() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(null));
			one(repository).getHotTopics(10); will(returnValue(new ArrayList<Topic>()));
			one(config).getInt(ConfigKeys.RECENT_TOPICS); will(returnValue(10));
			one(propertyBag).put("topics", new ArrayList<Topic>());
			one(propertyBag).put("recentTopicsSectionKey", "recentTopicsHot");
			one(viewService).renderView("list");
		}});

		component.listHot();
		context.assertIsSatisfied();
	}
}

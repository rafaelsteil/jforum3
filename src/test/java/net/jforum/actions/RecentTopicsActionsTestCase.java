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
import net.jforum.repository.RecentTopicsRepository;
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
	private RecentTopicsActions component = new RecentTopicsActions(repository, propertyBag, config);

	@Test
	public void list() {
		context.checking(new Expectations() {{
			one(repository).getRecentTopics(10); will(returnValue(new ArrayList<Topic>()));
			one(config).getInt(ConfigKeys.RECENT_TOPICS); will(returnValue(10));
			one(propertyBag).put("topics", new ArrayList<Topic>());
		}});

		component.list();
		context.assertIsSatisfied();
	}
}

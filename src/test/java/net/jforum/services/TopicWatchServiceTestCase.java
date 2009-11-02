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
package net.jforum.services;

import net.jforum.entities.Topic;
import net.jforum.entities.TopicWatch;
import net.jforum.entities.User;
import net.jforum.repository.TopicWatchRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class TopicWatchServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private TopicWatchRepository repository = context.mock(TopicWatchRepository.class);
	private TopicWatchService service = new TopicWatchService(repository);

	@Test
	public void unwatch() {
		final Topic topic = new Topic(); topic.setId(1);
		final User user = new User(); user.setId(2);

		context.checking(new Expectations() {{
			one(repository).removeSubscription(topic, user);
		}});

		service.unwatch(topic, user);
		context.assertIsSatisfied();
	}

	@Test
	public void watchUserNotSubscribedShouldAdd() {
		final Topic topic = new Topic(); topic.setId(1);
		final User user = new User(); user.setId(2);

		context.checking(new Expectations() {{
			one(repository).getSubscription(topic, user); will(returnValue(false));
			one(repository).add(with(aNonNull(TopicWatch.class)));
		}});

		service.watch(topic, user);
		context.assertIsSatisfied();
	}

	@Test
	public void watchUserIsSubscribedShouldIgnore() {
		final Topic topic = new Topic(); topic.setId(1);
		final User user = new User(); user.setId(2);

		context.checking(new Expectations() {{
			one(repository).getSubscription(topic, user); will(returnValue(true));
		}});

		service.watch(topic, user);
		context.assertIsSatisfied();
	}
}

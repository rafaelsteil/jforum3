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
package net.jforum.core.events.topic;

import net.jforum.entities.Topic;
import net.jforum.repository.TopicWatchRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class TopicWatchTopicEventTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private TopicWatchRepository repository = context.mock(TopicWatchRepository.class);
	private TopicWatchTopicEvent event = new TopicWatchTopicEvent(repository);

	@Test
	public void deleted() {
		final Topic topic = new Topic(); topic.setId(2);

		context.checking(new Expectations() {{
			one(repository).removeSubscription(topic);
		}});

		event.deleted(topic);
		context.assertIsSatisfied();
	}
}

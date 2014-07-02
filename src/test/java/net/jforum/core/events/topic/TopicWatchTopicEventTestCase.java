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

import static org.mockito.Mockito.*;
import net.jforum.entities.Topic;
import net.jforum.repository.TopicWatchRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class TopicWatchTopicEventTestCase {
	
	@Mock private TopicWatchRepository repository;
	@InjectMocks private TopicWatchTopicEvent event = new TopicWatchTopicEvent(repository);

	@Test
	public void deleted() {
		final Topic topic = new Topic(); topic.setId(2);
		
		event.deleted(topic);

		verify(repository).removeSubscription(topic);
	}
}

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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import net.jforum.entities.Topic;
import net.jforum.entities.TopicWatch;
import net.jforum.entities.User;
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
public class TopicWatchServiceTestCase {

	@Mock private TopicWatchRepository repository;
	@InjectMocks private TopicWatchService service;

	@Test
	public void unwatch() {
		final Topic topic = new Topic(); topic.setId(1);
		final User user = new User(); user.setId(2);

		service.unwatch(topic, user);

		verify(repository).removeSubscription(topic, user);
	}

	@Test
	public void watchUserNotSubscribedShouldAdd() {
		final Topic topic = new Topic(); topic.setId(1);
		final User user = new User(); user.setId(2);
		when(repository.getSubscription(topic, user)).thenReturn(null);

		service.watch(topic, user);

		verify(repository).add(notNull(TopicWatch.class));
	}

	@Test
	public void watchUserIsSubscribedShouldIgnore() {
		final Topic topic = new Topic(); topic.setId(1);
		final User user = new User(); user.setId(2);

		when(repository.getSubscription(topic, user)).thenReturn(new TopicWatch());

		service.watch(topic, user);
		
		verify(repository,never()).add(any(TopicWatch.class));
	}
}

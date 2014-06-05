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
package net.jforum.core.events.post;

import static org.mockito.Mockito.*;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.services.TopicWatchService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class TopicWatchPostEventTestCase {
	
	@Mock private TopicWatchService service;
	@InjectMocks private TopicWatchPostEvent event;

	@Test
	public void addedPostNotifyEnabledExpectWatch() {
		final Post post = new Post(); post.setNotifyReplies(true);
		post.setTopic(new Topic(1));
		User user = new User();
		user.setId(2);
		post.setUser(user);
		
		event.added(post);
		
		verify(service).watch(post.getTopic(), post.getUser());
	}

	@Test
	public void addedPostNotifyDisabledShouldDoNothing() {
		Post post = new Post(); post.setNotifyReplies(false);
		event.added(post);
	}
}

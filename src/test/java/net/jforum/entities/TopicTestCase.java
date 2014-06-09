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
package net.jforum.entities;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import net.jforum.repository.TopicRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class TopicTestCase {

	@Mock private TopicRepository repository;

	@Test
	public void getPosts() {
		final Topic topic = new Topic(repository);

		topic.getPosts(0, 10);

		verify(repository).getPosts(topic, 0, 10);
	}

	@Test
	public void getTotalPosts() {
		final Topic topic = new Topic();
		topic.incrementTotalReplies();

		assertEquals(2, topic.getTotalPosts());
	}

	@Test(expected = IllegalStateException.class)
	public void getPostsWithoutRepositoryExpectException() {
		new Topic().getPosts(0, 10);
	}
}

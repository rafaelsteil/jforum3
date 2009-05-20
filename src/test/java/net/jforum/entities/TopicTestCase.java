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

import junit.framework.Assert;
import net.jforum.repository.TopicRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class TopicTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private TopicRepository repository = context.mock(TopicRepository.class);

	@Test
	public void getPosts() {
		final Topic topic = new Topic(repository);

		context.checking(new Expectations() {{
			one(repository).getPosts(topic, 0, 10);
		}});

		topic.getPosts(0, 10);
		context.assertIsSatisfied();
	}

	@Test
	public void getTotalPosts() {
		final Topic topic = new Topic() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public int getTotalReplies() {
				return 1;
			}
		};

		Assert.assertEquals(2, topic.getTotalPosts());
	}

	@Test(expected = IllegalStateException.class)
	public void getPostsWithoutRepositoryExpectException() {
		new Topic().getPosts(0, 10);
	}
}

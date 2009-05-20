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

import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.services.TopicWatchService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class TopicWatchPostEventTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private TopicWatchService service = context.mock(TopicWatchService.class);
	private TopicWatchPostEvent event = new TopicWatchPostEvent(service);

	@Test
	public void addedPostNotifyEnabledExpectWatch() {
		final Post post = new Post(); post.setNotifyReplies(true);
		post.setTopic(new Topic() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});
		post.setUser(new User() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(2); }});

		context.checking(new Expectations() {{
			one(service).watch(post.getTopic(), post.getUser());
		}});

		event.added(post);
		context.assertIsSatisfied();
	}

	@Test
	public void addedPostNotifyDisabledShouldDoNothing() {
		Post post = new Post(); post.setNotifyReplies(false);
		event.added(post);
		context.assertIsSatisfied();
	}
}

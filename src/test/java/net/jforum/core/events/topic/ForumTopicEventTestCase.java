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

import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.repository.ForumRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ForumTopicEventTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ForumRepository repository = context.mock(ForumRepository.class);
	private ForumTopicEvent event = new ForumTopicEvent(repository);

	@Test
	public void deleteTopicLastPostIsNullShouldForceReload() {
		final Topic topic = this.newTopic();
		topic.getForum().setLastPost(null);

		context.checking(new Expectations() {{
			Post post = new Post(); post.setId(11);
			one(repository).getLastPost(topic.getForum()); will(returnValue(post));
		}});

		event.deleted(topic);
		context.assertIsSatisfied();

		Post expected = new Post(); expected.setId(11);
		Assert.assertEquals(expected, topic.getForum().getLastPost());
	}

	@Test
	public void deleteTopicExpectSuccess() {
		final Topic topic = this.newTopic();

		context.checking(new Expectations() {{
			Post post = new Post(); post.setId(11);
			one(repository).getLastPost(topic.getForum()); will(returnValue(post));
		}});

		topic.getLastPost().getTopic().setId(topic.getId());
		event.deleted(topic);
		context.assertIsSatisfied();

		Post expected = new Post(); expected.setId(11);
		Assert.assertEquals(expected, topic.getForum().getLastPost());
	}

	@Test
	public void deleteOrdinaryTopicShoulDoNothing() {
		final Topic topic = this.newTopic();
		topic.getForum().getLastPost().getTopic().setId(9);

		event.deleted(topic);
		context.assertIsSatisfied();
	}

	private Topic newTopic() {
		Topic t = new Topic(); t.setId(1); t.setUser(new User());

		t.setFirstPost(new Post()); t.getFirstPost().setId(1); t.getFirstPost().setTopic(new Topic());
		t.setLastPost(new Post()); t.getLastPost().setId(2); t.getLastPost().setTopic(new Topic());
		t.setForum(new Forum()); t.getForum().setId(1); t.getForum().setLastPost(t.getLastPost());

		return t;
	}
}

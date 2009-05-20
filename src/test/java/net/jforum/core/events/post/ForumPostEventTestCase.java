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

import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ForumPostEventTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ForumRepository repository = context.mock(ForumRepository.class);
	private ForumPostEvent event = new ForumPostEvent(repository);

	@Test
	public void deleteLastPostExpectUpdate() {
		final Post post = this.newPost();
		post.setId(2);

		context.checking(new Expectations() {{
			Post newLastPost = new Post(); newLastPost.setId(13);
			one(repository).getLastPost(post.getForum()); will(returnValue(newLastPost));
		}});

		event.deleted(post);
		context.assertIsSatisfied();

		Post expected = new Post(); expected.setId(13);
		Assert.assertEquals(expected, post.getForum().getLastPost());
	}

	@Test
	public void deleteOrdinaryPostShouldDoNothing() {
		context.checking(new Expectations() {{ }});
		Post post = this.newPost();
		post.setId(10);
		event.deleted(post);
		context.assertIsSatisfied();
	}

	private Post newPost() {
		Post p = new Post(); p.setId(1);
		Topic t = new Topic();
		t.setId(1);
		t.setFirstPost(new Post()); t.getFirstPost().setId(1);
		t.setLastPost(new Post()); t.getLastPost().setId(2);
		t.setForum(new Forum()); t.getForum().setId(1); t.getForum().setLastPost(t.getLastPost());
		p.setForum(t.getForum());
		p.setTopic(t);

		return p;
	}
}

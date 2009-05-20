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
import net.jforum.repository.TopicRepository;
import net.jforum.repository.UserRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class TopicPostEventTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private TopicRepository repository = context.mock(TopicRepository.class);
	private UserRepository userRepository = context.mock(UserRepository.class);
	private TopicPostEvent event = new TopicPostEvent(repository, userRepository);

	@Test
	public void shouldUpdateUserTotalPost() {
		final Post post = this.newPost();
		post.getUser().setTotalPosts(5);

		context.checking(new Expectations() {{
			one(repository).getTotalPosts(post.getTopic()); will(returnValue(1));
			allowing(repository);
			one(userRepository).getTotalPosts(post.getUser()); will(returnValue(2));
		}});

		event.deleted(post);
		context.assertIsSatisfied();

		Assert.assertEquals(2, post.getUser().getTotalPosts());
	}

	@Test
	public void emptyPostsShouldRemoveTopic() {
		final Post post = this.newPost();
		int totalPosts = post.getTopic().getTotalPosts();

		context.checking(new Expectations() {{
			one(repository).getTotalPosts(post.getTopic()); will(returnValue(0));
			one(repository).remove(post.getTopic());
		}});

		event.deleted(post);
		context.assertIsSatisfied();

		Assert.assertEquals(totalPosts - 1, post.getTopic().getTotalPosts());
	}

	@Test
	public void removeLastPostOnly() {
		final Post post = this.newPost();
		post.getTopic().getFirstPost().setId(3);
		post.getTopic().getLastPost().setId(4);
		int totalPosts = post.getTopic().getTotalPosts();

		context.checking(new Expectations() {{
			Post lastPost = new Post(); lastPost.setId(5);
			one(repository).getTotalPosts(post.getTopic()); will(returnValue(2));
			one(repository).getLastPost(post.getTopic()); will(returnValue(lastPost));
			one(userRepository).getTotalPosts(post.getUser());
		}});

		post.setId(4);
		event.deleted(post);
		context.assertIsSatisfied();

		Post expected = new Post(); expected.setId(5);
		Assert.assertEquals(expected, post.getTopic().getLastPost());
		Assert.assertEquals(totalPosts - 1, post.getTopic().getTotalPosts());
	}

	@Test
	public void removeFirstPostOnly() {
		final Post post = this.newPost();
		int totalPosts = post.getTopic().getTotalPosts();

		context.checking(new Expectations() {{
			Post newFirst = newPost(); newFirst.setId(6);
			newFirst.getUser().setId(9);

			one(repository).getTotalPosts(post.getTopic()); will(returnValue(2));
			one(repository).getFirstPost(post.getTopic()); will(returnValue(newFirst));
			one(userRepository).getTotalPosts(post.getUser());
		}});

		event.deleted(post);
		context.assertIsSatisfied();

		Post expected = new Post(); expected.setId(6);
		Assert.assertEquals(expected, post.getTopic().getFirstPost());

		User expectedUser = new User(); expectedUser.setId(9);
		Assert.assertEquals(expectedUser, post.getTopic().getUser());
		Assert.assertEquals(totalPosts - 1, post.getTopic().getTotalPosts());
	}

	private Post newPost() {
		Post p = new Post(); p.setId(1); p.setUser(new User());
		Topic t = new Topic(); t.setId(1);
		t.setUser(new User());
		t.setFirstPost(new Post()); t.getFirstPost().setId(1);
		t.setLastPost(new Post()); t.getLastPost().setId(2);
		p.setTopic(t);

		return p;
	}
}

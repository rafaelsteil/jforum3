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
import net.jforum.repository.ForumRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ForumTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ForumRepository repository = context.mock(ForumRepository.class);

	@Test
	public void getTotalTopics() {
		final Forum forum = new Forum(repository);

		context.checking(new Expectations() {{
			one(repository).getTotalTopics(forum);
		}});

		forum.getTotalTopics();
		context.assertIsSatisfied();
	}

	@Test
	public void getTopicsPendingModerationForumIsNotModeratedShouldReturnEmptyList() {
		Forum forum = new Forum(); forum.setModerated(false);
		Assert.assertEquals(0, forum.getTopicsPendingModeration().size());
	}

	@Test
	public void getTopicsPendingModeration() {
		final Forum forum = new Forum(repository);
		forum.setModerated(true);

		context.checking(new Expectations() {{
			one(repository).getTopicsPendingModeration(forum);
		}});

		forum.getTopicsPendingModeration();
		context.assertIsSatisfied();
	}

	@Test
	public void getTopics() {
		final Forum forum = new Forum(repository);

		context.checking(new Expectations() {{
			one(repository).getTopics(forum, 0, 10);
		}});

		forum.getTopics(0, 10);
		context.assertIsSatisfied();
	}

	@Test
	public void getTotalPosts() {
		final Forum forum = new Forum(repository);

		context.checking(new Expectations() {{
			one(repository).getTotalPosts(forum);
		}});

		forum.getTotalPosts();
		context.assertIsSatisfied();
	}

	@Test
	public void getModeratorsForumIsNotModeratedExpectEmptyList() {
		Forum forum = new Forum();
		forum.setModerated(false);
		Assert.assertEquals(0, forum.getModerators().size());
	}

	@Test
	public void getModerators() {
		final Forum forum = new Forum(repository);
		forum.setModerated(true);

		context.checking(new Expectations() {{
			one(repository).getModerators(forum);
		}});

		forum.getModerators();
		context.assertIsSatisfied();
	}

	@Test(expected = IllegalStateException.class)
	public void getModeratorsWithoutRepositoryExpectException() {
		Forum forum = new Forum();
		forum.setModerated(true);
		forum.getModerators();
	}

	@Test(expected = IllegalStateException.class)
	public void getTotalTopicsWithoutRepositoryExpectException() {
		new Forum().getTotalTopics();
	}

	@Test(expected = IllegalStateException.class)
	public void getTotalPostsWithoutRepositoryExpectException() {
		new Forum().getTotalPosts();
	}

	@Test(expected = IllegalStateException.class)
	public void getTopicsWithoutRepositoryExpectException() {
		new Forum().getTopics(0, 10);
	}

	@Test(expected = IllegalStateException.class)
	public void getTopicsPendingModerationWithoutRepositoryExpectException() {
		Forum forum = new Forum();
		forum.setModerated(true);
		forum.getTopicsPendingModeration();
	}
}

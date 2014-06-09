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
import net.jforum.repository.ForumRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ForumTestCase {

	@Mock
	private ForumRepository repository;

	@Test
	public void getTotalTopics() {
		final Forum forum = new Forum(repository);

		forum.getTotalTopics();

		verify(repository).getTotalTopics(forum);
	}

	@Test
	public void getTopicsPendingModerationForumIsNotModeratedShouldReturnEmptyList() {
		Forum forum = new Forum();
		forum.setModerated(false);
		assertEquals(0, forum.getTopicsPendingModeration().size());
	}

	@Test
	public void getTopicsPendingModeration() {
		final Forum forum = new Forum(repository);
		forum.setModerated(true);

		forum.getTopicsPendingModeration();

		verify(repository).getTopicsPendingModeration(forum);
	}

	@Test
	public void getTopics() {
		final Forum forum = new Forum(repository);

		forum.getTopics(0, 10);

		verify(repository).getTopics(forum, 0, 10);
	}

	@Test
	public void getTotalPosts() {
		final Forum forum = new Forum(repository);

		forum.getTotalPosts();

		verify(repository).getTotalPosts(forum);
	}

	@Test
	public void getModeratorsForumIsNotModeratedExpectEmptyList() {
		Forum forum = new Forum();
		forum.setModerated(false);
		assertEquals(0, forum.getModerators().size());
	}

	@Test
	public void getModerators() {
		final Forum forum = new Forum(repository);
		forum.setModerated(true);

		forum.getModerators();

		verify(repository).getModerators(forum);
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

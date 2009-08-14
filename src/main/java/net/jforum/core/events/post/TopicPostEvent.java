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
import net.jforum.events.EmptyPostEvent;
import net.jforum.repository.TopicRepository;
import net.jforum.repository.UserRepository;

import org.hibernate.ObjectNotFoundException;

/**
 * Post events related to a topic.
 * @author Rafael Steil
 */
public class TopicPostEvent extends EmptyPostEvent {
	private TopicRepository topicRepository;
	private UserRepository userRepository;

	public TopicPostEvent(TopicRepository topicRepository, UserRepository userRepository) {
		this.topicRepository = topicRepository;
		this.userRepository = userRepository;
	}

	/**
	 * The actions are:
	 * <ul>
	 * 	<li> if topic.totalPosts == 0, delete topic
	 * 	<li> If 1st post, update topic.firstPost
	 * 	<li> If last post, update topic.lastPost
	 * 	<li> Decrement topic replies
	 * </ul>
	 */
	@Override
	public void deleted(Post post) {
		boolean isTopicDeleted = this.handleEmptyTopic(post);
		post.getTopic().decrementTotalReplies();

		if (!isTopicDeleted) {
			// If it wasn't the first post which was deleted,
			// then check if it was the last one. It never will
			// be both (which would result in a topic delete)
			if (!this.handleFirstPostDeleted(post)) {
				this.handleLastPostDeleted(post);
			}

			int userTotalPosts = this.userRepository.getTotalPosts(post.getUser());
			post.getUser().setTotalPosts(userTotalPosts);
		}
	}

	private boolean handleEmptyTopic(Post post) {
		if (this.topicRepository.getTotalPosts(post.getTopic()) < 1) {
			this.topicRepository.remove(post.getTopic());
			return true;
		}

		return false;
	}

	private void handleLastPostDeleted(Post post) {
		boolean isLastPost = false;

		try {
			// FIXME: post.getTopic.getLastPost() may throw this exception,
			// because the post itself was deleted before this method,
			// and a call to post.getTopic().getLastPost() may issue
			// a query to load the last post of such topic, which
			// won't exist, of course. So, is this expected, or should
			// we handle this using another approach?
			isLastPost = post.getTopic().getLastPost().equals(post);
		}
		catch (ObjectNotFoundException e) {
			isLastPost = true;
		}

		if (isLastPost) {
			post.getTopic().setLastPost(this.topicRepository.getLastPost(post.getTopic()));
		}
	}

	private boolean handleFirstPostDeleted(Post post) {
		boolean isFirstPost = false;

		try {
			isFirstPost = post.getTopic().getFirstPost().equals(post);
		}
		catch (ObjectNotFoundException e) {
			isFirstPost = true;
		}

		if (isFirstPost) {
			Post firstPost = this.topicRepository.getFirstPost(post.getTopic());
			post.getTopic().setFirstPost(firstPost);
			post.getTopic().setUser(firstPost.getUser());

			return true;
		}

		return false;
	}
}

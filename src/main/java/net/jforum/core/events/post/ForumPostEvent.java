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
import net.jforum.repository.ForumRepository;

import org.hibernate.ObjectNotFoundException;

/**
 * @author Rafael Steil
 */
public class ForumPostEvent extends EmptyPostEvent {
	private ForumRepository repository;

	public ForumPostEvent(ForumRepository repository) {
		this.repository = repository;
	}

	/**
	 * The actions are:
	 * <ul>
	 * 	<li> If last post, update forum.lastPost
	 * </ul>
	 */
	@Override
	public void deleted(Post post) {
		boolean isLastPost = false;

		try {
			// FIXME: Check TopicPostEvent#handleLastPostDeleted
			isLastPost = post.equals(post.getForum().getLastPost());
		}
		catch (ObjectNotFoundException e) {
			isLastPost = true;
		}

		if (isLastPost) {
			Post lastPost = this.repository.getLastPost(post.getForum());
			post.getForum().setLastPost(lastPost);
		}
	}
}

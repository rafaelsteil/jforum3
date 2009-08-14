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
import net.jforum.entities.Topic;
import net.jforum.events.EmptyTopicEvent;
import net.jforum.repository.ForumRepository;

import org.hibernate.ObjectNotFoundException;

/**
 * @author Rafael Steil
 */
public class ForumTopicEvent extends EmptyTopicEvent {
	private ForumRepository repository;

	public ForumTopicEvent(ForumRepository repository) {
		this.repository = repository;
	}

	/**
	 * The actions are:
	 * <ul>
	 * 	<li> If topic.lastPost == forum.lastPost, update forum.lastPost
	 * </ul>
	 */
	@Override
	public void deleted(Topic topic) {
		Forum forum = topic.getForum();
		boolean topicMatches = false;

		try {
			// FIXME: Check TopiPostEvent#handleLastPostDeleted
			topicMatches = forum.getLastPost() == null
				? true
				: forum.getLastPost().getTopic().equals(topic);
		}
		catch (ObjectNotFoundException e) {
			topicMatches = true;
		}

		if (topicMatches) {
			forum.setLastPost(this.repository.getLastPost(forum));
		}
	}
}

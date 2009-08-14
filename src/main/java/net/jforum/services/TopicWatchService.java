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
package net.jforum.services;

import net.jforum.entities.Topic;
import net.jforum.entities.TopicWatch;
import net.jforum.entities.User;
import net.jforum.repository.TopicWatchRepository;

/**
 * Topic Watching for posts (new posts)
 * @author Rafael Steil
 */
public class TopicWatchService {
	private TopicWatchRepository repository;

	public TopicWatchService(TopicWatchRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see {@link TopicWatchRepository#isUserSubscribed(Topic, User))
	 */
	public boolean isUserSubscribed(Topic topic, User user) {
		return this.repository.isUserSubscribed(topic, user);
	}

	/**
	 * Watch a specific topic
	 * @param topic the topic to watch
	 * @param user the user who wants to watch
	 */
	public void watch(Topic topic, User user) {
		boolean isUserSubscribed = this.repository.isUserSubscribed(topic, user);

		if (!isUserSubscribed) {
			TopicWatch watch = new TopicWatch();
			watch.setTopic(topic);
			watch.setUser(user);

			this.repository.add(watch);
		}
	}

	/**
	 * Unwatch a specific topic
	 * @param topic the topic to unwatch
	 * @param user the user who wants to unwatch
	 */
	public void unwatch(Topic topic, User user) {
		this.repository.removeSubscription(topic, user);
	}
}

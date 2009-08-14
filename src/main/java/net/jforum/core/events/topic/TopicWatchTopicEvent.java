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

import net.jforum.entities.Topic;
import net.jforum.events.EmptyTopicEvent;
import net.jforum.repository.TopicWatchRepository;

/**
 * @author Rafael Steil
 */
public class TopicWatchTopicEvent extends EmptyTopicEvent {
	private TopicWatchRepository repository;

	public TopicWatchTopicEvent(TopicWatchRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see net.jforum.events.EmptyTopicEvent#deleted(net.jforum.entities.Topic)
	 */
	@Override
	public void deleted(Topic topic) {
		this.repository.removeSubscription(topic);
	}
}

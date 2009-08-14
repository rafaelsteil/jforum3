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
import net.jforum.services.TopicWatchService;

/**
 * @author Rafael Steil
 */
public class TopicWatchPostEvent extends EmptyPostEvent {
	private TopicWatchService service;

	public TopicWatchPostEvent(TopicWatchService service) {
		this.service = service;
	}

	/**
	 * @see net.jforum.events.EmptyPostEvent#added(net.jforum.entities.Post)
	 */
	@Override
	public void added(Post post) {
		if (post.shouldNotifyReplies()) {
			this.service.watch(post.getTopic(), post.getUser());
		}
	}
}

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

import java.util.List;

import net.jforum.entities.Post;
import net.jforum.entities.User;
import net.jforum.events.EmptyPostEvent;
import net.jforum.repository.TopicWatchRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.mail.Spammer;
import net.jforum.util.mail.SpammerFactory;
import net.jforum.util.mail.SpammerTaskExecutor;

/**
 * When a new post is added, dispatch emails to the users who are watching the respective topic.
 * @author Rafael Steil
 */
public class TopicReplyEvent extends EmptyPostEvent {
	private TopicWatchRepository watchRepository;
	private SpammerTaskExecutor taskExecutor;
	private JForumConfig config;
	private SpammerFactory spammerFactory;

	public TopicReplyEvent(TopicWatchRepository watchRepository, SpammerTaskExecutor taskExecutor,
		JForumConfig config, SpammerFactory spammerFactory) {
		this.watchRepository = watchRepository;
		this.taskExecutor = taskExecutor;
		this.config = config;
		this.spammerFactory = spammerFactory;
	}

	/**
	 * @see net.jforum.events.EmptyPostEvent#added(net.jforum.entities.Post)
	 */
	@Override
	public void added(Post post) {
		// TODO: should also consider moderated posts (and the respective moderation action)
		if (this.config.getBoolean(ConfigKeys.MAIL_NOTIFY_ANSWERS)) {
			List<User> users = this.watchRepository.getUsersWaitingNotification(post.getTopic());

			Spammer spammer = this.spammerFactory.newTopicReply(post.getTopic(), users);

			this.taskExecutor.dispatch(spammer);
		}
	}
}

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
package net.jforum.util.mail;

import java.util.List;

import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.util.JForumConfig;

/**
 * Factory for specific types of mail dispatching
 * @author Rafael Steil
 */
public class SpammerFactory {
	private JForumConfig config;

	public SpammerFactory(JForumConfig config) {
		this.config = config;
	}

	/**
	 * Creates a {@link TopicReplySpammer} istance
	 * @param topic the topic being replies
	 * @param users the list of users who should be notified
	 * @return the spammer instance
	 */
	public Spammer newTopicReply(Topic topic, List<User> users) {
		TopicReplySpammer spammer = new TopicReplySpammer(this.config);
		spammer.prepare(topic, users);

		return spammer;
	}
}

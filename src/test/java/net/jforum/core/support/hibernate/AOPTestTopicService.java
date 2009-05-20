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
package net.jforum.core.support.hibernate;

import java.util.List;

import net.jforum.actions.helpers.AttachedFile;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.services.TopicService;

/**
 * @author Rafael Steil
 */
public class AOPTestTopicService extends TopicService {
	/**
	 * @see net.jforum.services.TopicService#addTopic(net.jforum.entities.Topic, java.util.List, java.util.List)
	 */
	@Override
	public void addTopic(Topic topic, List<PollOption> pollOptions, List<AttachedFile> attachments) {
	}

	/**
	 * @see net.jforum.services.TopicService#reply(net.jforum.entities.Topic, net.jforum.entities.Post, java.util.List)
	 */
	@Override
	public void reply(Topic topic, Post post, List<AttachedFile> attachments) {
	}
}

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

import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.services.ModerationService;

/**
 * @author Rafael Steil
 */
public class AOPTestModerationService extends ModerationService {
	/**
	 * @see net.jforum.services.ModerationService#deleteTopics(java.util.List)
	 */
	@Override
	public void deleteTopics(List<Topic> topics) {
	}

	/**
	 * @see net.jforum.services.ModerationService#approvePost(net.jforum.entities.Post)
	 */
	@Override
	public void approvePost(Post post) {
	}

	/**
	 * @see net.jforum.services.ModerationService#moveTopics(int, int[])
	 */
	@Override
	public void moveTopics(int toForumId, int... topicIds) {
	}
}

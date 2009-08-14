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
package net.jforum.repository;

import java.util.List;

import net.jforum.entities.Topic;

/**
 * @author Rafael Steil
 */
public interface RecentTopicsRepository extends Repository<Topic> {
	/**
	 * Get the most recent topics from the entire board
	 * @param count how many topics should be retrieved
	 * @return a list of most recent topics, from all forums
	 */
	public List<Topic> getNewTopics(int count);

	public List<Topic> getUpdatedTopics(int count);

	public List<Topic> getHotTopics(int count);
}

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

import java.util.Date;
import java.util.List;

import net.jforum.entities.Forum;
import net.jforum.entities.ForumStats;
import net.jforum.entities.Group;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.util.PaginatedResult;

/**
 * @author Rafael Steil
 */
public interface ForumRepository extends Repository<Forum> {
	public ForumStats getForumStats();

	/**
	 * Get the moderators (if any) of some forum
	 * @param forum the forum
	 * @return the moderators
	 */
	public List<Group> getModerators(Forum forum);

	/**
	 * Get the topics from a given forum
	 * @param forum
	 * @param intValue
	 * @return
	 */
	public List<Topic> getTopics(Forum forum, int start, int count);

	/**
	 * @return the number of messages in the entire forum
	 */
	public int getTotalMessages();

	/**
	 * @param forum the forum
	 * @return the number of posts in the forum
	 */
	public int getTotalPosts(Forum forum);

	/**
	 * Gets the last post of a given forum
	 * @param topic the forum to check
	 * @return the post instance
	 */
	public Post getLastPost(Forum forum);

	/**
	 * Get all topics that have pending moderation posts
	 * @param forum the forum
	 * @return a list with all topics with pending messages in this forum
	 */
	public List<Topic> getTopicsPendingModeration(Forum forum);

	/**
	 * Get the number of topics in a specific forum
	 * @param forum the forum
	 * @return the number of non-pending moderatio topics
	 */
	public int getTotalTopics(Forum forum);

	/**
	 * @param date
	 * @param start
	 * @param recordsPerPage
	 */
	public PaginatedResult<Topic> getNewMessages(Date from, int start, int recordsPerPage);

	/**
	 * @param forum
	 * @param topicIds
	 */
	public void moveTopics(Forum toForum, int... topicIds);

	/**
	 * Load all the forum
	 * @return
	 */
	public List<Forum> loadAll();
}
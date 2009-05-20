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

import net.jforum.entities.Post;
import net.jforum.entities.Topic;

/**
 * @author Rafael Steil
 */
public interface TopicRepository extends Repository<Topic> {
	/**
	 * Gets the first post of a given topic
	 * @param topic the topic to check
	 * @return the first post instance
	 */
	public Post getFirstPost(Topic topic);

	/**
	 * Gets the last post of a given topic
	 * @param topic the topic to check
	 * @return the post instance
	 */
	public Post getLastPost(Topic topic);

	/**
	 * Get the number of posts in this topic
	 * @param topic the topic to check
	 * @return the total posts in the topic
	 */
	public int getTotalPosts(Topic topic);

	/**
	 * Get all posts from a given topic.
	 *
	 * @param topic the topic of the posts
	 * @param start the initial page to start fetching
	 * @param count the number of records to fetch
	 * @return all non-pending moderation posts
	 */
	public List<Post> getPosts(Topic topic, int start, int count);
}
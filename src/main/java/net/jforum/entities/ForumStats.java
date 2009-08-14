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
package net.jforum.entities;

/**
 * @author Rafael Steil
 */
public class ForumStats {
	private int users;
	private int posts;
	private int topics;
	private double postsPerDay;
	private double topicsPerDay;
	private double usersPerDay;

	/**
	 * @return Returns the posts.
	 */
	public int getPosts() {
		return this.posts;
	}

	/**
	 * @param posts The posts to set.
	 */
	public void setPosts(int posts) {
		this.posts = posts;
	}

	/**
	 * @return Returns the postsPerDay.
	 */
	public double getPostsPerDay() {
		return this.postsPerDay;
	}

	/**
	 * @param postsPerDay The postsPerDay to set.
	 */
	public void setPostsPerDay(double postsPerDay) {
		this.postsPerDay = postsPerDay;
	}

	/**
	 * @return Returns the topics.
	 */
	public int getTopics() {
		return this.topics;
	}

	/**
	 * @param topics The topics to set.
	 */
	public void setTotalTopics(int topics) {
		this.topics = topics;
	}

	/**
	 * @return Returns the topicsPerDay.
	 */
	public double getTopicsPerDay() {
		return this.topicsPerDay;
	}

	/**
	 * @param topicsPerDay The topicsPerDay to set.
	 */
	public void setTopicsPerDay(double topicsPerDay) {
		this.topicsPerDay = topicsPerDay;
	}

	/**
	 * @return Returns the users.
	 */
	public int getUsers() {
		return this.users;
	}

	/**
	 * @param users The users to set.
	 */
	public void setTotalUsers(int users) {
		this.users = users;
	}

	/**
	 * @return Returns the usersPerDay.
	 */
	public double getUsersPerDay() {
		return this.usersPerDay;
	}

	/**
	 * @param usersPerDay The usersPerDay to set.
	 */
	public void setUsersPerDay(double usersPerDay) {
		this.usersPerDay = usersPerDay;
	}
}

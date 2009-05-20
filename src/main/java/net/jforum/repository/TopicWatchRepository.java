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
import net.jforum.entities.TopicWatch;
import net.jforum.entities.User;

/**
 * @author Rafael Steil
 */
public interface TopicWatchRepository extends Repository<TopicWatch> {

	/**
	 * Get the users to notify
	 *
	 * @param topic The topic
	 * @return <code>ArrayList</code> of <code>User</code> objects. Each
	 * entry is an user who will receive the topic anwser notification
	 * */
	public List<User> getUsersWaitingNotification(Topic topic);

	/**
	 * Check the subscrition status of the user on the topic.
	 *
	 * @param topic the topic
	 * @param user the user
	 * @return true if the user is watching the topic
	 */
	public boolean isUserSubscribed(Topic topic, User user);

	/**
	 * Clear all subscriptions of some topic
	 *
	 * @param topic the topic
	 */
	public void removeSubscription(Topic topic);

	/**
	 * Remove the subscription of a specific user
	 * @param topic the topic to remove the subscription
	 * @param user the user to remove the subscription
	 */
	public void removeSubscription(Topic topic, User user);

}
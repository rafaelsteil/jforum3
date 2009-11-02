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
package net.jforum.core.hibernate;

import java.util.List;

import net.jforum.entities.Topic;
import net.jforum.entities.TopicWatch;
import net.jforum.entities.User;
import net.jforum.repository.TopicWatchRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * @author Rafael Steil
 */
public class TopicWatchDAO extends HibernateGenericDAO<TopicWatch> implements TopicWatchRepository {
	public TopicWatchDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.TopicWatchRepository#getUsersWaitingNotification(net.jforum.entities.Topic)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersWaitingNotification(Topic topic) {
		List<User> users = this.session().createQuery("select u from TopicWatch tw " +
			" inner join tw.user u where tw.topic = :topic " +
			" and (tw.read = true or u.notifyAlways = true)")
			.setEntity("topic", topic)
			.setComment("topicWatchDAO.getUsersWaitingNotification")
			.list();

		if (users.size() > 0) {
			this.markAllAsUnread(topic);
		}

		return users;
	}

	/**
	 * @see net.jforum.repository.TopicWatchRepository#getSubscription(net.jforum.entities.Topic, net.jforum.entities.User)
	 */
	public TopicWatch getSubscription(Topic topic, User user) {
		return (TopicWatch)this.session().createCriteria(this.persistClass)
			.add(Restrictions.eq("topic", topic))
			.add(Restrictions.eq("user", user))
			.setComment("topicWatchDAO.isUserSubscribed")
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.TopicWatchRepository#removeSubscription(net.jforum.entities.Topic, net.jforum.entities.User)
	 */
	public void removeSubscription(Topic topic, User user) {
		this.session().createQuery("delete from TopicWatch tw where tw.topic = :topic and tw.user = :user")
			.setEntity("topic", topic)
			.setEntity("user", user)
			.setComment("topicWatchDAO.removeSubscriptionByUser")
			.executeUpdate();
	}

	/**
	 * @see net.jforum.repository.TopicWatchRepository#removeSubscription(net.jforum.entities.Topic)
	 */
	public void removeSubscription(Topic topic) {
		this.session().createQuery("delete from TopicWatch tw where tw.topic = :topic")
			.setEntity("topic", topic)
			.setComment("topicWatchDAO.removeSubscription")
			.executeUpdate();
	}

	private void markAllAsUnread(Topic topic) {
		this.session().createQuery("update TopicWatch set read = false where topic = :topic")
			.setEntity("topic", topic)
			.setComment("topicWatchDAO.markAllAsRead")
			.executeUpdate();
	}
}

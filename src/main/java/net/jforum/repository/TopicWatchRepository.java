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

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class TopicWatchRepository extends HibernateGenericDAO<TopicWatch> implements Repository<TopicWatch> {
	public TopicWatchRepository(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersWaitingNotification(Topic topic) {
		List<User> users = session.createQuery("select u from TopicWatch tw " +
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

	public TopicWatch getSubscription(Topic topic, User user) {
		return (TopicWatch)session.createCriteria(this.persistClass)
			.add(Restrictions.eq("topic", topic))
			.add(Restrictions.eq("user", user))
			.setComment("topicWatchDAO.isUserSubscribed")
			.uniqueResult();
	}

	public void removeSubscription(Topic topic, User user) {
		session.createQuery("delete from TopicWatch tw where tw.topic = :topic and tw.user = :user")
			.setEntity("topic", topic)
			.setEntity("user", user)
			.setComment("topicWatchDAO.removeSubscriptionByUser")
			.executeUpdate();
	}

	public void removeSubscription(Topic topic) {
		session.createQuery("delete from TopicWatch tw where tw.topic = :topic")
			.setEntity("topic", topic)
			.setComment("topicWatchDAO.removeSubscription")
			.executeUpdate();
	}

	private void markAllAsUnread(Topic topic) {
		session.createQuery("update TopicWatch set read = false where topic = :topic")
			.setEntity("topic", topic)
			.setComment("topicWatchDAO.markAllAsRead")
			.executeUpdate();
	}
}

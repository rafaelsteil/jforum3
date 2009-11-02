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
import net.jforum.repository.RecentTopicsRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * @author Rafael Steil
 */
public class RecentTopicsDAO extends HibernateGenericDAO<Topic> implements RecentTopicsRepository {
	public RecentTopicsDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.RecentTopicsRepository#getNewTopics(int)
	 */
	@SuppressWarnings("unchecked")
	public List<Topic> getNewTopics(int count) {
		return this.session().createCriteria(this.persistClass)
			.add(Restrictions.eq("pendingModeration", false))
			.addOrder(Order.desc("id"))
			.setMaxResults(count)
			.setCacheable(true)
			.setCacheRegion("recentTopicsDAO")
			.setComment("recentTopicsDAO.getRecentTopics")
			.list();
	}

	@SuppressWarnings("unchecked")
	public List<Topic> getUpdatedTopics(int count) {
		return this.session().createCriteria(this.persistClass)
			.add(Restrictions.eq("pendingModeration", false))
			.createAlias("lastPost", "lastPost")
			.addOrder(Order.desc("lastPost.id"))
			.setMaxResults(count)
			.setCacheable(true)
			.setCacheRegion("recentTopicsDAO")
			.setComment("recentTopicsDAO.getRecentTopics")
			.list();
	}

	@SuppressWarnings("unchecked")
	public List<Topic> getHotTopics(int count) {
		return this.session().createCriteria(this.persistClass)
			.add(Restrictions.eq("pendingModeration", false))
			.createAlias("lastPost", "lastPost")
			.addOrder(Order.desc("totalReplies"))
			.setMaxResults(count)
			.setCacheable(true)
			.setCacheRegion("recentTopicsDAO")
			.setComment("recentTopicsDAO.getRecentTopics")
			.list();
	}
}

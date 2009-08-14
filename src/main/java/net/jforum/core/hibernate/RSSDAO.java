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

import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.repository.RSSRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * @author Rafael Steil
 */
public class RSSDAO implements RSSRepository {
	private SessionFactory sessionFactory;

	public RSSDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see net.jforum.repository.RSSRepository#getForumTopics(net.jforum.entities.Forum, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Topic> getForumTopics(Forum forum, int count) {
		return this.sessionFactory.getCurrentSession().createCriteria(Topic.class)
			.add(Restrictions.eq("forum", forum))
			.add(Restrictions.eq("pendingModeration", false))
			.addOrder(Order.desc("date"))
			.setMaxResults(count)
			.setCacheable(true)
			.setCacheRegion("rssDAO.getForumTopics#" + forum.getId())
			.setComment("rssDAO.getForumTopics#" + forum.getId())
			.list();
	}

}

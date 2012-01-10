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

import net.jforum.entities.Forum;
import net.jforum.entities.Topic;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class RSSRepository {
	private final Session session;

	public RSSRepository(Session session) {
		this.session = session;
	}

	/**
	 * @see net.jforum.repository.RSSRepository#getForumTopics(net.jforum.entities.Forum, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Topic> getForumTopics(Forum forum, int count) {
		return session.createCriteria(Topic.class)
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

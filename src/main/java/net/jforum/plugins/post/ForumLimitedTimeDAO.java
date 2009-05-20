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
package net.jforum.plugins.post;

import net.jforum.core.hibernate.HibernateGenericDAO;
import net.jforum.entities.Forum;

import org.hibernate.SessionFactory;

/**
 * @author Bill
 */
public class ForumLimitedTimeDAO extends HibernateGenericDAO<ForumLimitedTime>
		implements ForumLimitedTimeRepository {

	public ForumLimitedTimeDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.plugins.post.ForumLimitedTimeRepository#getLimitedTime(net.jforum.entities.Forum)
	 */
	public long getLimitedTime(Forum forum) {
		ForumLimitedTime forumLimited = this.getForumLimitedTime(forum);
		return forumLimited != null ? forumLimited.getLimitedTime() : 0;
	}

	public ForumLimitedTime getForumLimitedTime(Forum forum) {
		return (ForumLimitedTime) this.session().createQuery("from ForumLimitedTime f where f.forum = :forum")
				.setParameter("forum", forum)
				.setMaxResults(1).uniqueResult();
	}

	/**
	 * @see net.jforum.plugins.post.ForumLimitedTimeRepository#saveOrUpdate(net.jforum.plugins.post.ForumLimitedTime)
	 */
	public void saveOrUpdate(ForumLimitedTime fourmLimitedTime) {
		this.session().saveOrUpdate(fourmLimitedTime);
	}
}

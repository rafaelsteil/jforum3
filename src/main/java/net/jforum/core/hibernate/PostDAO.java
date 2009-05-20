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

import net.jforum.entities.Post;
import net.jforum.repository.PostRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class PostDAO extends HibernateGenericDAO<Post> implements PostRepository {
	public PostDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.PostRepository#countPreviousPosts(int)
	 */
	public int countPreviousPosts(int postId) {
		return ((Long)this.session().createQuery("select count(*) from Post p " +
			"where p.topic = (select p2.topic from Post p2 where p2.id = :id) and p.id <= :id")
			.setParameter("id", postId)
			.setComment("postDAO.countPreviousPosts")
			.uniqueResult()).intValue();
	}
}

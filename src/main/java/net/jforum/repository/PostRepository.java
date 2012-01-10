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

import net.jforum.entities.Post;

import org.hibernate.Session;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PostRepository extends HibernateGenericDAO<Post> implements Repository<Post> {
	public PostRepository(Session session) {
		super(session);
	}

	public int countPreviousPosts(int postId) {
		return ((Long)session.createQuery("select count(*) from Post p " +
			"where p.topic = (select p2.topic from Post p2 where p2.id = :id) and p.id <= :id")
			.setParameter("id", postId)
			.setComment("postDAO.countPreviousPosts")
			.uniqueResult()).intValue();
	}
}

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

import net.jforum.entities.Session;
import net.jforum.repository.SessionRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class SessionDAO extends HibernateGenericDAO<Session> implements SessionRepository {
	public SessionDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * Always execute saveOrUpdate().
	 */
	@Override
	public void add(Session entity) {
		this.session().saveOrUpdate(entity);
	}

	/**
	 * Always execute saveOrUpdate()
	 */
	@Override
	public void update(Session entity) {
		this.session().saveOrUpdate(entity);
	}
}

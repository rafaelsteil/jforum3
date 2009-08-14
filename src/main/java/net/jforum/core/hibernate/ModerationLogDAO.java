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

import net.jforum.entities.ModerationLog;
import net.jforum.repository.ModerationLogRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class ModerationLogDAO extends HibernateGenericDAO<ModerationLog> implements ModerationLogRepository {
	public ModerationLogDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.ModerationLogRepository#getTotalRecords()
	 */
	public int getTotalRecords() {
		return ((Number)this.session().createQuery("select count(*) from ModerationLog").uniqueResult()).intValue();
	}

	/**
	 * @see net.jforum.repository.ModerationLogRepository#getAll(int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<ModerationLog> getAll(int start, int count) {
		return this.session().createQuery("from ModerationLog l order by l.id desc")
			.setFirstResult(start)
			.setMaxResults(count)
			.list();
	}
}

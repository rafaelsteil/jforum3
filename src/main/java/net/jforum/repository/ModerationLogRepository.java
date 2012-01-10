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

import net.jforum.entities.ModerationLog;

import org.hibernate.Session;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class ModerationLogRepository extends HibernateGenericDAO<ModerationLog> implements Repository<ModerationLog> {
	public ModerationLogRepository(Session session) {
		super(session);
	}

	public int getTotalRecords() {
		return ((Number)session.createQuery("select count(*) from ModerationLog").uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<ModerationLog> getAll(int start, int count) {
		return session.createQuery("from ModerationLog l order by l.id desc")
			.setFirstResult(start)
			.setMaxResults(count)
			.list();
	}
}

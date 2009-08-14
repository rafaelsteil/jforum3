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

import net.jforum.entities.Banlist;
import net.jforum.repository.BanlistRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class BanlistDAO extends HibernateGenericDAO<Banlist> implements BanlistRepository {
	public BanlistDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.BanlistRepository#getAllBanlists()
	 */
	@SuppressWarnings("unchecked")
	public List<Banlist> getAllBanlists() {
		return this.session().createCriteria(this.persistClass).list();
	}
}

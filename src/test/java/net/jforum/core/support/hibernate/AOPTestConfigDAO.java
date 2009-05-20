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
package net.jforum.core.support.hibernate;

import java.util.List;

import net.jforum.core.hibernate.HibernateGenericDAO;
import net.jforum.entities.Config;
import net.jforum.repository.ConfigRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class AOPTestConfigDAO extends HibernateGenericDAO<Config> implements ConfigRepository {
	/**
	 * @param sessionFactory
	 */
	public AOPTestConfigDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.core.hibernate.HibernateGenericDAO#add(java.lang.Object)
	 */
	@Override
	public void add(Config entity) {
	}

	/**
	 * @see net.jforum.core.hibernate.HibernateGenericDAO#update(java.lang.Object)
	 */
	@Override
	public void update(Config entity) {
	}

	/**
	 * @see net.jforum.repository.ConfigRepository#getByName(java.lang.String)
	 */
	public Config getByName(String configName) {
		return null;
	}

	/**
	 * @see net.jforum.repository.ConfigRepository#getAll()
	 */
	public List<Config> getAll() {
		return null;
	}
}

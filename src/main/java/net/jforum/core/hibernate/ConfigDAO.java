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

import net.jforum.entities.Config;
import net.jforum.repository.ConfigRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * @author Rafael Steil
 * @author Jose Donizetti de Brito Junior
 */
public class ConfigDAO extends HibernateGenericDAO<Config> implements ConfigRepository {

	public ConfigDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.core.hibernate.HibernateGenericDAO#update(java.lang.Object)
	 */
	@Override
	public void update(Config entity) {
		this.session().saveOrUpdate(entity);
	}

	/**
	 * @see net.jforum.repository.ConfigRepository#getByName(java.lang.String)
	 */
	public Config getByName(String configName) {
		return (Config)this.session().createCriteria(persistClass)
			.add(Restrictions.eq("name", configName))
			.setCacheable(true)
			.setCacheRegion("configDAO")
			.setComment("configDAO.getByName")
			.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Config> getAll() {
		return this.session().createCriteria(persistClass).list();
	}
}

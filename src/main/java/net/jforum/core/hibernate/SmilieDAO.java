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

import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class SmilieDAO extends HibernateGenericDAO<Smilie> implements SmilieRepository {
	public SmilieDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.SmilieRepository#getAllSmilies()
	 */
	@SuppressWarnings("unchecked")
	public List<Smilie> getAllSmilies() {
		return this.session().createCriteria(this.persistClass)
			.setCacheable(true)
			.setCacheRegion("smilieDAO")
			.setComment("smilieDAO.getAllSmilies")
			.list();
	}
}

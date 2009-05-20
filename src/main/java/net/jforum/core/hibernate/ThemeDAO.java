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

import net.jforum.entities.Theme;
import net.jforum.repository.ThemeRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class ThemeDAO extends HibernateGenericDAO<Theme> implements ThemeRepository {
	public ThemeDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.ThemeRepository#getAll()
	 */
	@SuppressWarnings("unchecked")
	public List<Theme> getAll() {
		return this.session().createCriteria(Theme.class).list();
	}
}

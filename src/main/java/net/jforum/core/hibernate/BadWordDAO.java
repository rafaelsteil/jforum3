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

import net.jforum.entities.BadWord;
import net.jforum.repository.BadWordRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class BadWordDAO extends HibernateGenericDAO<BadWord> implements BadWordRepository {

	public BadWordDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.BadWordRepository#getAll()
	 */
	@SuppressWarnings("unchecked")
	public List<BadWord> getAll() {
		return this.session().createCriteria(BadWord.class).list();
	}
}

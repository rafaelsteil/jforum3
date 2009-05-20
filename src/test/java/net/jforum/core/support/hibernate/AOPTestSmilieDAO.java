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
import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class AOPTestSmilieDAO extends HibernateGenericDAO<Smilie> implements SmilieRepository {

	public AOPTestSmilieDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.SmilieRepository#getAllSmilies()
	 */
	public List<Smilie> getAllSmilies() {
		return null;
	}

	/**
	 * @see net.jforum.repository.Repository#add(java.lang.Object)
	 */
	@Override
	public void add(Smilie entity) {
	}

	/**
	 * @see net.jforum.repository.Repository#get(int)
	 */
	@Override
	public Smilie get(int id) {
		return null;
	}

	/**
	 * @see net.jforum.repository.Repository#remove(java.lang.Object)
	 */
	@Override
	public void remove(Smilie entity) {
	}

	/**
	 * @see net.jforum.repository.Repository#update(java.lang.Object)
	 */
	@Override
	public void update(Smilie entity) {
	}

}

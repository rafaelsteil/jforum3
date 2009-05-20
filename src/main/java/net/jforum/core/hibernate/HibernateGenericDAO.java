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

import java.lang.reflect.ParameterizedType;

import net.jforum.repository.Repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class HibernateGenericDAO<T> implements Repository<T> {
	protected Class<T> persistClass;
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public HibernateGenericDAO(SessionFactory sessionFactory) {
		this.persistClass = (Class<T>)((ParameterizedType)this.getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Returns the session for the current executing context
	 * @return the session
	 */
	protected Session session() {
		return this.sessionFactory.getCurrentSession();
	}

	/**
	 *
	 * @see net.jforum.repository.Repository#remove(java.lang.Object)
	 */
	public void remove(T entity) {
		this.session().delete(entity);
	}

	/**
	 * @see net.jforum.repository.Repository#get(int)
	 */
	@SuppressWarnings("unchecked")
	public T get(int id) {
		return (T)this.session().get(this.persistClass, id);
	}

	/**
	 * @see net.jforum.repository.Repository#add(java.lang.Object)
	 */
	public void add(T entity) {
		this.session().save(entity);
	}

	/**
	 * @see net.jforum.repository.Repository#update(java.lang.Object)
	 */
	public void update(T entity) {
		this.session().update(entity);
	}
}

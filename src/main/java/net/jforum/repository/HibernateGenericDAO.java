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

import java.lang.reflect.ParameterizedType;

import org.hibernate.Session;

/**
 * @author Rafael Steil
 */
public class HibernateGenericDAO<T> implements Repository<T> {
	protected Class<T> persistClass;
	protected final Session session;

	@SuppressWarnings("unchecked")
	public HibernateGenericDAO(Session session) {
		this.session = session;
		this.persistClass = (Class<T>)((ParameterizedType)this.getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 *
	 * @see net.jforum.repository.Repository#remove(java.lang.Object)
	 */
	@Override
	public void remove(T entity) {
		session.delete(entity);
	}

	/**
	 * @see net.jforum.repository.Repository#get(int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T get(int id) {
		return (T)session.get(this.persistClass, id);
	}

	/**
	 * @see net.jforum.repository.Repository#add(java.lang.Object)
	 */
	@Override
	public void add(T entity) {
		session.save(entity);
	}

	/**
	 * @see net.jforum.repository.Repository#update(java.lang.Object)
	 */
	@Override
	public void update(T entity) {
		session.update(entity);
	}
}

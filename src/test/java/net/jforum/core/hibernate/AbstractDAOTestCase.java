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


import net.jforum.repository.HibernateGenericDAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

/**
 * @author Rafael Steil
 */
public abstract class AbstractDAOTestCase<T> {
	private Session session;
	private SessionFactory sessionFactory;

	/**
	 * Persists an entity.
	 * After inserting, the transaction will be commited and
	 * the session cleared.
	 * @param entity the entity to persist
	 * @param dao the dao to use
	 */
	protected void insert(T entity, HibernateGenericDAO<T> dao) {
		dao.add(entity);
		commit();
		beginTransaction();
	}

	/**
	 * Updates an entity.
	 * After updating, the transaction will be commited and
	 * the session cleared.
	 * @param entity the entity to update
	 * @param dao the dao to use
	 */
	protected void update(T entity, HibernateGenericDAO<T> dao) {
		dao.update(entity);
		commit();
		beginTransaction();
	}

	/**
	 * Deletes an entity
	 * @param entity the entity to delete
	 * @param dao the dao to use
	 */
	protected void delete(T entity, HibernateGenericDAO<T> dao) {
		dao.remove(entity);
		commit();
		beginTransaction();
	}

	protected Session session() {
		return session;
	}

	/**
	 * First commits the transaction
	 */
	protected void commit() {
		session.getTransaction().commit();
	}

	@Before
	public void setUp() throws Exception {
		Configuration config = new AnnotationConfiguration();
		config.configure("/hibernate-tests.cfg.xml");
		sessionFactory = config.buildSessionFactory();
		session = sessionFactory.openSession();
		beginTransaction();
	}

	protected Transaction beginTransaction() {
		return session.beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		if(session != null) {
			session.close();
		}
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
}

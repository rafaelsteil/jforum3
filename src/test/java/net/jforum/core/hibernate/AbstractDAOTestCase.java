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
	protected SessionFactory sessionFactory;

	/**
	 * Persists an entity.
	 * After inserting, the transaction will be commited and
	 * the session cleared.
	 * @param entity the entity to persist
	 * @param dao the dao to use
	 */
	protected void insert(T entity, HibernateGenericDAO<T> dao) {
		dao.add(entity);
		this.commit();
		this.beginTransaction();
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
		this.commit();
		this.beginTransaction();
	}

	/**
	 * Deletes an entity
	 * @param entity the entity to delete
	 * @param dao the dao to use
	 */
	protected void delete(T entity, HibernateGenericDAO<T> dao) {
		dao.remove(entity);
		this.commit();
		this.beginTransaction();
	}

	protected Session session() {
		return this.sessionFactory.getCurrentSession();
	}

	/**
	 * First commits the transaction, and then clears the session
	 */
	protected void commit() {
		this.sessionFactory.getCurrentSession().getTransaction().commit();
	}

	@Before
	public void setUp() throws Exception {
		Configuration config = new AnnotationConfiguration();
		config.configure("/hibernate-tests.cfg.xml");
		this.sessionFactory = config.buildSessionFactory();
		this.beginTransaction();
	}

	protected Transaction beginTransaction() {
		return this.sessionFactory.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		if (this.sessionFactory != null) {
			this.sessionFactory.close();
		}
	}
}

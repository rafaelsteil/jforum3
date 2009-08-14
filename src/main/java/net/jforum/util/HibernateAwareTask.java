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
package net.jforum.util;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Rafael Steil
 */
public class HibernateAwareTask {
	private final SessionFactory sessionFactory;

	public HibernateAwareTask(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void execute(HibernateRunnable runnable) {
		try {
			TransactionSynchronizationManager.bindResource(this.sessionFactory,
				new SessionHolder(this.sessionFactory.openSession()));
			this.sessionFactory.getCurrentSession().beginTransaction();

			runnable.run();

			this.sessionFactory.getCurrentSession().getTransaction().commit();
		}
		finally {
			TransactionSynchronizationManager.unbindResource(this.sessionFactory);
		}
	}
}

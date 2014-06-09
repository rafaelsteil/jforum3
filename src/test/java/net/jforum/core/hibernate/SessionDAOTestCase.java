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

import static org.junit.Assert.*;
import net.jforum.entities.Session;
import net.jforum.repository.SessionRepository;

import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class SessionDAOTestCase extends AbstractDAOTestCase<Session> {
	@Test
	public void addNew() {
		SessionRepository dao = this.newDao();
		assertNull(dao.get(1));
		this.insert(this.newSession(1), dao);
		assertNotNull(dao.get(1));
	}

	@Test
	public void updateDoesNotExistShouldAdd() {
		SessionRepository dao = this.newDao();
		assertNull(dao.get(1));
		this.update(this.newSession(1), dao);
		assertNotNull(dao.get(1));
	}

	private Session newSession(int userId) {
		Session session = new Session();

		session.setUserId(userId);

		return session;
	}

	private SessionRepository newDao() {
		return new SessionRepository(session());
	}
}

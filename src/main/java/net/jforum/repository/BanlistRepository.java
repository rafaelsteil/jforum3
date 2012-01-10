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

import java.util.List;

import net.jforum.entities.Banlist;

import org.hibernate.Session;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class BanlistRepository extends HibernateGenericDAO<Banlist> implements Repository<Banlist> {
	public BanlistRepository(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Banlist> getAllBanlists() {
		return session.createCriteria(this.persistClass).list();
	}
}

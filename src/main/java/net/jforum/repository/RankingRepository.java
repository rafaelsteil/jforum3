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

import net.jforum.entities.Ranking;

import org.hibernate.Session;
import org.hibernate.criterion.Order;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class RankingRepository extends HibernateGenericDAO<Ranking> implements Repository<Ranking> {
	public RankingRepository(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Ranking> getAllRankings() {
		return session.createCriteria(this.persistClass)
			.addOrder(Order.asc("min"))
			.setCacheable(true)
			.setCacheRegion("rankingDAO")
			.setComment("rankingDAO.getAllRankings")
			.list();
	}
}

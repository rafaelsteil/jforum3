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

import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

/**
 * @author Rafael Steil
 */
public class RankingDAO extends HibernateGenericDAO<Ranking> implements RankingRepository {
	public RankingDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.RankingRepository#getAllRankings()
	 */
	@SuppressWarnings("unchecked")
	public List<Ranking> getAllRankings() {
		return this.session().createCriteria(persistClass)
			.addOrder(Order.asc("min"))
			.setCacheable(true)
			.setCacheRegion("rankingDAO")
			.setComment("rankingDAO.getAllRankings")
			.list();
	}
}

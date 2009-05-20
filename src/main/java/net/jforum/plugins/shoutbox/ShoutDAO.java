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
package net.jforum.plugins.shoutbox;

import java.util.List;

import net.jforum.core.hibernate.HibernateGenericDAO;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

/**
 * @author Bill
 *
 */
public class ShoutDAO extends HibernateGenericDAO<Shout> implements
		ShoutRepository {

	public ShoutDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Shout> getAll() {
		Query query = getAllShoutsQuery();
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Shout> getAll(ShoutBox shoutBox) {
		Query query = getAllShoutsQuery(shoutBox);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Shout> getAll(int start, int count) {
		Query query = getAllShoutsQuery();
		query.setFirstResult(start);
		query.setMaxResults(count);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Shout> getAll(ShoutBox shoutBox, int start, int count) {
		Query query = getAllShoutsQuery(shoutBox);
		query.setFirstResult(start);
		query.setMaxResults(count);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Shout> getShout(int lastId,ShoutBox shoutBox, int maxResults) {

		Query query = this.session()
				.createQuery("from Shout as shout where shout.id > :lastId and shout.shoutBox = :shoutBox" +
						" order by shout.id DESC")
				.setInteger("lastId", lastId)
				.setParameter("shoutBox", shoutBox)
				.setCacheable(false);

		if(maxResults > 0)
				query.setMaxResults(maxResults);

		return query.list();
	}

	/**
	 * get last Shout of same IP
	 */
	public Shout getMyLastShout(String shouterIp) {
		Query query = this.session()
		.createQuery("from Shout as shout where shout.shouterIp = :shouterIp" +
				" order by shout.id DESC")
		.setString("shouterIp", shouterIp)
		.setMaxResults(1);

		return (Shout) query.uniqueResult();
	}

	//------------- Private Methods ---------------------------

	private Query getAllShoutsQuery (){
		return this.session().createQuery("from Shout order by shoutTime");
	}

	private Query getAllShoutsQuery(ShoutBox shoutBox){
		return this.session().createQuery("from Shout where shoutBox = :shoutBox order by shoutTime")
				.setParameter("shoutBox", shoutBox);
	}
}

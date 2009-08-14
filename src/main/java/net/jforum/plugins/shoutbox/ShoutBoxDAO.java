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
import net.jforum.entities.Category;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * @author Bill
 */
public class ShoutBoxDAO extends HibernateGenericDAO<ShoutBox> implements
		ShoutBoxRepository {

	public ShoutBoxDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public List<ShoutBox> getAvalibleBoxes(boolean isAnonymous) {
		return this.session().createCriteria(this.persistClass)
		.add(Restrictions.eq("isAllowAnonymous", isAnonymous))
		.add(Restrictions.eq("isDisabled", false))
		.setCacheable(true)
		.setCacheRegion("ShoutBoxDAO")
		.setComment("ShoutBoxDAO.getAvalibleBox")
		.list();
	}

	@Override
	public void remove(ShoutBox entity) {
		//Remove all the shouts that belongs to this shoutBox
		this.session().createQuery("delete from Shout as shout where shout.shoutBox = :shoutBox")
					  .setParameter("shoutBox", entity)
					  .setComment("ShoutBoxDAO.remove(ShoutBox)")
					  .executeUpdate();

		//then remove the shoutbox
		super.remove(entity);
	}

	public ShoutBox getShoutBox(Category category) {
		return (ShoutBox) this.session().createCriteria(this.persistClass)
		.add(Restrictions.eq("category", category))
		.setCacheable(true)
		.setCacheRegion("ShoutBoxDAO")
		.setComment("ShoutBoxDAO.getShoutBox")
		.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<ShoutBox> getAllShoutBoxes() {
		return this.session().createQuery("from ShoutBox order by id").list();
	}

}

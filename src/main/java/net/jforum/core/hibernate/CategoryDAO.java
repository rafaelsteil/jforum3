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

import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.repository.CategoryRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * @author Rafael Steil
 */
public class CategoryDAO extends HibernateGenericDAO<Category> implements CategoryRepository {
	public CategoryDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.CategoryRepository#getForums(net.jforum.entities.Category)
	 */
	@SuppressWarnings("unchecked")
	public List<Forum> getForums(Category category) {
		return this.session().createCriteria(Forum.class)
			.add(Restrictions.eq("category", category))
			.addOrder(Order.asc("displayOrder"))
			.setCacheable(true)
			.setCacheRegion("categoryDAO.getForums")
			.setComment("categoryDAO.getForums")
			.list();
	}

	/**
	 * @see net.jforum.core.hibernate.HibernateGenericDAO#add(java.lang.Object)
	 */
	@Override
	public void add(Category entity) {
		entity.setDisplayOrder(this.getMaxDisplayOrder());
		super.add(entity);
	}

	/**
	 * @see net.jforum.repository.CategoryRepository#getAllCategories()
	 */
	@SuppressWarnings("unchecked")
	public List<Category> getAllCategories() {
		return this.session().createCriteria(this.persistClass)
			.addOrder(Order.asc("displayOrder"))
			.setCacheable(true)
			.setCacheRegion("categoryDAO.getAllCategories")
			.setComment("categoryDAO.getAllCategories")
			.list();
	}

	private int getMaxDisplayOrder() {
		Integer displayOrder = (Integer)this.session().createCriteria(this.persistClass)
			.setProjection(Projections.max("displayOrder"))
			.uniqueResult();

		return displayOrder == null ? 1 : displayOrder + 1;
	}
}

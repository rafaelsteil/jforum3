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

import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.repository.GroupRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * @author Rafael Steil
 */
public class GroupDAO extends HibernateGenericDAO<Group> implements GroupRepository {
	public GroupDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.GroupRepository#getAllGroups()
	 */
	@SuppressWarnings("unchecked")
	public List<Group> getAllGroups() {
		return this.session().createCriteria(this.persistClass).list();
	}

	/**
	 * @see net.jforum.repository.GroupRepository#getByName(java.lang.String)
	 */
	public Group getByName(String groupName) {
		return (Group) this.session().createCriteria(this.persistClass)
			   .add(Restrictions.eq("name", groupName))
			   .uniqueResult();
	}

	/**
	 * @see net.jforum.core.hibernate.HibernateGenericDAO#remove(java.lang.Object)
	 */
	@Override
	public void remove(Group group) {
		List<User> users = group.getUsers();
		for(User user : users){
			List<Group> groups = user.getGroups();
			groups.remove(group);
			this.session().save(user);
		}
		super.remove(group);
	}
	
	
}

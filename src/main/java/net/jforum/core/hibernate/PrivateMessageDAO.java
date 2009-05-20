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

import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.User;
import net.jforum.repository.PrivateMessageRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * @author Rafael Steil
 */
public class PrivateMessageDAO extends HibernateGenericDAO<PrivateMessage> implements PrivateMessageRepository {
	/**
	 * @param sessionFactory
	 */
	public PrivateMessageDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.core.hibernate.HibernateGenericDAO#add(java.lang.Object)
	 */
	@Override
	public void add(PrivateMessage entity) {
		PrivateMessage targetCopy = new PrivateMessage(entity);

		// First copy is to the sender's list
		entity.setType(PrivateMessageType.SENT);
		super.add(entity);

		// Second copy is the target
		targetCopy.setType(PrivateMessageType.NEW);
		super.add(targetCopy);
	}

	/**
	 * @see net.jforum.repository.PrivateMessageRepository#getFromInbox(net.jforum.entities.User)
	 */
	@SuppressWarnings("unchecked")
	public List<PrivateMessage> getFromInbox(User user) {
		return this.session().createCriteria(persistClass)
			.add(Restrictions.eq("toUser", user))
			.add(Restrictions.disjunction()
				.add(Restrictions.eq("type", PrivateMessageType.NEW))
				.add(Restrictions.eq("type", PrivateMessageType.READ))
				.add(Restrictions.eq("type", PrivateMessageType.UNREAD))
			)
			.setComment("privateMessageDAO.getFromInbox")
			.list();
	}

	/**
	 * @see net.jforum.repository.PrivateMessageRepository#getFromSentBox(net.jforum.entities.User)
	 */
	@SuppressWarnings("unchecked")
	public List<PrivateMessage> getFromSentBox(User user) {
		return this.session().createCriteria(persistClass)
			.add(Restrictions.eq("fromUser", user))
			.add(Restrictions.eq("type", PrivateMessageType.SENT))
			.setComment("privateMessageDAO.getFromSentBox")
			.list();
	}

	/**
	 * This method will always throw UnsupportedOperationException
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void update(PrivateMessage entity) {
		throw new UnsupportedOperationException("Update is not supported for Private Messages");
	}
}

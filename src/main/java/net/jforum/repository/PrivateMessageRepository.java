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

import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.User;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PrivateMessageRepository extends HibernateGenericDAO<PrivateMessage> implements Repository<PrivateMessage> {
	public PrivateMessageRepository(Session session) {
		super(session);
	}

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

	@SuppressWarnings("unchecked")
	public List<PrivateMessage> getFromInbox(User user) {
		return session.createCriteria(this.persistClass)
			.add(Restrictions.eq("toUser", user))
			.add(Restrictions.disjunction()
				.add(Restrictions.eq("type", PrivateMessageType.NEW))
				.add(Restrictions.eq("type", PrivateMessageType.READ))
				.add(Restrictions.eq("type", PrivateMessageType.UNREAD))
			)
			.setComment("privateMessageDAO.getFromInbox")
			.list();
	}

	@SuppressWarnings("unchecked")
	public List<PrivateMessage> getFromSentBox(User user) {
		return session.createCriteria(this.persistClass)
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

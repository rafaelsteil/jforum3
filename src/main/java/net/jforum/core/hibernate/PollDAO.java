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

import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;
import net.jforum.entities.PollVoter;
import net.jforum.entities.User;
import net.jforum.repository.PollRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class PollDAO implements PollRepository {
	private final SessionFactory sessionFactory;

	public PollDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}

	/**
	 * @see net.jforum.repository.PollRepository#registerVote(net.jforum.entities.PollVoter)
	 */
	public void registerVote(PollVoter voter) {
		this.session().save(voter);
	}

	private Session session() {
		return this.sessionFactory.getCurrentSession();
	}

	/**
	 * @see net.jforum.repository.PollRepository#hasUserVoted(net.jforum.entities.Poll, net.jforum.entities.User)
	 */
	public boolean hasUserVoted(Poll poll, User user) {
		return this.session().createQuery("from PollVoter voter where voter.user = :user and voter.poll = :poll")
			.setParameter("user", user)
			.setParameter("poll", poll)
			.uniqueResult() != null;
	}

	/**
	 * @see net.jforum.repository.PollRepository#getOption(int)
	 */
	public PollOption getOption(int optionId) {
		return (PollOption)this.session().createQuery("from PollOption o where o.id = :id")
			.setParameter("id", optionId)
			.uniqueResult();
	}
}

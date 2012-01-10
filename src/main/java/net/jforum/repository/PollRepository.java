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

import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;
import net.jforum.entities.PollVoter;
import net.jforum.entities.User;

import org.hibernate.Session;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PollRepository extends HibernateGenericDAO<PollVoter> {
	public PollRepository(Session session) {
		super(session);
	}

	public void registerVote(PollVoter voter) {
		session.save(voter);
	}

	public boolean hasUserVoted(Poll poll, User user) {
		return session.createQuery("from PollVoter voter where voter.user = :user and voter.poll = :poll")
			.setParameter("user", user)
			.setParameter("poll", poll)
			.uniqueResult() != null;
	}

	public PollOption getOption(int optionId) {
		return (PollOption)session.createQuery("from PollOption o where o.id = :id")
			.setParameter("id", optionId)
			.uniqueResult();
	}
}

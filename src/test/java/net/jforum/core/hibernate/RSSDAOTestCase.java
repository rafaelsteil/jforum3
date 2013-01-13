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

import junit.framework.Assert;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.repository.RSSRepository;
import net.jforum.util.JDBCLoader;

import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RSSDAOTestCase extends AbstractDAOTestCase<Object> {
	@Test
	@SuppressWarnings("deprecation")
	public void getForumTopics() {
		new JDBCLoader(session().connection())
			.run("/rssdao/getForumTopics.sql");

		RSSRepository dao = this.newDao();
		Forum forum = new Forum(); forum.setId(1);
		List<Topic> topics = dao.getForumTopics(forum, 10);

		Assert.assertEquals(3, topics.size());

		Assert.assertEquals(2, topics.get(0).getId());
		Assert.assertEquals(1, topics.get(1).getId());
		Assert.assertEquals(3, topics.get(2).getId());
	}

	private RSSRepository newDao() {
		return new RSSRepository(session());
	}
}

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

import net.jforum.entities.Topic;
import net.jforum.repository.RecentTopicsRepository;
import net.jforum.util.JDBCLoader;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RecentTopicsDAOTestCase extends AbstractDAOTestCase<Topic> {
	@Test
	public void expectZeroResultsShouldReturnEmptyList() {
		RecentTopicsRepository dao = this.newDao();
		List<Topic> list = dao.getNewTopics(10);
		Assert.assertEquals(0, list.size());

		dao = this.newDao();
		list = dao.getUpdatedTopics(10);
		Assert.assertEquals(0, list.size());

		dao = this.newDao();
		list = dao.getHotTopics(10);
		Assert.assertEquals(0, list.size());
	}


	@Test
	public void postsInModerationExpectOneResult() {
		this.loadDump("/recenttopics/dump.sql");
		this.loadDump("/recenttopics/moderation.sql");

		RecentTopicsRepository dao = this.newDao();
		List<Topic> list = dao.getNewTopics(10);

		Assert.assertEquals(1, list.size());
		Assert.assertEquals(6, list.get(0).getId());
	}

	@Test
	public void recentTopics() {
		this.loadDump("/recenttopics/dump.sql");

		RecentTopicsRepository dao = this.newDao();
		List<Topic> list = dao.getNewTopics(10);
		Assert.assertEquals(8, list.size());

		Assert.assertEquals(8, list.get(0).getId());
		Assert.assertEquals(7, list.get(1).getId());
		Assert.assertEquals(6, list.get(2).getId());
		Assert.assertEquals(5, list.get(3).getId());
		Assert.assertEquals(4, list.get(4).getId());
		Assert.assertEquals(3, list.get(5).getId());
		Assert.assertEquals(2, list.get(6).getId());
		Assert.assertEquals(1, list.get(7).getId());

		Assert.assertEquals(4, list.get(0).getFirstPost().getId());
		Assert.assertEquals(12, list.get(0).getLastPost().getId());
		Assert.assertEquals(2, list.get(0).getFirstPost().getUser().getId());
		Assert.assertEquals(2, list.get(0).getLastPost().getUser().getId());
		Assert.assertEquals(3, list.get(0).getForum().getId());

		Assert.assertEquals(3, list.get(1).getFirstPost().getId());
		Assert.assertEquals(11, list.get(1).getLastPost().getId());
		Assert.assertEquals(2, list.get(1).getFirstPost().getUser().getId());
		Assert.assertEquals(2, list.get(1).getLastPost().getUser().getId());
		Assert.assertEquals(2, list.get(1).getForum().getId());

		Assert.assertEquals(2, list.get(2).getFirstPost().getId());
		Assert.assertEquals(10, list.get(2).getLastPost().getId());
		Assert.assertEquals(1, list.get(2).getFirstPost().getUser().getId());
		Assert.assertEquals(1, list.get(2).getLastPost().getUser().getId());
		Assert.assertEquals(1, list.get(2).getForum().getId());

		Assert.assertEquals(1, list.get(3).getFirstPost().getId());
		Assert.assertEquals(9, list.get(3).getLastPost().getId());
		Assert.assertEquals(1, list.get(3).getFirstPost().getUser().getId());
		Assert.assertEquals(2, list.get(3).getLastPost().getUser().getId());
		Assert.assertEquals(3, list.get(3).getForum().getId());

		Assert.assertEquals(8, list.get(4).getFirstPost().getId());
		Assert.assertEquals(8, list.get(4).getLastPost().getId());
		Assert.assertEquals(1, list.get(4).getFirstPost().getUser().getId());
		Assert.assertEquals(1, list.get(4).getLastPost().getUser().getId());
		Assert.assertEquals(3, list.get(4).getForum().getId());

		Assert.assertEquals(7, list.get(5).getFirstPost().getId());
		Assert.assertEquals(7, list.get(5).getLastPost().getId());
		Assert.assertEquals(2, list.get(5).getFirstPost().getUser().getId());
		Assert.assertEquals(2, list.get(5).getLastPost().getUser().getId());
		Assert.assertEquals(2, list.get(5).getForum().getId());

		Assert.assertEquals(6, list.get(6).getFirstPost().getId());
		Assert.assertEquals(6, list.get(6).getLastPost().getId());
		Assert.assertEquals(1, list.get(6).getFirstPost().getUser().getId());
		Assert.assertEquals(1, list.get(6).getLastPost().getUser().getId());
		Assert.assertEquals(1, list.get(6).getForum().getId());

		Assert.assertEquals(5, list.get(7).getFirstPost().getId());
		Assert.assertEquals(5, list.get(7).getLastPost().getId());
		Assert.assertEquals(1, list.get(7).getFirstPost().getUser().getId());
		Assert.assertEquals(1, list.get(7).getLastPost().getUser().getId());
		Assert.assertEquals(1, list.get(7).getForum().getId());
	}

	@SuppressWarnings("deprecation")
	private void loadDump(String file) {
		session().beginTransaction();
		JDBCLoader loader = new JDBCLoader(session());
		loader.run(file);
		session().getTransaction().commit();
		session().beginTransaction();
	}

	private RecentTopicsRepository newDao() {
		return new RecentTopicsRepository(session());
	}
}

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
package net.jforum.plugins.post;

import net.jforum.core.hibernate.AbstractDAOTestCase;
import net.jforum.entities.Forum;
import net.jforum.util.JDBCLoader;

import org.junit.Assert;
import org.junit.Test;

public class FourmLimitedTimeDAOTestCase extends AbstractDAOTestCase<ForumLimitedTime> {
	@Test
	@SuppressWarnings("deprecation")
	public void getFourmLimitedTime() {
		new JDBCLoader(session())
			.run("/posteditlimited/dump.sql");

		ForumLimitedTimeRepository dao = this.newFourmLimitedTimeDAO();
		Forum forum = new Forum();
		forum.setId(2);

		ForumLimitedTime fourmLimitedTime = dao.getForumLimitedTime(forum);

		Assert.assertNotNull(fourmLimitedTime);
		Assert.assertEquals(4, fourmLimitedTime.getId());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getFourmLimitedTimeReturnNullIfNotFound() {
		new JDBCLoader(session())
			.run("/posteditlimited/dump.sql");

		ForumLimitedTimeRepository dao = this.newFourmLimitedTimeDAO();
		Forum forum = new Forum();
		forum.setId(5);

		ForumLimitedTime fourmLimitedTime = dao.getForumLimitedTime(forum);

		Assert.assertNull(fourmLimitedTime);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getLimitedTime() {
		new JDBCLoader(session())
		.run("/posteditlimited/dump.sql");

		ForumLimitedTimeRepository dao = this.newFourmLimitedTimeDAO();
		Forum forum = new Forum();
		forum.setId(1);

		long limitedTime = dao.getLimitedTime(forum);

		Assert.assertEquals(125, limitedTime);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getLimitedTimeReturn0IfNotFound() {
		new JDBCLoader(session())
		.run("/posteditlimited/dump.sql");

		ForumLimitedTimeRepository dao = this.newFourmLimitedTimeDAO();
		Forum forum = new Forum();
		forum.setId(5);

		long limitedTime = dao.getLimitedTime(forum);

		Assert.assertEquals(0, limitedTime);
	}

	private ForumLimitedTimeRepository newFourmLimitedTimeDAO() {
		return new ForumLimitedTimeRepository(session());
	}
}

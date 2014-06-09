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

import net.jforum.entities.Post;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.repository.UserRepository;
import net.jforum.util.JDBCLoader;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PostDAOTestCase extends AbstractDAOTestCase<Post> {
	@Test
	@SuppressWarnings("deprecation")
	public void countPreviousPostsShouldReturn6() {
		new JDBCLoader(session())
			.run("/postdao/countPreviousPosts.sql");
		this.commit();
		this.beginTransaction();

		PostRepository dao = this.newDao();
		int total = dao.countPreviousPosts(6);
		Assert.assertEquals(6, total);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void countPreviousPostsShouldReturn3() {
		new JDBCLoader(session())
			.run("/postdao/countPreviousPosts.sql");
		this.commit();
		this.beginTransaction();

		PostRepository dao = this.newDao();
		int total = dao.countPreviousPosts(3);
		Assert.assertEquals(3, total);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void add() {
		new JDBCLoader(session()) .run("/postdao/dump.sql");

		PostRepository dao = this.newDao();
		Post post = this.newPost();
		this.insert(post, dao);

		Assert.assertTrue(post.getId() > 0);
	}

	private Post newPost() {
		Post post = new Post();
		post.setSubject("teste");
		post.setText("teste");
		post.setUser(this.newUserDao().get(1));
		post.setTopic(this.newTopicDao().get(1));

		return post;
	}

	private TopicRepository newTopicDao() {
		return new TopicRepository(session());
	}

	private UserRepository newUserDao() {
		return new UserRepository(session());
	}

	private PostRepository newDao() {
		return new PostRepository(session());
	}
}

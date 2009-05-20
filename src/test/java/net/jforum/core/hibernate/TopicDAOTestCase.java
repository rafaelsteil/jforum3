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

import java.util.Arrays;
import java.util.List;

import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.util.JDBCLoader;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class TopicDAOTestCase extends AbstractDAOTestCase<Topic> {
	@Test
	public void saveTopicWithPollExpectingCascadeToWork() {
		Topic t = new Topic(); t.setForum(null);

		Poll p = new Poll(); p.setLabel("poll1");
		PollOption o1 = new PollOption();
		o1.setPoll(p);
		o1.setText("o1");

		PollOption o2 = new PollOption();
		o2.setPoll(p);
		o2.setText("o2");

		p.setOptions(Arrays.asList(o1, o2));

		t.setPoll(p);

		TopicDAO dao = this.newTopicDao();
		this.insert(t, dao);

		t = dao.get(t.getId());
		Assert.assertNotNull(t.getPoll());
		Assert.assertEquals("poll1", t.getPoll().getLabel());
		Assert.assertEquals(2, t.getPoll().getOptions().size());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void removeShouldDeletePostsAndUpdateUserTotalMessages() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/topicdao/removeShouldDeletePostsAndUpdateUserTotalMessages.sql");

		TopicDAO dao = this.newTopicDao();
		Topic topic = dao.get(1);

		Assert.assertEquals(3, dao.getTotalPosts(topic));

		this.delete(topic, dao);

		Assert.assertEquals(0, dao.getTotalPosts(topic));

		UserDAO userDao = this.newUserDao();
		Assert.assertEquals(1, userDao.get(1).getTotalPosts());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getPostsShouldNotFetchModeratedExpectTwoResults() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection()) .run("/postdao/dump.sql");

		PostDAO postDao = this.newPostDao();
		Post post1 = this.newPost(); postDao.add(post1);
		Post post2 = this.newPost(); postDao.add(post2);
		Post post3 = this.newPost(); post3.setModerate(true); postDao.add(post3);

		TopicDAO topicDao = this.newTopicDao();
		Topic topic = topicDao.get(1);
		List<Post> posts = topicDao.getPosts(topic, 0, 10);

		Assert.assertEquals(2, posts.size());
		Assert.assertTrue(posts.contains(post1));
		Assert.assertTrue(posts.contains(post2));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void totalPostsExpectTwoResults() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/topicdao/totalPostsExpectTwoResults.sql");

		TopicDAO dao = this.newTopicDao();
		Topic topic = dao.get(1);
		Assert.assertEquals(2, dao.getTotalPosts(topic));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void lastPost() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/topicdao/firstLastPost.sql");

		TopicDAO dao = this.newTopicDao();
		Topic t = new Topic(); t.setId(1);
		Post expectedFirst = new Post(); expectedFirst.setId(2);

		Assert.assertEquals(expectedFirst, dao.getLastPost(t));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void firstPost() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/topicdao/firstLastPost.sql");

		TopicDAO dao = this.newTopicDao();
		Topic t = new Topic(); t.setId(1);
		Post expectedFirst = new Post(); expectedFirst.setId(1);

		Assert.assertEquals(expectedFirst, dao.getFirstPost(t));
	}

	private UserDAO newUserDao() {
		return new UserDAO(sessionFactory);
	}

	private PostDAO newPostDao() {
		return new PostDAO(sessionFactory);
	}

	private TopicDAO newTopicDao() {
		return new TopicDAO(sessionFactory);
	}

	private Post newPost() {
		Post post = new Post();
		post.setSubject("teste");
		post.setText("teste");
		post.setUser(this.newUserDao().get(1));
		post.setTopic(this.newTopicDao().get(1));

		return post;
	}
}

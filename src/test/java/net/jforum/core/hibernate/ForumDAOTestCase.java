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

import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.List;

import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.Group;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.util.PaginatedResult;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JDBCLoader;
import net.jforum.util.JForumConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ForumDAOTestCase extends AbstractDAOTestCase<Forum> {
	@Test
	@SuppressWarnings("deprecation")
	public void moveTopics() {
		new JDBCLoader(session())
			.run("/forumdao/moveTopics.sql");

		ForumRepository dao = this.newForumDao();
		Forum toForum = dao.get(2);

		Assert.assertEquals(1, dao.getTotalTopics(toForum));
		Assert.assertEquals(1, dao.getTotalPosts(toForum));

		dao.moveTopics(toForum, 1);

		Assert.assertEquals(2, dao.getTotalTopics(toForum));
		Assert.assertEquals(2, dao.getTotalPosts(toForum));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getNewMessages() {
		new JDBCLoader(session())
			.run("/forumdao/getNewMessages.sql");

		Calendar from = Calendar.getInstance();
		from.set(2008, 5, 11, 14, 50);

		ForumRepository dao = this.newForumDao();
		PaginatedResult<Topic> messages = dao.getNewMessages(from.getTime(), 0, 3);

		Assert.assertEquals(3, messages.getResults().size());
		Assert.assertEquals(5, messages.getTotalRecords());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getModerators() {
		new JDBCLoader(session())
			.run("/forumdao/getModerators.sql");

		Forum forum = new Forum(); forum.setId(1);
		List<Group> moderators = this.newForumDao().getModerators(forum);

		Assert.assertEquals(2, moderators.size());
		Assert.assertTrue(moderators.contains(new Group() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }}));
		Assert.assertTrue(moderators.contains(new Group() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(2); }}));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getLastPost() {
		new JDBCLoader(session())
			.run("/topicdao/firstLastPost.sql");

		ForumRepository dao = this.newForumDao();
		Forum f = new Forum(); f.setId(1);
		Post expectedFirst = new Post(); expectedFirst.setId(2);

		Assert.assertEquals(expectedFirst, dao.getLastPost(f));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getLastPostShouldIgnorePendingModerationPost() {
		new JDBCLoader(session())
			.run("/forumdao/getLastPostShouldIgnorePendingModerationPost.sql");

		ForumRepository dao = this.newForumDao();
		Forum forum = dao.get(1);
		Post expected = new Post(); expected.setId(2);
		Assert.assertEquals(expected, dao.getLastPost(forum));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getTopicsShouldIgnoreModeratedExpectThreeResults() {
		this.createGetTopicsPosts();

		new JDBCLoader(session())
			.run("/forumdao/getTopicsShouldIgnoreModeratedExpectThreeResults.sql");

		ForumRepository dao = this.newForumDao();
		Forum forum = dao.get(1);

		List<Topic> topics = dao.getTopics(forum, 0, 10);
		Assert.assertEquals(3, topics.size());

		Assert.assertEquals("t1.3", topics.get(0).getSubject());
		Assert.assertEquals("t1.2", topics.get(1).getSubject());
		Assert.assertEquals("t1.1", topics.get(2).getSubject());
	}

	@Test
	public void getTopicsPendingModerationExpectTwoResults() {
		this.createGetTopicsPosts();

		ForumRepository dao = this.newForumDao();
		Forum forum = dao.get(1);

		List<Topic> moderatedTopics = dao.getTopicsPendingModeration(forum);
		Assert.assertEquals(2, moderatedTopics.size());

		Assert.assertEquals("t1.1", moderatedTopics.get(0).getSubject());
		Assert.assertEquals("t1.2", moderatedTopics.get(1).getSubject());
		Assert.assertEquals(2, moderatedTopics.get(0).getPosts().size());
		Assert.assertEquals(1, moderatedTopics.get(1).getPosts().size());
	}

	@Test
	public void insertShouldIncrementDisplayOrder() {
		Forum f1 = this.newForum();
		Forum f2 = this.newForum();

		ForumRepository dao = this.newForumDao();

		this.insert(f1, dao);
		this.insert(f2, dao);

		Assert.assertEquals(1, f1.getDisplayOrder());
		Assert.assertEquals(2, f2.getDisplayOrder());
	}

	@Test
	public void getTotalMessage() {
		ForumRepository forumDao = this.newForumDao();
		PostRepository postDao = this.newPostDao();
		Post p = new Post(); p.setText("x"); p.setSubject("y");
		postDao.add(p);
		Assert.assertEquals(1, forumDao.getTotalMessages());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getTotalPosts() {
		new JDBCLoader(session())
			.run("/forumdao/getTotalPosts.sql");

		ForumRepository dao = this.newForumDao();
		Forum forum = dao.get(1);

		int totalPosts = dao.getTotalPosts(forum);
		Assert.assertEquals(2, totalPosts);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getTopicsShouldFetchFromForumAndFromMovedIdExpectTwoResults() {
		new JDBCLoader(session())
			.run("/forumdao/getTopicsShouldFetchFromForumAndFromMovedIdExpectTwoResults.sql");

		ForumRepository dao = this.newForumDao();
		Forum forum = new Forum(); forum.setId(1);
		List<Topic> topics = dao.getTopics(forum, 0, 10);
		Assert.assertEquals(2, topics.size());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getTopicsShouldIgnoreMovedIdExpectOneResult() {
		new JDBCLoader(session())
			.run("/forumdao/getTopicsShouldFetchFromForumAndFromMovedIdExpectTwoResults.sql");

		final JForumConfig config = mock(JForumConfig.class);
		
		when(config.getBoolean(ConfigKeys.QUERY_IGNORE_TOPIC_MOVED)).thenReturn(true);
		
		ForumRepository dao = this.newForumDao();
		dao.setJforumConfig(config);
		Forum forum = new Forum(); forum.setId(1);
		List<Topic> topics = dao.getTopics(forum, 0, 10);
		Assert.assertEquals(1, topics.size());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getTotalTopicsShouldFetchOnlyNonModeratedExpectTwoResults() {
		new JDBCLoader(session())
			.run("/forumdao/getTotalTopics.sql");

		Forum forum = new Forum(this.newForumDao()); forum.setId(1);
		Assert.assertEquals(2, forum.getTotalTopics());
	}

	@Test
	public void insert() {
		ForumRepository dao = this.newForumDao();

		Forum f = newForum();

		this.insert(f, dao);

		Assert.assertTrue(f.getId() > 0);

		Forum loaded = dao.get(f.getId());

		Assert.assertEquals("desc1", loaded.getDescription());
		Assert.assertEquals("forum1", loaded.getName());
		Assert.assertEquals(false, loaded.isModerated());
		Assert.assertEquals(1, loaded.getDisplayOrder());
		Assert.assertNotNull(loaded.getCategory());
		Assert.assertEquals(f.getCategory().getId(), loaded.getCategory().getId());
		Assert.assertNull(f.getLastPost());
	}

	@Test
	public void update() {
		ForumRepository dao = this.newForumDao();
		Forum f = newForum();
		this.insert(f, dao);
		f = dao.get(f.getId());

		// We'll change the category as well
		Category c = new Category(); c.setName("c2");
		CategoryRepository categoryDao = new CategoryRepository(session());
		categoryDao.add(c);

		f.setName("changed");
		f.setDescription("changed description");
		f.setModerated(true);
		f.setDisplayOrder(6);
		f.setCategory(c);

		this.update(f, dao);

		Forum loaded = dao.get(f.getId());

		Assert.assertEquals(f.getName(), loaded.getName());
		Assert.assertEquals(f.getDescription(), loaded.getDescription());
		Assert.assertEquals(f.isModerated(), loaded.isModerated());
		Assert.assertEquals(f.getDisplayOrder(), loaded.getDisplayOrder());
		Assert.assertEquals(f.getCategory().getId(), loaded.getCategory().getId());
	}

	private Forum newForum() {
		Forum f = new Forum();

		f.setDescription("desc1");
		f.setModerated(false);
		f.setName("forum1");
		f.setDisplayOrder(1);

		// Create the category before creating the forum
		CategoryRepository categoryDao = new CategoryRepository(session());

		Category c = new Category();
		c.setName("c1");

		categoryDao.add(c);

		f.setCategory(c);

		return f;
	}

	private ForumRepository newForumDao() {
		return new ForumRepository(session());
	}

	private PostRepository newPostDao() {
		return new PostRepository(session());
	}

	@SuppressWarnings("deprecation")
	private void createGetTopicsPosts() {
		new JDBCLoader(session())
			.run("/forumdao/getTopics.sql");

		// Topic 1
		PostRepository postDao = this.newPostDao();
		Post p1 = new Post(); p1.setSubject("p1.1"); p1.setTopic(new Topic() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }}); p1.setModerate(false);
		Post p2 = new Post(); p2.setSubject("p1.2"); p2.setTopic(new Topic() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }}); p2.setModerate(true);
		Post p3 = new Post(); p3.setSubject("p1.3"); p3.setTopic(new Topic() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }}); p3.setModerate(true);

		postDao.add(p1); postDao.add(p2); postDao.add(p3);

		// Topic 2
		Post p4 = new Post(); p4.setSubject("p2.1"); p4.setTopic(new Topic() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(2); }}); p4.setModerate(false);
		Post p5 = new Post(); p5.setSubject("p2.2"); p5.setTopic(new Topic() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(2); }}); p5.setModerate(true);

		postDao.add(p4); postDao.add(p5);

		// Topic 3
		Post p6 = new Post(); p6.setSubject("p3.1"); p6.setTopic(new Topic() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(3); }}); p6.setModerate(false);
		postDao.add(p6);

		this.commit();
		this.beginTransaction();
	}
}

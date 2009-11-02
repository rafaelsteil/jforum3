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
package net.jforum.plugins.tagging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.jforum.core.hibernate.AbstractDAOTestCase;
import net.jforum.core.hibernate.TopicDAO;
import net.jforum.entities.Topic;
import net.jforum.util.JDBCLoader;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Bill
 */
public class TagDAOTestCase extends AbstractDAOTestCase<Tag> {
	@Test
	@SuppressWarnings("deprecation")
	public void getTagByTopic() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/tagdao/dump.sql");

		TopicDAO topicDAO = this.newTopicDao();
		TagDAO tagdao = this.newDao();

		Topic topic = topicDAO.get(1);

		Tag tag1 = tagdao.get(1);
		Tag tag3 = tagdao.get(3);

		List<Tag> tags = tagdao.getTags(topic);
		Assert.assertEquals(2, tags.size());
		Assert.assertTrue(tags.contains(tag1));
		Assert.assertTrue(tags.contains(tag3));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getAll(){
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
		.run("/tagdao/dump.sql");

		TagDAO tagdao = this.newDao();
		List<String> tags = tagdao.getAll();

		Assert.assertEquals(3, tags.size());
		Assert.assertTrue(tags.contains("IT"));
		Assert.assertTrue(tags.contains("Indonesia"));
		Assert.assertTrue(tags.contains("Jakarta"));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getHotTags(){
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
		.run("/tagdao/dump.sql");
		this.commit();
		this.beginTransaction();

		TopicDAO topicDAO = this.newTopicDao();
		TagDAO tagdao = this.newDao();

		Topic topic1 = topicDAO.get(1);
		Topic topic2 = topicDAO.get(2);

		List<Topic> topics = new ArrayList<Topic>();
		topics.add(topic1);
		topics.add(topic2);
		Map<String,Long> tags = tagdao.getHotTags(topics, 3);
		this.commit();

		Assert.assertEquals(3, tags.size());

		Assert.assertTrue(tags.containsKey("IT"));
		Assert.assertTrue(tags.containsKey("Jakarta"));
		Assert.assertTrue(tags.containsKey("Indonesia"));

		Assert.assertTrue(tags.get("Indonesia").equals(1l));
		Assert.assertTrue(tags.get("IT").equals(2l));
		Assert.assertTrue(tags.get("Jakarta").equals(1l));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void removeTagByName(){
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
		.run("/tagdao/dump.sql");
		this.commit();
		this.beginTransaction();

		TagDAO tagdao = this.newDao();
		tagdao.remove("IT");
		this.commit();

		this.beginTransaction();
		List<String> tags = tagdao.getAll();
		this.commit();

		Assert.assertFalse(tags.contains("IT"));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void updateTagName(){
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
		.run("/tagdao/dump.sql");
		this.commit();
		this.beginTransaction();

		TagDAO tagdao = this.newDao();
		tagdao.update("IT","Information Technology");
		this.commit();

		this.beginTransaction();
		List<String> tags = tagdao.getAll();
		this.commit();

		Assert.assertFalse(tags.contains("IT"));
		Assert.assertTrue(tags.contains("Information Technology"));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void countTag(){
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
		.run("/tagdao/dump.sql");
		this.commit();
		this.beginTransaction();

		TagDAO tagdao = this.newDao();
		int count = tagdao.count("IT");
		this.commit();

		Assert.assertEquals(3, count);
	}

	@Test
	@SuppressWarnings({ "deprecation", "serial" })
	public void getTopicOfTagName(){
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
		.run("/tagdao/dump.sql");
		this.commit();
		this.beginTransaction();

		TagDAO tagdao = this.newDao();
		List<Topic> topics = tagdao.getTopics("IT");
		this.commit();

		Assert.assertEquals(3, topics.size());
		Assert.assertTrue(topics.contains(new Topic() {{ setId(1); }}));
		Assert.assertTrue(topics.contains(new Topic() {{ setId(2); }}));
		Assert.assertTrue(topics.contains(new Topic() {{ setId(3); }}));

	}
	private TagDAO newDao() {
		return new TagDAO(sessionFactory);
	}

	private TopicDAO newTopicDao() {
		return new TopicDAO(sessionFactory);
	}
}

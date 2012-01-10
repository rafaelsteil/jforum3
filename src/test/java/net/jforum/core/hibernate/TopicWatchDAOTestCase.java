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
import net.jforum.entities.TopicWatch;
import net.jforum.entities.User;
import net.jforum.repository.TopicRepository;
import net.jforum.repository.TopicWatchRepository;
import net.jforum.repository.UserRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class TopicWatchDAOTestCase extends AbstractDAOTestCase<TopicWatch> {
	@Test
	public void getUsersWaitingNotificationNoWatchExpectEmptyList() {
		TopicWatchRepository dao = this.newDao();
		Topic topic = new Topic(); topic.setId(13);
		List<User> users = dao.getUsersWaitingNotification(topic);
		Assert.assertEquals(0, users.size());
	}

	@Test
	public void getUsersWaitingNotificationTwoWatchesOneReadExpectOneResultShouldUpdate() {
		TopicWatchRepository dao = this.newDao();

		TopicWatch watch = this.newWatch(1, 1); watch.markAsRead();
		this.insert(watch, dao);
		this.insert(this.newWatch(1, 2), dao);

		Topic topic = new Topic(); topic.setId(1);

		List<User> users = dao.getUsersWaitingNotification(topic);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(1, users.get(0).getId());

		// Check the update
		users = dao.getUsersWaitingNotification(topic);
		Assert.assertEquals(0, users.size());
	}

	@Test
	public void removeSubscriptionByTopic() {
		TopicWatchRepository dao = this.newDao();

		this.insert(this.newWatch(1, 1), dao);

		Topic topic = new Topic(); topic.setId(1);
		User user = new User(); user.setId(1);

		dao.removeSubscription(topic);

		Assert.assertNull(dao.getSubscription(topic, user));
	}

	@Test
	public void removeSubscriptionByUser() {
		TopicWatchRepository dao = this.newDao();

		this.insert(this.newWatch(1, 1), dao);
		this.insert(this.newWatch(1, 2), dao);

		Topic topic = new Topic(); topic.setId(1);
		User user = new User(); user.setId(1);

		dao.removeSubscription(topic, user);

		Assert.assertNull(dao.getSubscription(topic, user));

		user.setId(2);
		Assert.assertNotNull(dao.getSubscription(topic, user));
	}

	@Test
	public void isUserSubscribedExpectFalse() {
		TopicWatchRepository dao = this.newDao();

		Topic topic = new Topic(); topic.setId(1);
		User user = new User(); user.setId(1);

		Assert.assertNull(dao.getSubscription(topic, user));
	}

	@Test
	public void isUserSubscribedExpectTrue() {
		TopicWatchRepository dao = this.newDao();

		this.insert(this.newWatch(1, 1), dao);

		Topic topic = new Topic(); topic.setId(1);
		User user = new User(); user.setId(1);

		Assert.assertNotNull(dao.getSubscription(topic, user));
	}

	@Test
	public void delete() {
		TopicWatchRepository dao = this.newDao();

		TopicWatch watch = this.newWatch(1, 1);
		this.insert(watch, dao);

		TopicWatch loaded = dao.get(watch.getId());
		this.delete(loaded, dao);

		Assert.assertNull(dao.get(watch.getId()));
	}

	@Test
	public void insert() {
		TopicWatchRepository dao = this.newDao();

		TopicWatch watch = this.newWatch(1, 1);
		this.insert(watch, dao);

		Assert.assertTrue(watch.getId() > 0);

		TopicWatch loaded = dao.get(watch.getId());
		Assert.assertNotNull(loaded);
		Assert.assertEquals(1, loaded.getTopic().getId());
		Assert.assertEquals(1, loaded.getUser().getId());
	}

	@Before
	public void setup() {
		User user = new User(); user.setUsername("u1");
		User user2 = new User(); user2.setUsername("u2");
		new UserRepository(session()).add(user);
		new UserRepository(session()).add(user2);

		Topic topic = new Topic(); topic.setSubject("t1"); topic.setFirstPost(null); topic.setLastPost(null); topic.setForum(null);
		new TopicRepository(session()).add(topic);
	}

	private TopicWatch newWatch(int topicId, int userId) {
		TopicWatch watch = new TopicWatch();

		Topic topic = new Topic(); topic.setId(topicId);
		User user = new User(); user.setId(userId);
		watch.setTopic(topic);
		watch.setUser(user);

		return watch;
	}

	private TopicWatchRepository newDao() {
		return new TopicWatchRepository(session());
	}
}

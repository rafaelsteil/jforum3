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
package net.jforum.entities;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;
import net.jforum.util.ConfigKeys;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class UserSessionTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private HttpSession httpSession = context.mock(HttpSession.class);
	private States state = context.states("userSessionState");
	private Map<Integer, Long> topicsReadTime;
	private UserSession userSession;

	@Before
	public void setup() {
		userSession = new UserSession(null);

		context.checking(new Expectations() {{
			allowing(request).getSession(); will(returnValue(httpSession));
			allowing(httpSession).getAttribute(ConfigKeys.LOGGED); will(returnValue("1")); when(state.is("logged"));
			allowing(httpSession).getAttribute(ConfigKeys.LOGGED); will(returnValue("0")); when(state.isNot("logged"));
		}});

		this.loadTopicsReadTime();
	}

	@Test
	public void isForumReadLastPostTimeNewerThanLastVisitTopicTrackingSmallerThanLastPostExpectFalse() {
		state.become("logged");
		userSession.setLastVisit(10);
		Forum forum = this.newForum(1, 20); forum.getLastPost().getTopic().setId(1);
		topicsReadTime.put(1, 15l);
		Assert.assertFalse(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadLastPostTimeNewerThanLastVisitTopicTrackingDoestNotExistExpectFalse() {
		state.become("logged");
		userSession.setLastVisit(10);
		Forum forum = this.newForum(1, 20); forum.getLastPost().getTopic().setId(1);
		topicsReadTime.put(2, 15l);
		Assert.assertFalse(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadLastPostTimeNewerThanLastVisitExpectFalse() {
		state.become("logged");
		userSession.setLastVisit(1);
		Assert.assertFalse(userSession.isForumRead(this.newForum(1, 2)));
	}

	@Test
	public void isForumReadLastVisitNewerThanLastPostTimeExpectTrue() {
		state.become("logged");
		userSession.setLastVisit(10);
		Assert.assertTrue(userSession.isForumRead(this.newForum(1, 5)));
	}

	@Test
	public void isForumReadLastPostIsNullExpectTrue() {
		state.become("logged");
		Forum forum = this.newForum(1, 1);
		forum.setLastPost(null);
		Assert.assertTrue(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadZeroPostsExpectTrue() {
		state.become("logged");
		Assert.assertTrue(userSession.isForumRead(this.newForum(0, 0)));
	}

	@Test
	public void isForumReadNotLoggedExpectTrue() {
		Assert.assertTrue(userSession.isForumRead(new Forum()));
	}

	@Test
	public void asSession() {
		context.checking(new Expectations() {{
			one(request).getRemoteAddr(); will(returnValue("ip"));
		}});

		userSession.setUser(new User() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(2); }});
		userSession.setCreationTime(2);
		userSession.setLastAccessedTime(1);
		userSession.setLastVisit(5);

		Session session  = userSession.asSession();

		context.assertIsSatisfied();

		Assert.assertEquals(new Date(1), session.getLastAccessed());
		Assert.assertEquals(new Date(2), session.getStart());
		Assert.assertEquals(new Date(5), session.getLastVisit());
		Assert.assertEquals("ip", session.getIp());
		Assert.assertEquals(2, session.getUserId());
	}

	@Test
	public void isTopicReadNotLoggedShouldReturnTrue() {
		Assert.assertFalse(userSession.isLogged());
		Assert.assertTrue(userSession.isTopicRead(new Topic()));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitNewerThanTopicShouldReturnTrue() {
		state.become("logged");

		Topic topic = new Topic(); topic.setLastPost(new Post()); topic.getLastPost().setDate(new Date(1));
		userSession.setLastVisit(5l);

		Assert.assertTrue(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitAndReadTimeOlderThanTopicShouldReturnFalse() {
		state.become("logged");

		Topic topic = new Topic(); topic.setLastPost(new Post()); topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(1);
		topicsReadTime.put(topic.getId(), 8L);

		Assert.assertFalse(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitOlderThanTopicReadTimeNewerThanTopicShouldReturnTrue() {
		state.become("logged");

		Topic topic = new Topic(); topic.setLastPost(new Post()); topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(1);
		topicsReadTime.put(topic.getId(), 20L);

		Assert.assertTrue(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitOlderThanTopicShouldReturnFalse() {
		state.become("logged");

		Topic topic = new Topic(); topic.setLastPost(new Post()); topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(5);

		Assert.assertFalse(userSession.isTopicRead(topic));
	}

	@Test
	public void markAsReadWhenLogged() {
		state.become("logged");
		Assert.assertTrue(userSession.isLogged());
		Assert.assertEquals(0, topicsReadTime.size());
		userSession.markTopicAsRead(2);
		Assert.assertEquals(1, topicsReadTime.size());
		Assert.assertTrue(topicsReadTime.containsKey(2));
	}

	@Test
	public void markTopicAsReadNotLoggedShouldIgnore() {
		Assert.assertFalse(userSession.isLogged());
		Assert.assertEquals(0, topicsReadTime.size());
		userSession.markTopicAsRead(1);
		Assert.assertEquals(0, topicsReadTime.size());
	}

	@SuppressWarnings("unchecked")
	private void loadTopicsReadTime() {
		Field[] fields = userSession.getClass().getDeclaredFields();
		for (Field field: fields) {
			if (field.getName().equals("topicReadTime")) {
				field.setAccessible(true);
				try {
					topicsReadTime = (Map<Integer, Long>)field.get(userSession);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private Forum newForum(final int totalPosts, long lastPostTime) {
		Forum forum = new Forum() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public int getTotalPosts() { return totalPosts; }
		};

		forum.setLastPost(new Post());
		forum.getLastPost().setDate(new Date(lastPostTime));
		forum.getLastPost().setTopic(new Topic());

		return forum;
	}
}

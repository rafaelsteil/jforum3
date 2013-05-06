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

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;
import net.jforum.util.ConfigKeys;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class UserSessionTestCase {
	
	@Mock private HttpServletRequest request;
	@Mock private HttpSession httpSession;
	private Map<Integer, Long> topicsReadTime;
	private UserSession userSession;

	@Before
	public void setup() {
		userSession = new UserSession();
		userSession.setRequest(request);
		
		when(request.getSession()).thenReturn(httpSession);
		
		this.loadTopicsReadTime();
	}

	@Test
	public void isForumReadLastPostTimeNewerThanLastVisitTopicTrackingSmallerThanLastPostExpectFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		userSession.setLastVisit(10);
		Forum forum = this.newForum(1, 20); 
		forum.getLastPost().getTopic().setId(1);
		topicsReadTime.put(1, 15l);
		
		Assert.assertFalse(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadLastPostTimeNewerThanLastVisitTopicTrackingDoestNotExistExpectFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		userSession.setLastVisit(10);
		Forum forum = this.newForum(1, 20); 
		forum.getLastPost().getTopic().setId(1);
		topicsReadTime.put(2, 15l);
		
		Assert.assertFalse(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadLastPostTimeNewerThanLastVisitExpectFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		userSession.setLastVisit(1);
		
		Assert.assertFalse(userSession.isForumRead(this.newForum(1, 2)));
	}

	@Test
	public void isForumReadLastVisitNewerThanLastPostTimeExpectTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		userSession.setLastVisit(10);
		
		Assert.assertTrue(userSession.isForumRead(this.newForum(1, 5)));
	}

	@Test
	public void isForumReadLastPostIsNullExpectTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Forum forum = this.newForum(1, 1);
		forum.setLastPost(null);
		
		Assert.assertTrue(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadZeroPostsExpectTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Forum forum = this.newForum(0, 0);
		
		Assert.assertTrue(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadNotLoggedExpectTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("0");
		
		Forum forum = new Forum();
		
		Assert.assertTrue(userSession.isForumRead(forum));
	}

	@Test
	public void asSession() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("0");
		when(request.getRemoteAddr()).thenReturn("ip");
		
		User user = new User();
		user.setId(2);
		userSession.setUser(user);
		userSession.setCreationTime(2);
		userSession.setLastAccessedTime(1);
		userSession.setLastVisit(5);

		Session session  = userSession.asSession();
		
		Assert.assertEquals(new Date(1), session.getLastAccessed());
		Assert.assertEquals(new Date(2), session.getStart());
		Assert.assertEquals(new Date(5), session.getLastVisit());
		Assert.assertEquals("ip", session.getIp());
		Assert.assertEquals(2, session.getUserId());
	}

	@Test
	public void isTopicReadNotLoggedShouldReturnTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("0");
		
		Topic topic = new Topic();
		
		Assert.assertFalse(userSession.isLogged());
		Assert.assertTrue(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitNewerThanTopicShouldReturnTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Topic topic = new Topic(); topic.setLastPost(new Post());
		topic.getLastPost().setDate(new Date(1));
		userSession.setLastVisit(5l);

		Assert.assertTrue(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitAndReadTimeOlderThanTopicShouldReturnFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Topic topic = new Topic(); topic.setLastPost(new Post());
		topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(1);
		topicsReadTime.put(topic.getId(), 8L);

		Assert.assertFalse(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitOlderThanTopicReadTimeNewerThanTopicShouldReturnTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Topic topic = new Topic(); topic.setLastPost(new Post());
		topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(1);
		topicsReadTime.put(topic.getId(), 20L);

		Assert.assertTrue(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitOlderThanTopicShouldReturnFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Topic topic = new Topic(); topic.setLastPost(new Post());
		topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(5);

		Assert.assertFalse(userSession.isTopicRead(topic));
	}

	@Test
	public void markAsReadWhenLogged() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Assert.assertTrue(userSession.isLogged());
		Assert.assertEquals(0, topicsReadTime.size());
		userSession.markTopicAsRead(2);
		Assert.assertEquals(1, topicsReadTime.size());
		Assert.assertTrue(topicsReadTime.containsKey(2));
	}

	@Test
	public void markTopicAsReadNotLoggedShouldIgnore() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("0");
		
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

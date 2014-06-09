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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
		
		assertFalse(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadLastPostTimeNewerThanLastVisitTopicTrackingDoestNotExistExpectFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		userSession.setLastVisit(10);
		Forum forum = this.newForum(1, 20); 
		forum.getLastPost().getTopic().setId(1);
		topicsReadTime.put(2, 15l);
		
		assertFalse(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadLastPostTimeNewerThanLastVisitExpectFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		userSession.setLastVisit(1);
		
		assertFalse(userSession.isForumRead(this.newForum(1, 2)));
	}

	@Test
	public void isForumReadLastVisitNewerThanLastPostTimeExpectTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		userSession.setLastVisit(10);
		
		assertTrue(userSession.isForumRead(this.newForum(1, 5)));
	}

	@Test
	public void isForumReadLastPostIsNullExpectTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Forum forum = this.newForum(1, 1);
		forum.setLastPost(null);
		
		assertTrue(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadZeroPostsExpectTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Forum forum = this.newForum(0, 0);
		
		assertTrue(userSession.isForumRead(forum));
	}

	@Test
	public void isForumReadNotLoggedExpectTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("0");
		
		Forum forum = new Forum();
		
		assertTrue(userSession.isForumRead(forum));
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
		
		assertEquals(new Date(1), session.getLastAccessed());
		assertEquals(new Date(2), session.getStart());
		assertEquals(new Date(5), session.getLastVisit());
		assertEquals("ip", session.getIp());
		assertEquals(2, session.getUserId());
	}

	@Test
	public void isTopicReadNotLoggedShouldReturnTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("0");
		
		Topic topic = new Topic();
		
		assertFalse(userSession.isLogged());
		assertTrue(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitNewerThanTopicShouldReturnTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Topic topic = new Topic(); topic.setLastPost(new Post());
		topic.getLastPost().setDate(new Date(1));
		userSession.setLastVisit(5l);

		assertTrue(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitAndReadTimeOlderThanTopicShouldReturnFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Topic topic = new Topic(); topic.setLastPost(new Post());
		topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(1);
		topicsReadTime.put(topic.getId(), 8L);

		assertFalse(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitOlderThanTopicReadTimeNewerThanTopicShouldReturnTrue() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Topic topic = new Topic(); topic.setLastPost(new Post());
		topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(1);
		topicsReadTime.put(topic.getId(), 20L);

		assertTrue(userSession.isTopicRead(topic));
	}

	@Test
	public void isTopicReadWhenLoggedLastVisitOlderThanTopicShouldReturnFalse() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		Topic topic = new Topic(); topic.setLastPost(new Post());
		topic.getLastPost().setDate(new Date(10));
		userSession.setLastVisit(5);

		assertFalse(userSession.isTopicRead(topic));
	}

	@Test
	public void markAsReadWhenLogged() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		assertTrue(userSession.isLogged());
		assertEquals(0, topicsReadTime.size());
		userSession.markTopicAsRead(2);
		assertEquals(1, topicsReadTime.size());
		assertTrue(topicsReadTime.containsKey(2));
	}

	@Test
	public void markTopicAsReadNotLoggedShouldIgnore() {
		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("0");
		
		assertFalse(userSession.isLogged());
		assertEquals(0, topicsReadTime.size());
		userSession.markTopicAsRead(1);
		assertEquals(0, topicsReadTime.size());
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

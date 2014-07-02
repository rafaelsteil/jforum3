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
package net.jforum.controllers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.RecentTopicsRepository;
import net.jforum.util.JForumConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class RecentTopicsControllerTestCase {
	@Mock private RecentTopicsRepository repository;
	@Mock private JForumConfig config;
	@Mock private UserSession userSession;
	@Spy private MockResult mockResult;
	
	@InjectMocks private RecentTopicsController component;

	List<Topic> topicList = new ArrayList<Topic>();
	
	@Test
	public void listNew() {
		component.listNew();

		assertEquals("recentTopicsNew", mockResult.included("recentTopicsSectionKey"));
		assertEquals(topicList, mockResult.included("topics"));
	}

	@Test
	public void listUpdated() {
		component.listUpdated();

		assertEquals("recentTopicsUpdated", mockResult.included("recentTopicsSectionKey"));
		assertEquals(topicList, mockResult.included("topics"));
	}

	@Test
	public void listHot() {
		component.listHot();

		assertEquals("recentTopicsHot", mockResult.included("recentTopicsSectionKey"));
		assertEquals(topicList, mockResult.included("topics"));
	}
}

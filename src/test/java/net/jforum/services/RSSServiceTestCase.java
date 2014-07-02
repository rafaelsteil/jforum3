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
package net.jforum.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;

import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.RSSRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class RSSServiceTestCase {

	@Mock private JForumConfig config;
	@Mock private RSSRepository rssRepository;
	@Mock private I18n i18n;
	@Mock private ForumRepository forumRepository;
	@InjectMocks private RSSService service;

	@Test
	public void forTopics() throws Exception {
		Forum forum = new Forum(); forum.setId(1); forum.setName("forum x"); forum.setDescription("forum description");

		when(forumRepository.get(forum.getId())).thenReturn(forum);
		when(config.getInt(ConfigKeys.TOPICS_PER_PAGE)).thenReturn(10);
		when(rssRepository.getForumTopics(forum, 10)).thenReturn(Arrays.asList(newTopic(1, "topic 1", 1, "post text 1")));
		when(i18n.params("forum x")).thenReturn(new Object[] { "forum x" });
		when(i18n.getFormattedMessage("RSS.ForumTopics.title", new Object[] { "forum x" })).thenReturn("channel title");
		when(config.getValue(ConfigKeys.RSS_DATE_TIME_FORMAT)).thenReturn("EEE, d MMM yyyy HH:mm:ss");
		when(config.getString(ConfigKeys.FORUM_LINK)).thenReturn("http://site.link/");
		String result = service.forForum(1);

		XpathEngine xpath = XMLUnit.newXpathEngine();
		Document document = XMLUnit.buildControlDocument(result);

		assertEquals("forum description", xpath.evaluate("//channel/description", document));
		assertEquals("http://site.link/forums/show/1.page", xpath.evaluate("//channel/link", document));
		assertEquals("channel title", xpath.evaluate("//channel/title", document));
		assertEquals("post text 1", xpath.evaluate("//channel/item/description", document));
		assertEquals("http://site.link/topics/preList/1/1.page", xpath.evaluate("//channel/item/link", document));
		assertEquals("topic 1", xpath.evaluate("//channel/item/title", document));
	}

	@Before
	public void setup() {
		when(config.getValue(ConfigKeys.SERVLET_EXTENSION)).thenReturn(".page");
	}

	private Topic newTopic(int id, String subject, int postId, String postText) {
		Topic topic = new Topic();

		topic.setId(1);
		topic.setDate(new Date());
		topic.setSubject(subject);
		topic.setLastPost(new Post());
		topic.getLastPost().setId(postId);
		topic.getLastPost().setText(postText);

		return topic;
	}
}

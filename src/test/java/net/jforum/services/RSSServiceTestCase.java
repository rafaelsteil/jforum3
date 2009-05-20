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

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.RSSRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * @author Rafael Steil
 */
public class RSSServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private JForumConfig config = context.mock(JForumConfig.class);
	private RSSRepository rssRepository = context.mock(RSSRepository.class);
	private ViewService viewService = context.mock(ViewService.class);
	private I18n i18n = context.mock(I18n.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private RSSService service = new RSSService(config, rssRepository, i18n, forumRepository);

	@Test
	public void forTopics() throws Exception {
		context.checking(new Expectations() {{
			Forum forum = new Forum(); forum.setId(1); forum.setName("forum x"); forum.setDescription("forum description");

			one(forumRepository).get(forum.getId()); will(returnValue(forum));
			one(config).getInt(ConfigKeys.TOPICS_PER_PAGE); will(returnValue(10));
			one(rssRepository).getForumTopics(forum, 10); will((returnValue(Arrays.asList(
				newTopic(1, "topic 1", 1, "post text 1")))));
			one(i18n).params("forum x"); will(returnValue(new Object[] { "forum x" }));
			one(i18n).getFormattedMessage("RSS.ForumTopics.title", new Object[] { "forum x" }); will(returnValue("channel title"));
			one(config).getValue(ConfigKeys.RSS_DATE_TIME_FORMAT); will(returnValue("EEE, d MMM yyyy HH:mm:ss"));
		}});

		String result = service.forForum(1, viewService);
		context.assertIsSatisfied();

		System.out.println(result);

		XpathEngine xpath = XMLUnit.newXpathEngine();
		Document document = XMLUnit.buildControlDocument(result);

		Assert.assertEquals("forum description", xpath.evaluate("//channel/description", document));
		Assert.assertEquals("http://site.link/forums/show/1.page", xpath.evaluate("//channel/link", document));
		Assert.assertEquals("channel title", xpath.evaluate("//channel/title", document));
		Assert.assertEquals("post text 1", xpath.evaluate("//channel/item/description", document));
		Assert.assertEquals("http://site.link/topics/preList/1/1.page", xpath.evaluate("//channel/item/link", document));
		Assert.assertEquals("topic 1", xpath.evaluate("//channel/item/title", document));
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(viewService).getForumLink(); will(returnValue("http://site.link/"));
			allowing(config).getValue(ConfigKeys.SERVLET_EXTENSION); will(returnValue(".page"));
		}});
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

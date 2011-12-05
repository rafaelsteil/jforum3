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


import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.jforum.controllers.TopicController;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.TopicRepository;
import net.jforum.security.ReplyTopicRule;
import net.jforum.security.RoleManager;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.vraptor.Result;

/**
 * @author Bill
 */
public class TagControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private TagService tagService = context.mock(TagService.class);
	private TopicRepository topicRepository = context.mock(TopicRepository.class);
	private UserSession userSession = context.mock(UserSession.class);
	private Result mockResult = context.mock(Result.class);
	private TagController tagAction = new TagController(tagService, topicRepository, userSession, mockResult);

	@Test
	public void find(){
		final String tag = "IT";
		context.checking(new Expectations() {{
			one(userSession).getRoleManager();
			one(tagService).search(with(is(tag)), with(any(RoleManager.class))); will(returnValue(new ArrayList<Topic>()));
			one(mockResult).include("topics", new ArrayList<Topic>(0));
			one(mockResult).include("tag", tag);
		}});

		tagAction.find(tag);
		context.assertIsSatisfied();
	}

	@Test
	public void replayShouldHasSecurityConstraint() throws SecurityException, NoSuchMethodException{
		Method method = tagAction.getClass().getMethod("reply", int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(ReplyTopicRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void reply() {
		final int topicId = 1;
		context.checking(new Expectations() {{
			Topic topic = new Topic(topicId);
			one(topicRepository).get(topicId); will(returnValue(topic));
			one(mockResult).include(with(is("forum")), with(any(Forum.class)));
			one(mockResult).include("topic", topic);
			one(tagService).getTagString(topic); will(returnValue("tags,tags"));
			one(mockResult).include("tags", "tags,tags");
		}});

		tagAction.reply(topicId);
		context.assertIsSatisfied();
	}

	@Test
	public void replySaveShouldHasSecurityConstraint() throws SecurityException, NoSuchMethodException {
		Method method = tagAction.getClass().getMethod("replySave", Topic.class,String.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(ReplyTopicRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void replySave(){
		final String tagString ="tags,tags";
		final Topic topic = new Topic();
		final TopicController mockTopicController = context.mock(TopicController.class);
		context.checking(new Expectations() {{
			one(tagService).addTag(with(is(tagString)), with(any(Topic.class)));
			one(mockResult).redirectTo(TopicController.class); will(returnValue(mockTopicController));
			one(mockTopicController).list(topic.getId(), 0, false);
		}});

		tagAction.replySave(topic, tagString);
		context.assertIsSatisfied();
	}

	@Test
	public void list(){
		context.checking(new Expectations() {{
			one(userSession).getRoleManager();
			one(tagService).getHotTags(with(is(200)), with(is(7)), with(any(RoleManager.class))); will(returnValue(new LinkedHashMap<String,Integer>()));
			one(mockResult).include("tags", new LinkedHashMap<String,Integer>());
		}});

		tagAction.list();
		context.assertIsSatisfied();
	}
}

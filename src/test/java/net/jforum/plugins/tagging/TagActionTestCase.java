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


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.TopicRepository;
import net.jforum.security.ReplyTopicRule;
import net.jforum.security.RoleManager;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

/**
 * @author Bill
 *
 */
public class TagActionTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private TagService tagService = context.mock(TagService.class);
	private TopicRepository topicRepository = context.mock(TopicRepository.class);
	private UserSession userSession = context.mock(UserSession.class);
	private TagController tagAction;

	@Test
	public void find(){
		final String tag = "IT";
		context.checking(new Expectations() {{
			one(userSession).getRoleManager();
			one(tagService).search(with(is(tag)), with(any(RoleManager.class))); will(returnValue(new ArrayList<Topic>()));
			one(propertyBag).put("topics", new ArrayList<Topic>(0));
			one(propertyBag).put("tag", tag);
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
			one(propertyBag).put(with(is("forum")), with(any(Forum.class)));
			one(propertyBag).put("topic", topic);
			one(tagService).getTagString(topic); will(returnValue("tags,tags"));
			one(propertyBag).put("tags", "tags,tags");
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
		context.checking(new Expectations() {{
			one(tagService).addTag(with(is(tagString)), with(any(Topic.class)));
			one(viewService).redirectToAction(Domain.TOPICS, Actions.LIST,topic.getId());
		}});

		tagAction.replySave(topic, tagString);
		context.assertIsSatisfied();
	}

	@Test
	public void list(){
		context.checking(new Expectations() {{
			one(userSession).getRoleManager();
			one(tagService).getHotTags(with(is(200)), with(is(7)), with(any(RoleManager.class))); will(returnValue(new LinkedHashMap<String,Integer>()));
			one(propertyBag).put("tags", new LinkedHashMap<String,Integer>());
		}});

		tagAction.list();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		tagAction = new TagController(propertyBag, tagService, topicRepository, viewService, userSession);
	}
}

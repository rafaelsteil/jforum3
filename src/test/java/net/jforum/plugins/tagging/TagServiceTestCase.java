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

import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Bill
 *
 */
public class TagServiceTestCase {

	private Mockery context = TestCaseUtils.newMockery();
	private TagRepository repository = context.mock(TagRepository.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private TagService service = new TagService(repository,forumRepository);

	@Test
	public void addNullTagShouldIgnore(){
		Topic topic = new Topic();
		context.checking(new Expectations() {{
		}});

		service.addTag(null,topic);
		context.assertIsSatisfied();
	}

	@Test
	public void addNullTopicShouldIgnore(){
		context.checking(new Expectations() {{
		}});

		service.addTag("sdsds",null);
		context.assertIsSatisfied();
	}

	@Test
	public void addEmptyTagShouldIgnore(){
		Topic topic = new Topic();
		context.checking(new Expectations() {{
		}});

		service.addTag("",topic);
		context.assertIsSatisfied();
	}

	@Test
	public void addEmptySpaceTagShouldIgnore(){
		Topic topic = new Topic();
		context.checking(new Expectations() {{
		}});

		service.addTag("    ",topic);
		context.assertIsSatisfied();
	}

	@Test
	public void addExpectSuccess() {
		final String tag = "IT,Jakarta";
		final Topic topic = new Topic();
		topic.setId(1);
		context.checking(new Expectations() {{
			Tag it = new Tag(); it.setName("IT");
			Tag jakarta = new Tag(); jakarta.setName("Jakarta");

			one(repository).add(it);
			one(repository).add(jakarta);
		}});
		service.addTag(tag,topic);
		context.assertIsSatisfied();
	}
}

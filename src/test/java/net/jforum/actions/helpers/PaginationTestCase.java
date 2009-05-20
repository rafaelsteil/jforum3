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
package net.jforum.actions.helpers;

import junit.framework.Assert;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.entities.util.Pagination;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PaginationTestCase {
	private Mockery context = TestCaseUtils.newMockery();

	@Test
	public void pagination1() {
		Pagination p = this.newPagination(5, 20, 0);

		Assert.assertEquals(1, p.getThisPage());
		Assert.assertEquals(4, p.getTotalPages());
	}

	@Test
	public void pagination2() {
		Pagination p = this.newPagination(5, 19, 0);
		Assert.assertEquals(4, p.getTotalPages());
	}

	@Test
	public void pagination3() {
		Pagination p = this.newPagination(3, 30, 0);
		Assert.assertEquals(10, p.getTotalPages());
	}

	@Test
	public void pagination4() {
		Pagination p = this.newPagination(3, 31, 0);
		Assert.assertEquals(11, p.getTotalPages());
	}

	@Test
	public void pagination5() {
		Pagination p = this.newPagination(7, 543, 0);
		Assert.assertEquals(78, p.getTotalPages());
	}

	@Test
	public void pageShouldNotBeBiggerThanTotalPages() {
		Pagination p = this.newPagination(10, 50, 100);
		Assert.assertEquals(p.getTotalPages(), p.getThisPage());
	}

	@Test
	public void pageZeroThisPageShouldBeOne() {
		Pagination p = this.newPagination(30, 100, 0);
		Assert.assertEquals(1, p.getThisPage());
	}

	@Test
	public void pageZeroStartShouldBeZero() {
		Pagination p = this.newPagination(30, 100, 0);
		Assert.assertEquals(0, p.getStart());
	}

	@Test
	public void pageOneStartShouldBeZero() {
		Pagination p = this.newPagination(30, 100, 1);
		Assert.assertEquals(0, p.getStart());
	}

	@Test
	public void pageTwoStartShouldBeProportional() {
		Pagination p = this.newPagination(30, 100, 2);
		Assert.assertEquals(30, p.getStart());
	}

	@Test
	public void forForum() {
		final Forum forum = context.mock(Forum.class);
		final JForumConfig config = context.mock(JForumConfig.class);

		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.TOPICS_PER_PAGE); will(returnValue(10));
			one(forum).getTotalTopics(); will(returnValue(50));
			one(forum).getId(); will(returnValue(1));
		}});

		Pagination p = new Pagination(config, 3).forForum(forum);

		context.assertIsSatisfied();

		Assert.assertEquals(10, p.getRecordsPerPage());
		Assert.assertEquals(50, p.getTotalRecords());
		Assert.assertEquals(5, p.getTotalPages());
		Assert.assertEquals(3, p.getThisPage());
		Assert.assertEquals(20, p.getStart());
		Assert.assertEquals(String.format("/%s/%s", Domain.FORUMS, Actions.SHOW), p.getBaseUrl());
	}

	@Test
	public void forTopic() {
		final Topic topic = context.mock(Topic.class);
		final JForumConfig config = context.mock(JForumConfig.class);

		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));
			one(topic).getTotalPosts(); will(returnValue(50));
			one(topic).getId(); will(returnValue(1));
		}});

		Pagination p = new Pagination(config, 3).forTopic(topic);

		context.assertIsSatisfied();

		Assert.assertEquals(10, p.getRecordsPerPage());
		Assert.assertEquals(50, p.getTotalRecords());
		Assert.assertEquals(5, p.getTotalPages());
		Assert.assertEquals(3, p.getThisPage());
		Assert.assertEquals(20, p.getStart());
		Assert.assertEquals(String.format("/%s/%s", Domain.TOPICS, Actions.LIST), p.getBaseUrl());
	}

	private Pagination newPagination(int recordsPerPage, int totalRecords, int page) {
		return new Pagination(totalRecords, recordsPerPage, page, "", 0);
	}
}

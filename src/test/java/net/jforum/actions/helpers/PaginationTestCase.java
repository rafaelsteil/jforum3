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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.entities.util.Pagination;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class PaginationTestCase {
	

	@Test
	public void pagination1() {
		Pagination p = this.newPagination(5, 20, 0);

		assertEquals(1, p.getThisPage());
		assertEquals(4, p.getTotalPages());
	}

	@Test
	public void pagination2() {
		Pagination p = this.newPagination(5, 19, 0);
		assertEquals(4, p.getTotalPages());
	}

	@Test
	public void pagination3() {
		Pagination p = this.newPagination(3, 30, 0);
		assertEquals(10, p.getTotalPages());
	}

	@Test
	public void pagination4() {
		Pagination p = this.newPagination(3, 31, 0);
		assertEquals(11, p.getTotalPages());
	}

	@Test
	public void pagination5() {
		Pagination p = this.newPagination(7, 543, 0);
		assertEquals(78, p.getTotalPages());
	}

	@Test
	public void pageShouldNotBeBiggerThanTotalPages() {
		Pagination p = this.newPagination(10, 50, 100);
		assertEquals(p.getTotalPages(), p.getThisPage());
	}

	@Test
	public void pageZeroThisPageShouldBeOne() {
		Pagination p = this.newPagination(30, 100, 0);
		assertEquals(1, p.getThisPage());
	}

	@Test
	public void pageZeroStartShouldBeZero() {
		Pagination p = this.newPagination(30, 100, 0);
		assertEquals(0, p.getStart());
	}

	@Test
	public void pageOneStartShouldBeZero() {
		Pagination p = this.newPagination(30, 100, 1);
		assertEquals(0, p.getStart());
	}

	@Test
	public void pageTwoStartShouldBeProportional() {
		Pagination p = this.newPagination(30, 100, 2);
		assertEquals(30, p.getStart());
	}

	@Test
	public void forForum() {
		final Forum forum = mock(Forum.class);
		final JForumConfig config = mock(JForumConfig.class);

		when(config.getInt(ConfigKeys.TOPICS_PER_PAGE)).thenReturn(10);
		when(forum.getTotalTopics()).thenReturn(50);
		when(forum.getId()).thenReturn(1);

		Pagination p = new Pagination(config, 3).forForum(forum);

		assertEquals(10, p.getRecordsPerPage());
		assertEquals(50, p.getTotalRecords());
		assertEquals(5, p.getTotalPages());
		assertEquals(3, p.getThisPage());
		assertEquals(20, p.getStart());
		assertEquals(String.format("/%s/%s", Domain.FORUMS, Actions.SHOW), p.getBaseUrl());
	}

	@Test
	public void forTopic() {
		final Topic topic = mock(Topic.class);
		final JForumConfig config = mock(JForumConfig.class);

		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
		when(topic.getTotalPosts()).thenReturn(50);
		when(topic.getId()).thenReturn(1);

		Pagination p = new Pagination(config, 3).forTopic(topic);

		assertEquals(10, p.getRecordsPerPage());
		assertEquals(50, p.getTotalRecords());
		assertEquals(5, p.getTotalPages());
		assertEquals(3, p.getThisPage());
		assertEquals(20, p.getStart());
		assertEquals(String.format("/%s/%s", Domain.TOPICS, Actions.LIST), p.getBaseUrl());
	}

	private Pagination newPagination(int recordsPerPage, int totalRecords, int page) {
		return new Pagination(totalRecords, recordsPerPage, page, "", 0);
	}
}

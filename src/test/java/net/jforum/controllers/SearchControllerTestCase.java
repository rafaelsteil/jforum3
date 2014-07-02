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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import net.jforum.actions.helpers.Actions;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.entities.util.SearchParams;
import net.jforum.entities.util.SearchResult;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.SearchRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.ConfigKeys;
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
public class SearchControllerTestCase {
	
	@Mock private CategoryRepository categoryRepository;
	@Mock private JForumConfig config;
	@Mock private SearchRepository searchRepository;
	@Mock private UserSession userSession;
	@Mock private RoleManager roleManager;
	@Spy private MockResult mockResult;
	@InjectMocks private SearchController controller;

	@Test
	public void executeWithEmptyQueryShouldRedirectToFilters() {
		SearchParams params = new SearchParams();
		params.setQuery("");
		
		controller.execute(params);
		
		verify(mockResult).redirectTo(Actions.FILTERS);
	}

	@Test
	public void executeFindThreeRecordsOneIsNotAllowedShouldRemoveExpectTwoRecords() throws Exception {
		final SearchResult result = new SearchResult(new ArrayList<Post>(Arrays.asList(this.newPost(1, 1), this.newPost(2, 1), this.newPost(3, 2))), 3);
		final SearchParams params = new SearchParams();
		params.setQuery("abc");

		when(config.getInt(ConfigKeys.TOPICS_PER_PAGE)).thenReturn(30);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(searchRepository.search(params)).thenReturn(result);
		when(roleManager.isForumAllowed(1)).thenReturn(true);
		when(roleManager.isForumAllowed(2)).thenReturn(false);
		when(categoryRepository.getAllCategories()).thenReturn(new ArrayList<Category>());
			
		controller.execute(params);
		
		assertEquals(result.getResults(), mockResult.included("results"));
		assertEquals(params, mockResult.included("searchParams"));
		assertEquals(new Pagination(config, 0), mockResult.included("pagination"));
		assertEquals(new ArrayList<Category>(), mockResult.included("categories"));
		assertEquals(2, result.getTotalRecords());
		assertEquals(2, result.getResults().size());
		Post post = new Post();
		post.setId(3);
		assertFalse(result.getResults().contains(post));
	}

	@Test
	public void filter() {
		when(categoryRepository.getAllCategories()).thenReturn(new ArrayList<Category>());
	
		controller.filters();
		
		assertEquals(new ArrayList<Category>(), mockResult.included("categories"));
	}

	private Post newPost(int postId, int forumId) {
		Post post = new Post();
		post.setId(postId);
		post.setForum(new Forum());
		post.getForum().setId(forumId);
		return post;
	}
}

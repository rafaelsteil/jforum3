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

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;
import net.jforum.actions.helpers.Actions;
import net.jforum.controllers.SearchController;
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
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class SearchControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private CategoryRepository categoryRepository = context
			.mock(CategoryRepository.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private SearchRepository searchRepository = context
			.mock(SearchRepository.class);
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private Result mockResult = context.mock(MockResult.class);
	private SearchController controller = new SearchController(categoryRepository,
			config, searchRepository, userSession, mockResult);

	@Test
	public void executeWithEmptyQueryShouldRedirectToFilters() {
		context.checking(new Expectations() {
			{
				one(mockResult).redirectTo(Actions.FILTERS);
			}
		});

		SearchParams params = new SearchParams();
		params.setQuery("");
		controller.execute(params);
	}

	@Test
	public void executeFindThreeRecordsOneIsNotAllowedShouldRemoveExpectTwoRecords()
			throws Exception {
		final SearchResult result = new SearchResult(new ArrayList<Post>(
				Arrays.asList(this.newPost(1, 1), this.newPost(2, 1),
						this.newPost(3, 2))), 3);
		final SearchParams params = new SearchParams();
		params.setQuery("abc");

		context.checking(new Expectations() {
			{
				allowing(config).getInt(ConfigKeys.TOPICS_PER_PAGE);
				will(returnValue(30));
				allowing(userSession).getRoleManager();
				will(returnValue(roleManager));
				one(searchRepository).search(params);
				will(returnValue(result));
				allowing(roleManager).isForumAllowed(1);
				will(returnValue(true));
				allowing(roleManager).isForumAllowed(2);
				will(returnValue(false));
				one(categoryRepository).getAllCategories();
				will(returnValue(new ArrayList<Category>()));

				one(mockResult).include("results", result.getResults());
				one(mockResult).include("searchParams", params);
				one(mockResult)
						.include("pagination", new Pagination(config, 0));
				one(mockResult)
						.include("categories", new ArrayList<Category>());
			}
		});

		controller.execute(params);
		context.assertIsSatisfied();
		Assert.assertEquals(2, result.getTotalRecords());
		Assert.assertEquals(2, result.getResults().size());
		Post post = new Post();
		post.setId(3);
		Assert.assertFalse(result.getResults().contains(post));
	}

	@Test
	public void filter() {
		context.checking(new Expectations() {
			{
				one(categoryRepository).getAllCategories();
				will(returnValue(new ArrayList<Category>()));
				one(mockResult)
						.include("categories", new ArrayList<Category>());
			}
		});

		controller.filters();
		context.assertIsSatisfied();
	}

	private Post newPost(int postId, int forumId) {
		Post post = new Post();
		post.setId(postId);
		post.setForum(new Forum());
		post.getForum().setId(forumId);
		return post;
	}
}

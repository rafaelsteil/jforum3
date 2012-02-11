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

import java.util.Date;

import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.PaginatedResult;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.AccessForumRule;
import net.jforum.security.AuthenticatedRule;
import net.jforum.services.MostUsersEverOnlineService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.GroupInteractionFilter;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.FORUMS)
public class ForumController {
	private CategoryRepository categoryRepository;
	private ForumRepository forumRepository;
	private UserRepository userRepository;
	private MostUsersEverOnlineService mostUsersEverOnlineService;
	private JForumConfig config;
	private GroupInteractionFilter groupInteractionFilter;
	private final Result result;
	private final UserSession userSession;
	private final SessionManager sessionManager;

	public ForumController(CategoryRepository categoryRepository,
		ForumRepository forumRepository, UserSession userSession,
		UserRepository userRepository, MostUsersEverOnlineService mostUsersEverOnlineService,
		JForumConfig config, GroupInteractionFilter groupInteractionFilter,
		Result result, SessionManager sessionManager) {
		this.categoryRepository = categoryRepository;
		this.userSession = userSession;
		this.forumRepository = forumRepository;
		this.userRepository = userRepository;
		this.mostUsersEverOnlineService = mostUsersEverOnlineService;
		this.config = config;
		this.groupInteractionFilter = groupInteractionFilter;
		this.result = result;
		this.sessionManager = sessionManager;
	}

	/**
	 * Show the new messages since the last time the user did something in the forum
	 */
	@SecurityConstraint(value = AuthenticatedRule.class, displayLogin = true)
	public void newMessages(int page) {
		UserSession userSession = this.userSession;
		int recordsPerPage = this.config.getInt(ConfigKeys.TOPICS_PER_PAGE);

		PaginatedResult<Topic> newMessages = this.forumRepository.getNewMessages(new Date(userSession.getLastVisit()),
			new Pagination().calculeStart(page, recordsPerPage), recordsPerPage);

		Pagination pagination = new Pagination(this.config, page).forNewMessages(newMessages.getTotalRecords());

		this.result.include("pagination", pagination);
		this.result.include("results", newMessages.getResults());
		this.result.include("categories", this.categoryRepository.getAllCategories());
	}

	/**
	 * Show topics from a forum
	 */
	@SecurityConstraint(value = AccessForumRule.class, displayLogin = true)
	@Path({"/show/{forumId}", "/show/{forumId}/{page}"})
	public void show(int forumId, int page) {
		Forum forum = this.forumRepository.get(forumId);

		Pagination pagination = new Pagination(this.config, page).forForum(forum);

		this.result.include("forum", forum);
		this.result.include("pagination", pagination);
		this.result.include("isModeratorOnline", this.sessionManager.isModeratorOnline());
		this.result.include("categories", this.categoryRepository.getAllCategories());
		this.result.include("topics", forum.getTopics(pagination.getStart(),
			pagination.getRecordsPerPage()));
	}

	/**
	 * Listing of all forums
	 */
	public void list() {
		this.result.include("categories", this.categoryRepository.getAllCategories());
		this.result.include("onlineUsers", this.sessionManager.getLoggedSessions());
		this.result.include("totalRegisteredUsers", this.userRepository.getTotalUsers());
		this.result.include("totalMessages", this.forumRepository.getTotalMessages());
		this.result.include("totalLoggedUsers", this.sessionManager.getTotalLoggedUsers());
		this.result.include("totalAnonymousUsers", this.sessionManager.getTotalAnonymousUsers());
		this.result.include("lastRegisteredUser", this.userRepository.getLastRegisteredUser());
		this.result.include("postsPerPage", this.config.getInt(ConfigKeys.POSTS_PER_PAGE));
		this.result.include("mostUsersEverOnline", mostUsersEverOnlineService
			.getMostRecentData(this.sessionManager.getTotalUsers()));

		if (userSession.isLogged() && !userSession.getRoleManager().roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)) {
			this.groupInteractionFilter.filterForumListing(this.result, userSession);
		}
	}
}

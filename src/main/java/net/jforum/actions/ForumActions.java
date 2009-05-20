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
package net.jforum.actions;

import java.util.Date;

import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
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

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.FORUMS)
@InterceptedBy(MethodSecurityInterceptor.class)
public class ForumActions {
	private CategoryRepository categoryRepository;
	private ForumRepository forumRepository;
	private UserRepository userRepository;
	private SessionManager sessionManager;
	private ViewPropertyBag propertyBag;
	private MostUsersEverOnlineService mostUsersEverOnlineService;
	private JForumConfig config;
	private GroupInteractionFilter groupInteractionFilter;

	public ForumActions(ViewPropertyBag propertyBag, CategoryRepository categoryRepository,
		SessionManager sessionManager, ForumRepository forumRepository,
		UserRepository userRepository, MostUsersEverOnlineService mostUsersEverOnlineService,
		JForumConfig config, GroupInteractionFilter groupInteractionFilter) {
		this.propertyBag = propertyBag;
		this.categoryRepository = categoryRepository;
		this.sessionManager = sessionManager;
		this.forumRepository = forumRepository;
		this.userRepository = userRepository;
		this.mostUsersEverOnlineService = mostUsersEverOnlineService;
		this.config = config;
		this.groupInteractionFilter = groupInteractionFilter;
	}

	/**
	 * Show the new messages since the last time the user did something in the forum
	 */
	@SecurityConstraint(value = AuthenticatedRule.class, displayLogin = true)
	public void newMessages(@Parameter(key = "page") int page) {
		UserSession userSession = sessionManager.getUserSession();
		int recordsPerPage = config.getInt(ConfigKeys.TOPICS_PER_PAGE);

		PaginatedResult<Topic> newMessages = forumRepository.getNewMessages(new Date(userSession.getLastVisit()),
			new Pagination().calculeStart(page, recordsPerPage), recordsPerPage);

		Pagination pagination = new Pagination(config, page).forNewMessages(newMessages.getTotalRecords());

		propertyBag.put("pagination", pagination);
		propertyBag.put("results", newMessages.getResults());
		propertyBag.put("categories", categoryRepository.getAllCategories());
	}

	/**
	 * Show topics from a forum
	 */
	@SecurityConstraint(value = AccessForumRule.class, displayLogin = true)
	public void show(@Parameter(key = "forumId") int forumId, @Parameter(key = "page") int page) {
		Forum forum = forumRepository.get(forumId);

		Pagination pagination = new Pagination(config, page).forForum(forum);

		propertyBag.put("forum", forum);
		propertyBag.put("pagination", pagination);
		propertyBag.put("isModeratorOnline", sessionManager.isModeratorOnline());
		propertyBag.put("categories", categoryRepository.getAllCategories());
		propertyBag.put("topics", forum.getTopics(pagination.getStart(),
			pagination.getRecordsPerPage()));
	}

	/**
	 * Listing of all forums
	 */
	public void list() {
		propertyBag.put("categories", categoryRepository.getAllCategories());
		propertyBag.put("onlineUsers", sessionManager.getLoggedSessions());
		propertyBag.put("totalRegisteredUsers", userRepository.getTotalUsers());
		propertyBag.put("totalMessages", forumRepository.getTotalMessages());
		propertyBag.put("totalLoggedUsers", sessionManager.getTotalLoggedUsers());
		propertyBag.put("totalAnonymousUsers", sessionManager.getTotalAnonymousUsers());
		propertyBag.put("lastRegisteredUser", userRepository.getLastRegisteredUser());
		propertyBag.put("postsPerPage", config.getInt(ConfigKeys.POSTS_PER_PAGE));
		propertyBag.put("mostUsersEverOnline", mostUsersEverOnlineService
			.getMostRecentData(sessionManager.getTotalUsers()));

		UserSession userSession = sessionManager.getUserSession();

		if (userSession.isLogged() && !userSession.getRoleManager().roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)) {
			groupInteractionFilter.filterForumListing(propertyBag, userSession);
		}
	}
}

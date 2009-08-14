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
		UserSession userSession = this.sessionManager.getUserSession();
		int recordsPerPage = this.config.getInt(ConfigKeys.TOPICS_PER_PAGE);

		PaginatedResult<Topic> newMessages = this.forumRepository.getNewMessages(new Date(userSession.getLastVisit()),
			new Pagination().calculeStart(page, recordsPerPage), recordsPerPage);

		Pagination pagination = new Pagination(this.config, page).forNewMessages(newMessages.getTotalRecords());

		this.propertyBag.put("pagination", pagination);
		this.propertyBag.put("results", newMessages.getResults());
		this.propertyBag.put("categories", this.categoryRepository.getAllCategories());
	}

	/**
	 * Show topics from a forum
	 */
	@SecurityConstraint(value = AccessForumRule.class, displayLogin = true)
	public void show(@Parameter(key = "forumId") int forumId, @Parameter(key = "page") int page) {
		Forum forum = this.forumRepository.get(forumId);

		Pagination pagination = new Pagination(this.config, page).forForum(forum);

		this.propertyBag.put("forum", forum);
		this.propertyBag.put("pagination", pagination);
		this.propertyBag.put("isModeratorOnline", this.sessionManager.isModeratorOnline());
		this.propertyBag.put("categories", this.categoryRepository.getAllCategories());
		this.propertyBag.put("topics", forum.getTopics(pagination.getStart(),
			pagination.getRecordsPerPage()));
	}

	/**
	 * Listing of all forums
	 */
	public void list() {
		this.propertyBag.put("categories", this.categoryRepository.getAllCategories());
		this.propertyBag.put("onlineUsers", this.sessionManager.getLoggedSessions());
		this.propertyBag.put("totalRegisteredUsers", this.userRepository.getTotalUsers());
		this.propertyBag.put("totalMessages", this.forumRepository.getTotalMessages());
		this.propertyBag.put("totalLoggedUsers", this.sessionManager.getTotalLoggedUsers());
		this.propertyBag.put("totalAnonymousUsers", this.sessionManager.getTotalAnonymousUsers());
		this.propertyBag.put("lastRegisteredUser", this.userRepository.getLastRegisteredUser());
		this.propertyBag.put("postsPerPage", this.config.getInt(ConfigKeys.POSTS_PER_PAGE));
		this.propertyBag.put("mostUsersEverOnline", mostUsersEverOnlineService
			.getMostRecentData(this.sessionManager.getTotalUsers()));

		UserSession userSession = this.sessionManager.getUserSession();

		if (userSession.isLogged() && !userSession.getRoleManager().roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)) {
			this.groupInteractionFilter.filterForumListing(propertyBag, userSession);
		}
	}
}

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
package net.jforum.security;

import javax.servlet.http.HttpServletRequest;

import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.Forum;
import net.jforum.entities.UserSession;
import net.jforum.repository.ForumRepository;
import br.com.caelum.vraptor.ioc.Component;



/**
 * Check if the user can create a new topic.
 * This is intended to be used with {@link SecurityConstraint}, and will check
 * if the current user can create a new topic on a given forum.
 * @author Rafael Steil
 */
@Component
public class CreateNewTopicRule implements AccessRule {
	private ForumRepository repository;
	private SessionManager sessionManager;

	public CreateNewTopicRule(ForumRepository repository, SessionManager sessionManager) {
		this.repository = repository;
		this.sessionManager = sessionManager;
	}

	/**
	 * Applies the following rules:
	 * <ul>
	 * 	<li> User must have access to the forum
	 * 	<li> Forum should not be read-only and not reply-only
	 * 	<li> User must be logged or anonymous posts allowed in the forum.
	 * </ul>
	 * It is expected that the parameter <i>forumId</i> or <i>topic.forum.id</i> exists in the request
	 */
	@Override
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		RoleManager roleManager = userSession.getRoleManager();

		int forumId = this.findForumId(request);
		Forum forum = this.repository.get(forumId);

		return roleManager.isForumAllowed(forumId)
			&& (userSession.isLogged() || forum.isAllowAnonymousPosts())
			&& (!roleManager.isForumReadOnly(forumId) && !roleManager.isForumReplyOnly(forumId))
			&& (!roleManager.getPostOnlyWithModeratorOnline() || (roleManager.getPostOnlyWithModeratorOnline() && this.sessionManager.isModeratorOnline()));
	}

	/**
	 * Tries to find the forum id in the current request
	 */
	private int findForumId(HttpServletRequest request) {
		int forumId = 0;

		if (request.getParameterMap().containsKey("forumId")) {
			forumId = Integer.parseInt(request.getParameter("forumId"));
		}
		else if (request.getParameterMap().containsKey("topic.forum.id")) {
			forumId = Integer.parseInt(request.getParameter("topic.forum.id"));
		}
		else {
			throw new AccessRuleException("Could not find forumId or topic.forum.id in the current request");
		}

		return forumId;
	}
}

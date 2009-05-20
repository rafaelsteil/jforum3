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

import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.Post;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostRepository;

/**
 * @author Rafael Steil
 */
public class ChangePostRule implements AccessRule {
	private final PostRepository repository;

	public ChangePostRule(PostRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see net.jforum.security.AccessRule#shouldProceed(net.jforum.entities.UserSession, javax.servlet.http.HttpServletRequest)
	 */
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		RoleManager roleManager = userSession.getRoleManager();

		if (roleManager.isAdministrator()) {
			return true;
		}

		int postId = this.findPostId(request);
		Post post = repository.get(postId);

		if (roleManager.isModerator() && roleManager.getCanModerateForum(post.getForum().getId())) {
			return true;
		}

		return userSession.isLogged() && userSession.getUser().getId() == post.getUser().getId();
	}

	private int findPostId(HttpServletRequest request) {
		int postId = 0;

		if (request.getParameterMap().containsKey("postId")) {
			postId = Integer.parseInt(request.getParameter("postId"));
		}
		else if (request.getParameterMap().containsKey("post.id")) {
			postId = Integer.parseInt(request.getParameter("post.id"));
		}
		else {
			throw new AccessRuleException("Could not find postId or post.id in the current request");
		}

		return postId;
	}
}

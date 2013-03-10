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
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.TopicRepository;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Check if the user has access to a forum
 * This is intended to be used with {@link SecurityConstraint}, and will check
 * if the current user can access the contents of a forum
 * @author Rafael Steil
 */
@Component
public class AccessForumRule implements AccessRule {
	private TopicRepository topicRepository;

	public AccessForumRule(TopicRepository topicRepository) {
		this.topicRepository = topicRepository;
	}

	/**
	 * Applies the following rules:
	 * <ul>
	 * 	<li> User should have access to the requested topic
	 * </ul>
	 * It is expected that the parameter <i>topicId</i> or <i>forumId</i> exists in the request
	 */
	@Override
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		int forumId = this.findForumId(request);
		return userSession.getRoleManager().isForumAllowed(forumId);
	}

	private int findForumId(HttpServletRequest request) {
		int forumId = 0;

		if (request.getParameterMap().containsKey("forumId")) {
			forumId = Integer.parseInt(request.getParameter("forumId"));
		}
		else if (request.getParameterMap().containsKey("topicId")) {
			Topic topic = topicRepository.get(Integer.parseInt(request.getParameter("topicId")));
			forumId = topic.getForum().getId();
		}
		else {
			throw new AccessRuleException("Could not find topicId in the current request");
		}

		return forumId;
	}
}

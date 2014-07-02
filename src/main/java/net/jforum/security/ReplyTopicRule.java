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
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Check if the user can reply to an existing topic.
 * This is intended to be used with {@link SecurityConstraint}, and will check
 * if the current user can reply to an existing topic.
 * @author Rafael Steil
 */
@Component
public class ReplyTopicRule implements AccessRule {
	private TopicRepository topicRepository;
	private PostRepository postRepository;
	private ForumRepository forumRepository;
	private SessionManager sessionManager;

	public ReplyTopicRule(TopicRepository topicRepository, PostRepository postRepository,
		ForumRepository forumRepository, SessionManager sessionManager) {
		this.topicRepository = topicRepository;
		this.postRepository = postRepository;
		this.forumRepository = forumRepository;
		this.sessionManager = sessionManager;
	}

	/**
	 * Applies the following rules:
	 * <ul>
	 * 	<li> User must have access to the forum
	 * 	<li> Forum should not be read-only
	 * 	<li> User must be logged or anonymous posts allowed in the forum.
	 * </ul>
	 * It is expected that the parameter <i>topicId</i>, <i>topic.forum.id</i>
	 * or <i>postId</i> exists in the request
	 */
	@Override
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		RoleManager roleManager = userSession.getRoleManager();
		int forumId = this.findForumId(request);
		Forum forum = this.forumRepository.get(forumId);

		return roleManager.isForumAllowed(forumId)
			&& (userSession.isLogged() || forum.isAllowAnonymousPosts())
			&& !roleManager.isForumReadOnly(forumId)
			&& (!roleManager.getPostOnlyWithModeratorOnline() || (roleManager.getPostOnlyWithModeratorOnline() && this.sessionManager.isModeratorOnline()));
	}

	private int findForumId(HttpServletRequest request) {
		int forumId = 0;

		if (request.getParameterMap().containsKey("topic.forum.id")) {
			forumId = Integer.parseInt(request.getParameter("topic.forum.id"));
		}
		else if (request.getParameterMap().containsKey("topicId")) {
			forumId = this.getForumIdFromTopic(Integer.parseInt(request.getParameter("topicId")));
		}
		else if (request.getParameterMap().containsKey("postId")) {
			forumId = this.getForumIdFromPost(Integer.parseInt(request.getParameter("postId")));
		}
		else {
			throw new AccessRuleException("Could not find topicId, topic.forum.id or postId in the current request");
		}

		return forumId;
	}

	private int getForumIdFromPost(int postId) {
		Post post = this.postRepository.get(postId);
		return post.getForum().getId();
	}

	private int getForumIdFromTopic(int topicId) {
		Topic topic = this.topicRepository.get(topicId);
		return topic.getForum().getId();
	}
}

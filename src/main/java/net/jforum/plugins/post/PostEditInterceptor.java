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
package net.jforum.plugins.post;

import javax.servlet.http.HttpServletRequest;

import net.jforum.controllers.PostController;
import net.jforum.core.SessionManager;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Apply minimum time constraints between each posting, to avoid spamming
 */
@Intercepts
public class PostEditInterceptor implements Interceptor {
	private final ForumLimitedTimeRepository repository;
	private final PostRepository postRepository;
	private final JForumConfig config;
	private final HttpServletRequest request;
	private final Result result;
	private final UserSession userSession;
	private final SessionManager sessionManager;

	public PostEditInterceptor(PostRepository postRepository, ForumLimitedTimeRepository repository,
			JForumConfig config, HttpServletRequest request, Result result,
			UserSession userSession, SessionManager sessionManager) {
		this.postRepository = postRepository;
		this.repository = repository;
		this.config = config;
		this.userSession = userSession;
		this.request = request;
		this.result = result;
		this.sessionManager = sessionManager;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false)
			&& method.getResource().getType().equals(PostController.class)
			&& method.getMethod().getName().equals("edit");
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		RoleManager roleManager = userSession.getRoleManager();

		if (!roleManager.isAdministrator() && !roleManager.isModerator() && !roleManager.getCanEditPosts()) {
			int postId = Integer.parseInt(request.getParameter("postId"));

			Post post = this.postRepository.get(postId);
			Forum forum = post.getForum();

			long time = this.repository.getLimitedTime(forum);

			if (time > 0) {
				long duration = (System.currentTimeMillis() - post.getDate().getTime()) / 1000;

				if (duration > time) {
					// TODO: Decide to where redirect the user
					throw new RuntimeException("duration > time");
				}
			}

			if (roleManager.getPostOnlyWithModeratorOnline() && !sessionManager.isModeratorOnline()) {
				// TODO
				throw new RuntimeException("Posting is only allowed when moderators are online");
			}
		}

		stack.next(method, resourceInstance);
	}
}

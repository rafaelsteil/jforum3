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

import net.jforum.core.SessionManager;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.ViewService;
import net.jforum.util.JForumConfig;

import org.vraptor.Interceptor;
import org.vraptor.LogicException;
import org.vraptor.LogicFlow;
import org.vraptor.LogicRequest;
import org.vraptor.view.ViewException;

/**
 * @author Bill
 */
public class PostEditInterceptor implements Interceptor {
	private final ForumLimitedTimeRepository repository;
	private final PostRepository postRepository;
	private final JForumConfig config;
	private final SessionManager sessionManager;
	private final ViewService viewService;

	public PostEditInterceptor(PostRepository postRepository, ForumLimitedTimeRepository repository,
			JForumConfig config, SessionManager sessionManager, ViewService viewService) {
		this.postRepository = postRepository;
		this.repository = repository;
		this.config = config;
		this.sessionManager = sessionManager;
		this.viewService = viewService;
	}

	/**
	 * @see org.vraptor.Interceptor#intercept(org.vraptor.LogicFlow)
	 */
	public void intercept(LogicFlow flow) throws LogicException, ViewException {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);

		if (isEnabled) {
			UserSession userSession = this.sessionManager.getUserSession();
			RoleManager roleManager = userSession.getRoleManager();

			if (!roleManager.isAdministrator() && !roleManager.isModerator() && !roleManager.getCanEditPosts()) {
				LogicRequest logicRequest = flow.getLogicRequest();

				HttpServletRequest request = logicRequest.getRequest();
				int postId = Integer.parseInt(request.getParameter("postId"));

				Post post = this.postRepository.get(postId);
				Forum forum = post.getForum();

				long time = this.repository.getLimitedTime(forum);

				if (time > 0) {
					long duration = (System.currentTimeMillis() - post.getDate().getTime()) / 1000;

					if (duration > time) {
						this.viewService.renderView("postTimeLimited", "limited");
						return;
					}
				}
				if(roleManager.getPostOnlyWithModeratorOnline() && !sessionManager.isModeratorOnline()) {
					this.viewService.renderView("canOnlyPostWithModeratorOnline", "moderatorOnline");
					return;
				}
			}
		}

		flow.execute();
	}
}

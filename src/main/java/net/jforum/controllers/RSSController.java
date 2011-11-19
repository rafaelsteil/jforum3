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

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.entities.UserSession;
import net.jforum.services.RSSService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.RSS)
public class RSSController {
	private RSSService rssService;
	private UserSession userSession;
	private JForumConfig config;
	private final Result result;

	public RSSController(Result result, RSSService rssService,
			UserSession userSession, JForumConfig config) {
		this.result = result;
		this.rssService = rssService;
		this.userSession = userSession;
		this.config = config;
	}

	/**
	 * Display the latest topics from a specific forum
	 *
	 * @param forumId the id of the forum to show
	 */
	public void forumTopics(int forumId) {
		if (!this.isRSSEnabled() || !this.userSession.getRoleManager().isForumAllowed(forumId)) {
			this.result.forwardTo(MessageController.class).accessDenied();
		}
		else {
			String contents = this.rssService.forForum(forumId);
			this.result.include("contents", contents);
			this.result.forwardTo(Actions.RSS);
		}
	}

	private boolean isRSSEnabled() {
		return this.config.getBoolean(ConfigKeys.RSS_ENABLED);
	}
}

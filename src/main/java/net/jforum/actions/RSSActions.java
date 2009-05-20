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

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.UserSession;
import net.jforum.services.RSSService;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.RSS)
public class RSSActions {
	private ViewPropertyBag propertyBag;
	private ViewService viewService;
	private RSSService rssService;
	private UserSession userSession;
	private JForumConfig config;

	public RSSActions(ViewPropertyBag propertyBag, ViewService viewService,
		RSSService rssService, UserSession userSession, JForumConfig config) {
		this.viewService = viewService;
		this.propertyBag = propertyBag;
		this.rssService = rssService;
		this.userSession = userSession;
		this.config = config;
	}

	/**
	 * Display the latest topics from a specific forum
	 * @param forumId the id of the forum to show
	 */
	public void forumTopics(@Parameter(key = "forumId") int forumId) {
		if (!this.isRSSEnabled() || !userSession.getRoleManager().isForumAllowed(forumId)) {
			viewService.renderView(Actions.ACCESS_DENIED);
		}
		else {
			String contents = rssService.forForum(forumId, viewService);
			propertyBag.put("contents", contents);
			viewService.renderView(Actions.RSS);
		}
	}

	private boolean isRSSEnabled() {
		return config.getBoolean(ConfigKeys.RSS_ENABLED);
	}
}

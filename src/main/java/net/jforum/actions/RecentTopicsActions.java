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

import java.util.List;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.RecentTopicsRepository;
import net.jforum.security.TopicFilter;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Component;

/**
 * @author Rafael Steil
 */
@Component(Domain.RECENT_TOPICS)
public class RecentTopicsActions {
	private final RecentTopicsRepository repository;
	private final ViewPropertyBag propertyBag;
	private final JForumConfig config;
	private final ViewService viewService;
	private final UserSession userSession;

	public RecentTopicsActions(RecentTopicsRepository repository, ViewPropertyBag propertyBag, JForumConfig config,
			ViewService viewService, UserSession userSession) {
		this.repository = repository;
		this.propertyBag = propertyBag;
		this.config = config;
		this.viewService = viewService;
		this.userSession = userSession;
	}

	public void listNew() {
		this.list(this.repository.getNewTopics(this.config.getInt(ConfigKeys.RECENT_TOPICS)), "recentTopicsNew");
	}

	public void listUpdated() {
		this.list(this.repository.getUpdatedTopics(this.config.getInt(ConfigKeys.RECENT_TOPICS)), "recentTopicsUpdated");
	}

	public void listHot() {
		this.list(this.repository.getHotTopics(this.config.getInt(ConfigKeys.RECENT_TOPICS)), "recentTopicsHot");
	}

	private void list(List<Topic> topics, String key) {
		TopicFilter filter = new TopicFilter();

		this.propertyBag.put("topics", filter.filter(topics, this.userSession.getRoleManager()));
		this.propertyBag.put("recentTopicsSectionKey", key);

		this.viewService.renderView(Actions.LIST);
	}
}

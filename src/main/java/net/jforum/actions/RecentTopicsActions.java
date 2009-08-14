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

import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Topic;
import net.jforum.repository.RecentTopicsRepository;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Component;

/**
 * @author Rafael Steil
 */
@Component("recentTopics")
public class RecentTopicsActions {
	private RecentTopicsRepository repository;
	private ViewPropertyBag propertyBag;
	private JForumConfig config;
	private final ViewService viewService;

	public RecentTopicsActions(RecentTopicsRepository repository,
		ViewPropertyBag propertyBag,
		JForumConfig config, ViewService viewService) {
		this.repository = repository;
		this.propertyBag = propertyBag;
		this.config = config;
		this.viewService = viewService;
	}

	public void listNew() {
		list(this.repository.getNewTopics(this.config.getInt(ConfigKeys.RECENT_TOPICS)), "recentTopicsNew");
	}
	public void listUpdated() {
		list(this.repository.getUpdatedTopics(this.config.getInt(ConfigKeys.RECENT_TOPICS)), "recentTopicsUpdated");
	}
	public void listHot() {
		list(this.repository.getHotTopics(this.config.getInt(ConfigKeys.RECENT_TOPICS)), "recentTopicsHot");
	}

	private void list(List<Topic> topics, String key) {
		this.propertyBag.put("topics", topics);
		this.propertyBag.put("sessionTitleKey", key);

		viewService.renderView("list");
	}
}

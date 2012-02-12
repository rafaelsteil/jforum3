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

import java.util.List;

import net.jforum.actions.helpers.Domain;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.RecentTopicsRepository;
import net.jforum.security.TopicFilter;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.RECENT_TOPICS)
public class RecentTopicsController {
	private final RecentTopicsRepository repository;
	private final JForumConfig config;
	private final UserSession userSession;
	private final Result result;

	public RecentTopicsController(RecentTopicsRepository repository, JForumConfig config, UserSession userSession, Result result) {
		this.repository = repository;
		this.config = config;
		this.userSession = userSession;
		this.result = result;
	}

	public void list() {

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

		this.result.include("topics", filter.filter(topics, this.userSession.getRoleManager()));
		this.result.include("recentTopicsSectionKey", key);

		result.of(this).list();
	}
}

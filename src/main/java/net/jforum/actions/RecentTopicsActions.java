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

import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.repository.RecentTopicsRepository;
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

	public RecentTopicsActions(RecentTopicsRepository repository,
		ViewPropertyBag propertyBag,
		JForumConfig config) {
		this.repository = repository;
		this.propertyBag = propertyBag;
		this.config = config;
	}

	public void list() {
		propertyBag.put("topics", repository.getRecentTopics(
			config.getInt(ConfigKeys.RECENT_TOPICS)));
	}
}

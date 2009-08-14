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
package net.jforum.actions.extensions;

import net.jforum.actions.TopicActions;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.security.AuthenticatedRule;
import net.jforum.services.TopicWatchService;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * Topic watch extension for {@link TopicActions}
 * @author Rafael Steil
 */
@Component(Domain.TOPICS)
@ActionExtension(Domain.TOPICS)
@InterceptedBy(MethodSecurityInterceptor.class)
public class TopicWatchExtension {
	private ViewPropertyBag propertyBag;
	private SessionManager sessionManager;
	private ViewService viewService;
	private TopicWatchService watchService;

	public TopicWatchExtension(ViewPropertyBag propertyBag, SessionManager sessionManager,
		ViewService viewService, TopicWatchService watchService) {
		this.propertyBag = propertyBag;
		this.sessionManager = sessionManager;
		this.viewService = viewService;
		this.watchService = watchService;
	}

	@Extends(Actions.LIST)
	public void afterList() {
		UserSession userSession = this.sessionManager.getUserSession();
		Topic topic = (Topic)this.propertyBag.get("topic");

		this.propertyBag.put("isUserWatchingTopic", userSession.isLogged()
			? this.watchService.isUserSubscribed(topic, userSession.getUser())
			: false);
	}

	/**
	 * Makes the current logged user watch a specific topic.
	 * @param topicId the id of the topic to watch
	 */
	@SecurityConstraint(value = AuthenticatedRule.class, displayLogin = true)
	public void watch(@Parameter(key = "page") int page, @Parameter(key = "topicId") int topicId) {
		Topic topic = new Topic(); topic.setId(topicId);

		UserSession userSession = this.sessionManager.getUserSession();

		this.watchService.watch(topic, userSession.getUser());
		this.viewService.redirectToAction(Actions.LIST, topicId);
	}

	/**
	 * Makes the current user to unwatch a specific topic
	 * @param topicId the id of the topic to unwatch
	 */
	@SecurityConstraint(value = AuthenticatedRule.class, displayLogin = true)
	public void unwatch(@Parameter(key = "page") int page, @Parameter(key = "topicId") int topicId) {
		Topic topic = new Topic(); topic.setId(topicId);

		UserSession userSession = this.sessionManager.getUserSession();

		this.watchService.unwatch(topic, userSession.getUser());
		this.viewService.redirectToAction(Actions.LIST, topicId);
	}
}

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
import net.jforum.entities.TopicWatch;
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
	private final ViewPropertyBag propertyBag;
	private final SessionManager sessionManager;
	private final ViewService viewService;
	private final TopicWatchService watchService;

	public TopicWatchExtension(ViewPropertyBag propertyBag, SessionManager sessionManager,
		ViewService viewService, TopicWatchService watchService) {
		this.propertyBag = propertyBag;
		this.sessionManager = sessionManager;
		this.viewService = viewService;
		this.watchService = watchService;
	}

	@Extends(Actions.LIST)
	public void afterList() {
		boolean isWatching = false;
		UserSession userSession = this.sessionManager.getUserSession();

		if (userSession.isLogged()) {
			Topic topic = (Topic)this.propertyBag.get("topic");
			TopicWatch subscription = this.watchService.getSubscription(topic, userSession.getUser());
			isWatching = subscription != null;

			if (!subscription.isRead()) {
				subscription.markAsRead();
			}
		}

		this.propertyBag.put("isUserWatchingTopic", isWatching);
	}

	/**
	 * Makes the current logged user watch a specific topic.
	 * @param topicId the id of the topic to watch
	 */
	@SecurityConstraint(value = AuthenticatedRule.class, displayLogin = true)
	public void watch(@Parameter(key = "page") int page, @Parameter(key = "topicId") int topicId) {
		Topic topic = new Topic();
		topic.setId(topicId);

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

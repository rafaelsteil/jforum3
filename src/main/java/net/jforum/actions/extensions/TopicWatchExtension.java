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

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.Topic;
import net.jforum.entities.TopicWatch;
import net.jforum.entities.UserSession;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.security.AuthenticatedRule;
import net.jforum.services.TopicWatchService;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * Topic watch extension for {@link TopicController}
 *
 * @author Rafael Steil
 */
@Resource
@Path(Domain.TOPICS)
@ActionExtension(Domain.TOPICS)
public class TopicWatchExtension {

	private final TopicWatchService watchService;
	private final Result result;
	private final UserSession userSession;

	public TopicWatchExtension(TopicWatchService watchService, Result result, UserSession userSession) {
		this.userSession = userSession;
		this.watchService = watchService;
		this.result = result;
	}

	@Extends(Actions.LIST)
	public void afterList() {
		boolean isWatching = false;
		UserSession userSession = this.userSession;

		if (userSession.isLogged()) {
			Topic topic = (Topic) this.result.included().get("topic");
			TopicWatch subscription = this.watchService.getSubscription(topic,
					userSession.getUser());
			isWatching = subscription != null;

			if (!subscription.isRead()) {
				subscription.markAsRead();
			}
		}

		this.result.include("isUserWatchingTopic", isWatching);
	}

	/**
	 * Makes the current logged user watch a specific topic.
	 *
	 * @param topicId
	 *            the id of the topic to watch
	 */
	@SecurityConstraint(value = AuthenticatedRule.class, displayLogin = true)
	public void watch(int page, int topicId) {
		Topic topic = new Topic();
		topic.setId(topicId);

		UserSession userSession = this.userSession;

		this.watchService.watch(topic, userSession.getUser());
		this.result.redirectTo(Actions.LIST + "/" + topicId);
	}

	/**
	 * Makes the current user to unwatch a specific topic
	 *
	 * @param topicId
	 *            the id of the topic to unwatch
	 */
	@SecurityConstraint(value = AuthenticatedRule.class, displayLogin = true)
	public void unwatch(int page, int topicId) {
		Topic topic = new Topic();
		topic.setId(topicId);

		this.watchService.unwatch(topic, userSession.getUser());
		this.result.redirectTo(this).list(topicId);
	}
	public void list(int topicId) {


	}
}

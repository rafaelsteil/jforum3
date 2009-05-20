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
import net.jforum.services.ViewService;
import net.jforum.util.I18n;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.Parameter;

/**
 * Just display some messages
 * @author Rafael Steil
 */
@Component(Domain.MESSAGES)
public class MessageActions {
	private ViewPropertyBag propertyBag;
	private ViewService viewService;
	private I18n i18n;

	public MessageActions(ViewPropertyBag propertyBag, ViewService viewService, I18n i18n) {
		this.propertyBag = propertyBag;
		this.viewService = viewService;
		this.i18n = i18n;
	}

	/**
	 * Displays an "access is denied" message
	 */
	public void accessDenied() {
		propertyBag.put("message", i18n.getMessage("Message.accessDenied"));
		viewService.renderView(Actions.MESSAGE);
	}

	/**
	 * Displays a "waiting moderation" message for newly created topics in moderated forums
	 * @param forumId
	 */
	public void topicWaitingModeration(@Parameter(key = "forumId") int forumId) {
		propertyBag.put("message", i18n.getFormattedMessage("PostShow.waitingModeration",
			i18n.params(viewService.buildUrl(Domain.FORUMS, Actions.SHOW, forumId))));

		viewService.renderView(Actions.MESSAGE);
	}

	/**
	 * Displays a "waiting moderation" message for replies in topics of moderated forums
	 * @param forumId
	 */
	public void replyWaitingModeration(@Parameter(key = "topicId") int topicId) {
		propertyBag.put("message", i18n.getFormattedMessage("PostShow.waitingModeration",
				i18n.params(viewService.buildUrl(Domain.TOPICS, Actions.LIST, topicId))));

		viewService.renderView(Actions.MESSAGE);
	}
}

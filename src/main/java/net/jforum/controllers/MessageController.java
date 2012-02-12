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
import net.jforum.util.I18n;
import net.jforum.util.URLBuilder;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * Just display some messages
 *
 * @author Rafael Steil
 */
@Resource
@Path(Domain.MESSAGES)
public class MessageController {
	private I18n i18n;
	private final Result result;

	public MessageController(I18n i18n, Result result) {
		this.i18n = i18n;
		this.result = result;
	}

	/**
	 * Displays an "access is denied" message
	 */
	public void accessDenied() {
		this.result.include("message", this.i18n.getMessage("Message.accessDenied"));
		result.of(this).message();
	}

	public void message() {

	}

	/**
	 * Displays a "waiting moderation" message for newly created topics in
	 * moderated forums
	 *
	 * @param forumId
	 */
	public void topicWaitingModeration(int forumId) {
		this.result.include("message", this.i18n.getFormattedMessage(
			"PostShow.waitingModeration", URLBuilder.build(Domain.FORUMS, Actions.SHOW, forumId)));
		result.of(this).message();
	}

	/**
	 * Displays a "waiting moderation" message for replies in topics of
	 * moderated forums
	 *
	 * @param forumId
	 */
	public void replyWaitingModeration(int topicId) {
		this.result.include("message", this.i18n.getFormattedMessage("PostShow.waitingModeration",
			URLBuilder.build(Domain.TOPICS, Actions.LIST, topicId)));
		result.of(this).message();
	}
}

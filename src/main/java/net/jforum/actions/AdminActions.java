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

import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.repository.ForumRepository;
import net.jforum.security.AdministrationRule;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;

/**
 * @author Rafael Steil
 */
@Component(Domain.ADMIN)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class AdminActions {
	private final SessionManager sessionManager;
	private final ViewPropertyBag propertyBag;
	private final ForumRepository forumRepository;

	public AdminActions(SessionManager sessionManager,ViewPropertyBag propertyBag, ForumRepository forumRepository) {
		this.sessionManager = sessionManager;
		this.propertyBag = propertyBag;
		this.forumRepository = forumRepository;
	}

	/**
	 * Shows the main administration page (for logged users)
	 */
	public void index() { }

	/**
	 * The left navigation menu
	 */
	public void menu() {

	}

	/**
	 * The main admin page
	 */
	public void main() {
		this.propertyBag.put("stats", this.forumRepository.getForumStats());
		this.propertyBag.put("sessions", this.sessionManager.getLoggedSessions());
		this.propertyBag.put("totalLoggedUsers", this.sessionManager.getTotalLoggedUsers());
	}
}
